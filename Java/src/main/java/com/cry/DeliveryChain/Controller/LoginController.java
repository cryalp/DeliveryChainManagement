package com.cry.DeliveryChain.Controller;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.cry.DeliveryChain.Core.Functions;
import com.cry.DeliveryChain.Entity.UserAccount;

@Controller
@RequestMapping(value = "/Login")
public class LoginController {
    RESTService restService;
    Functions _functions;

    public LoginController() {}

    @Autowired
    public LoginController(RESTService RESTService, Functions Functions) {
        this.restService = RESTService;
        this._functions = Functions;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String Index(HttpSession httpSession, Model model, HttpServletRequest httpRequest) {
        try {
            var notification = httpRequest.getParameter("Notification");
            if (notification != null && notification.length() > 64) {
                notification = notification.substring(0, 64);
            }
            model.addAttribute("Notification", notification);

            return IsLoggedIn(httpSession) ? "redirect:/" : "Login/Index";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "Login/Index";
        }
    }

    @RequestMapping(value = "/Save", method = RequestMethod.POST)
    public String Save(HttpSession httpSession, RedirectAttributes redirectAttributes, HttpServletRequest httpRequest) {
        try {
            var username = httpRequest.getParameter("Username");
            var password = httpRequest.getParameter("Password");

            var userAccount = restService.FindUserAccountByUsername(username);
            if (userAccount == null || !BCrypt.checkpw(password, userAccount.Password) || !userAccount.IsActive) {
                redirectAttributes.addAttribute("Notification", "Bu hesap mevcut de??il ya da ??ifre e??le??miyor.");
                return "redirect:/Login";
            }

            httpSession.setAttribute("LOGIN_UniqueId", userAccount.UniqueId);
            return "redirect:/";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/Login/";
        }
    }

    @RequestMapping(value = "/Logout", method = RequestMethod.GET)
    public String Logout(HttpSession httpSession) {
        try {
            httpSession.invalidate();
            return "redirect:/Login/";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/Login/";
        }
    }

    @RequestMapping(value = "/Register", method = RequestMethod.GET)
    public String Register(Model model, HttpServletRequest httpRequest) {
        try {
            var notification = httpRequest.getParameter("Notification");
            if (notification != null && notification.length() > 64) {
                notification = notification.substring(0, 64);
            }
            model.addAttribute("Notification", notification);

            return "Login/Register/Index";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/Login/";
        }
    }

    @RequestMapping(value = "/Register", method = RequestMethod.POST)
    public String Register(RedirectAttributes redirectAttributes, HttpServletRequest httpRequest) {
        try {
            String email = httpRequest.getParameter("Email");
            String username = httpRequest.getParameter("Username");
            String password = httpRequest.getParameter("Password");
            String passwordConfirm = httpRequest.getParameter("PasswordConfirm");
            String accountType = httpRequest.getParameter("AccountType");

            if (!password.equals(passwordConfirm)) {
                redirectAttributes.addAttribute("Notification", "??ifreler e??le??miyor.");
                return "redirect:/Login/Register/";
            }
            if (!accountType.equals("Supplier") && !accountType.equals("Seller")) {
                redirectAttributes.addAttribute("Notification", "Yanl???? hesap tipi.");
                return "redirect:/Login/Register/";
            }

            var userAccount = restService.FindUserAccountByUsernameOrEmail(username, email);
            if (userAccount != null) {
                redirectAttributes.addAttribute("Notification", "Bu hesap zaten kay??tl??.");
                return "redirect:/Login/Register/";
            }

            var photoBase64 =
                    "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAQAAAAAYLlVAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAAmJLR0QA/4ePzL8AAAAHdElNRQfkAxgQICe/pMQtAAADZklEQVRo3u2ZS0hUURjHf3dGHc1X4ytLkUxL3TgliUgJFm60jSCRgZtcRA8iCNpGFEi6qUVCRIuwhVEE7UqCqKiwjNRMkdJeYva0JsdRR53bIjHvODP3nDv3uvL/LWfu9/+dxz3nfOfCqlYVmRRskSYw9lQaLlzk4sSOh1EG6OY9syvT6jxO0YMXdUnMMUIbu4mx2jyWRgbxa8z/x29aybHS3smFgJYvj6eUWmW/livM69irqLymzAr7GFqYE7BXUeki33yAeiYE7VVU2ogz134DLyTsVbzsNRfgiNDoL40OEsyzj+eepL2KmwqR1GIL6SaKpaGT2GkeQAGpBvrNhd0sgByRVMuULfImiAEkG7CHBJGdIcLNNHKJAbgN5Z7EZxbACPMGAEaZMgvgDeMGAHpFsMUAhumTtp/gscjfxAA83EaVBHjOS2noMMqmW2ohnmK/mfYADUxKALQTbzaAg/PCO2I3hWbbA6TQJoQwyA4r7AFSaWVa9zhWLpNSb5NxUko1tZTzkx94ecAXinCG+LeHaxynD3BSzz62kc4ME9Jv0ILSOEYnEwtVwDBHSQIUCmiiP6An5hnjBtU4ABsV3GUWFRUf77iIK3QFFro0K6GFSk0PzXKHJrrwo5BJCSXk4sTGJKP008UQM0AmhzhMhibXCGe5Kle4bacv6AiP0Uwx0Yv4UUQtLmYKmRzgWdCJ6uGEzJkii0dhptkY7TRQSOKCtUIsWVTRzCt8IZ/6RZ3oENg4x0kdxDm+84ERxvGTwHo2ksUanWd6qOWjSPvL+Cp9BhaLMyL2Ni5ZZK8ytLxkW74bbqZGfLJIKo89+gCVZFsGADWBMyUQwM4ug9c2YiomNzxAhoEaSEbpuMIDbCTLUgA7W8MDbCHRUgAo0pYrgQD5ls4AgBxtE7UAirV3XACkkRIaIIZ1lgMkaE8TgQDGylAZBXhoAaLNvloKoijtedkW8GO0VDIjsoV7CxRDFxHyfRASQMW/AgCaklULMG3wJkBGPm2lrQXwMGw5wDifQgP4eWj5IPSGA4AOBiy193Fd796kUaoKlo2bJOkxOjit+1HCaNwnT6SbHBzkbcjPMkbDzeVgW13wzVchjzqqyCdZu2xIS8GPl890cosnzIgC/FMcGaTgiAgA5vnDN9wrssStyoj+AhbAtJAsggKZAAAAJXRFWHRkYXRlOmNyZWF0ZQAyMDIwLTAzLTI0VDE2OjMyOjM5KzAwOjAwFC32vAAAACV0RVh0ZGF0ZTptb2RpZnkAMjAyMC0wMy0yNFQxNjozMjozOSswMDowMGVwTgAAAAAASUVORK5CYII";

            var subject = "Tedarik Zinciri Y??ntemi - Kay??t";

            var userAccountUniqueId = UUID.randomUUID();
            var message = "Hesab??n??z?? onaylamak i??in ba??lant??ya t??klay??n??z. E??er aktif olarak t??klanam??yorsa taray??c??n??z??n URL k??sm??nda arat??n.\nBa??lant??n??z:\n";
            var link = "https://deliverychainmanagement.cryalp.com/Login/UserConfirmation?UniqueId=" + userAccountUniqueId;
            var messageBody = message + link;

            _functions.SendMail(email, subject, messageBody);

            userAccount = new UserAccount(email, username, BCrypt.hashpw(password, BCrypt.gensalt(10)), accountType, LocalDateTime.now(), photoBase64, false,
                    userAccountUniqueId);
            restService.SaveUserAccount(userAccount);

            redirectAttributes.addAttribute("Notification", "Hesap ba??ar?? ile olu??turuldu. L??tfen mailinizi kontrol edin.");
            return "redirect:/Login/";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());

            redirectAttributes.addAttribute("Notification", "Hesap olu??turulurken hata ile kar????la????ld??.");
            return "redirect:/Login/Register";
        }
    }

