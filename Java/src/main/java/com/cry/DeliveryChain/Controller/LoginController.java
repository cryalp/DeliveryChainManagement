package com.cry.DeliveryChain.Controller;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.cry.DeliveryChain.Core.Functions;
import com.cry.DeliveryChain.Entity.UserAccount;
import com.cry.DeliveryChain.Repository.UserAccountRepo;

@Controller
@RequestMapping(value = "/Login")
public class LoginController {
    UserAccountRepo userAccountRepo;

    Functions _functions;

    @Value("${App.Username}")
    private String appUsername;
    @Value("${App.Password}")
    private String appPassword;

    public LoginController() {}

    @Autowired
    public LoginController(UserAccountRepo UserAccountRepo, Functions Functions) {
        this.userAccountRepo = UserAccountRepo;
        this._functions = Functions;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String Index(HttpSession httpSession, Model model, String Notification) {
        try {
            if (Notification != null && Notification.length() > 64) {
                Notification = Notification.substring(0, 64);
            }
            model.addAttribute("Notification", Notification);

            return IsLoggedIn(httpSession) ? "redirect:/" : "Login/Index";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "Login/Index";
        }
    }

    @RequestMapping(value = "/Save", method = RequestMethod.POST)
    public String Save(HttpSession httpSession, RedirectAttributes redirectAttributes, String Username, String Password) {
        try {
            var userAccount = userAccountRepo.findByUsername(Username);
            if (userAccount == null || !BCrypt.checkpw(Password, userAccount.Password) || !userAccount.IsActive) {
                redirectAttributes.addAttribute("Notification", "Bu hesap mevcut değil ya da şifre eşleşmiyor.");
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
    public String Register(Model model, String Notification) {
        try {
            if (Notification != null && Notification.length() > 64) {
                Notification = Notification.substring(0, 64);
            }

            model.addAttribute("Notification", Notification);
            return "/Login/Register/Index";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/Login/";
        }
    }

    @RequestMapping(value = "/Register", method = RequestMethod.POST)
    public String Register(RedirectAttributes redirectAttributes, String Email, String Username, String Password, String PasswordConfirm, String AccountType,
            MultipartFile Photo) {
        try {
            if (!Password.equals(PasswordConfirm)) {
                redirectAttributes.addAttribute("Notification", "Şifreler eşleşmiyor.");
                return "redirect:/Login/Register/";
            }
            if (!AccountType.equals("Supplier") && !AccountType.equals("Seller")) {
                redirectAttributes.addAttribute("Notification", "Yanlış hesap tipi.");
                return "redirect:/Login/Register/";
            }

            var userAccount = userAccountRepo.findByUsernameOrEmail(Username, Email);
            if (userAccount != null) {
                redirectAttributes.addAttribute("Notification", "Bu hesap zaten kayıtlı.");
                return "redirect:/Login/Register/";
            }

            var photoBase64 =
                    "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAQAAAAAYLlVAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAAmJLR0QA/4ePzL8AAAAHdElNRQfkAxgQICe/pMQtAAADZklEQVRo3u2ZS0hUURjHf3dGHc1X4ytLkUxL3TgliUgJFm60jSCRgZtcRA8iCNpGFEi6qUVCRIuwhVEE7UqCqKiwjNRMkdJeYva0JsdRR53bIjHvODP3nDv3uvL/LWfu9/+dxz3nfOfCqlYVmRRskSYw9lQaLlzk4sSOh1EG6OY9syvT6jxO0YMXdUnMMUIbu4mx2jyWRgbxa8z/x29aybHS3smFgJYvj6eUWmW/livM69irqLymzAr7GFqYE7BXUeki33yAeiYE7VVU2ogz134DLyTsVbzsNRfgiNDoL40OEsyzj+eepL2KmwqR1GIL6SaKpaGT2GkeQAGpBvrNhd0sgByRVMuULfImiAEkG7CHBJGdIcLNNHKJAbgN5Z7EZxbACPMGAEaZMgvgDeMGAHpFsMUAhumTtp/gscjfxAA83EaVBHjOS2noMMqmW2ohnmK/mfYADUxKALQTbzaAg/PCO2I3hWbbA6TQJoQwyA4r7AFSaWVa9zhWLpNSb5NxUko1tZTzkx94ecAXinCG+LeHaxynD3BSzz62kc4ME9Jv0ILSOEYnEwtVwDBHSQIUCmiiP6An5hnjBtU4ABsV3GUWFRUf77iIK3QFFro0K6GFSk0PzXKHJrrwo5BJCSXk4sTGJKP008UQM0AmhzhMhibXCGe5Kle4bacv6AiP0Uwx0Yv4UUQtLmYKmRzgWdCJ6uGEzJkii0dhptkY7TRQSOKCtUIsWVTRzCt8IZ/6RZ3oENg4x0kdxDm+84ERxvGTwHo2ksUanWd6qOWjSPvL+Cp9BhaLMyL2Ni5ZZK8ytLxkW74bbqZGfLJIKo89+gCVZFsGADWBMyUQwM4ug9c2YiomNzxAhoEaSEbpuMIDbCTLUgA7W8MDbCHRUgAo0pYrgQD5ls4AgBxtE7UAirV3XACkkRIaIIZ1lgMkaE8TgQDGylAZBXhoAaLNvloKoijtedkW8GO0VDIjsoV7CxRDFxHyfRASQMW/AgCaklULMG3wJkBGPm2lrQXwMGw5wDifQgP4eWj5IPSGA4AOBiy193Fd796kUaoKlo2bJOkxOjit+1HCaNwnT6SbHBzkbcjPMkbDzeVgW13wzVchjzqqyCdZu2xIS8GPl890cosnzIgC/FMcGaTgiAgA5vnDN9wrssStyoj+AhbAtJAsggKZAAAAJXRFWHRkYXRlOmNyZWF0ZQAyMDIwLTAzLTI0VDE2OjMyOjM5KzAwOjAwFC32vAAAACV0RVh0ZGF0ZTptb2RpZnkAMjAyMC0wMy0yNFQxNjozMjozOSswMDowMGVwTgAAAAAASUVORK5CYII";
            if (Photo != null) {
                photoBase64 = "data:" + Photo.getContentType() + ";base64," + new String(Base64.getEncoder().encode(Photo.getBytes()));
            }

            var subject = "Tedarik Zinciri Yöntemi - Kayıt";

            var userAccountUniqueId = UUID.randomUUID();
            var message = "Hesabınızı onaylamak için bağlantıya tıklayınız. Eğer aktif olarak tıklanamıyorsa tarayıcınızın URL kısmında aratın.\nBağlantınız:\n";
            var link = "https://localhost/Login/UserConfirmation?UniqueId=" + userAccountUniqueId;
            var messageBody = message + link;

            _functions.SendMail(Email, subject, messageBody);

            userAccount = new UserAccount(Email, Username, BCrypt.hashpw(Password, BCrypt.gensalt(10)), AccountType, LocalDateTime.now(), photoBase64, false,
                    userAccountUniqueId);
            userAccountRepo.save(userAccount);

            redirectAttributes.addAttribute("Notification", "Hesap başarı ile oluşturuldu. Lütfen mailinizi kontrol edin.");
            return "redirect:/Login/";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());

            redirectAttributes.addAttribute("Notification", "Hesap oluşturulurken hata ile karşılaşıldı.");
            return "redirect:/Login/Register";
        }
    }

    @RequestMapping(value = "/UserConfirmation", method = RequestMethod.GET)
    public String UserConfirmation(RedirectAttributes redirectAttributes, String UniqueId) {
        try {
            var userAccount = userAccountRepo.findByUniqueId(UUID.fromString(UniqueId));
            userAccount.IsActive = true;
            userAccountRepo.save(userAccount);

            redirectAttributes.addAttribute("Notification", "Hesabınız başarı ile aktifleştirildi.");
            return "redirect:/Login/";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            redirectAttributes.addAttribute("Notification", "Aktifleştirme sırasında hata ile karşılaşıldı.");
            return "redirect:/Login/Register";
        }
    }

    public UUID GetSessionUserUniqueId(HttpSession httpSession) {
        return UUID.fromString(httpSession.getAttribute("LOGIN_UniqueId").toString());
    }

    public Boolean IsLoggedIn(HttpSession httpSession) {
        try {
            if (httpSession.getAttribute("LOGIN_UniqueId") == null) {
                return false;
            }

            var userAccountUniqueId = httpSession.getAttribute("LOGIN_UniqueId");
            var userAccount = userAccountRepo.findByUniqueId(UUID.fromString(userAccountUniqueId.toString()));
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