    @RequestMapping(value = "/UserConfirmation", method = RequestMethod.GET)
    public String UserConfirmation(RedirectAttributes redirectAttributes, HttpServletRequest httpRequest) {
        try {
            var uniqueId = httpRequest.getParameter("UniqueId");
            var userAccount = restService.FindUserAccountByUniqueId(uniqueId);
            userAccount.IsActive = true;
            restService.SaveUserAccount(userAccount);

            redirectAttributes.addAttribute("Notification", "Hesab??n??z ba??ar?? ile aktifle??tirildi.");
            return "redirect:/Login/";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            redirectAttributes.addAttribute("Notification", "Aktifle??tirme s??ras??nda hata ile kar????la????ld??.");
            return "redirect:/Login/Register";
        }
    }

    public String GetSessionUserUniqueId(HttpSession httpSession) {
        return httpSession.getAttribute("LOGIN_UniqueId").toString();
    }

    public Boolean IsLoggedIn(HttpSession httpSession) {
        try {
            if (httpSession.getAttribute("LOGIN_UniqueId") == null) {
                return false;
            }

            var userAccountUniqueId = httpSession.getAttribute("LOGIN_UniqueId");
            var userAccount = restService.FindUserAccountByUniqueId(userAccountUniqueId.toString());
            if (!userAccount.IsActive) {
                return false;
            }
            return true;
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return false;
        }
    }
}
