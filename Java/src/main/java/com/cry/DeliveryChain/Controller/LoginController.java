package com.cry.DeliveryChain.Controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.cry.DeliveryChain.Core.Functions;

@Controller
@RequestMapping(value = "/Login")
public class LoginController {

    @Value("${App.Username}")
    private String appUsername;

    @Value("${App.Password}")
    private String appPassword;

    private Functions _functions = new Functions();

    public LoginController() {}

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String Index(HttpSession httpSession) {
        try {
            return IsLoggedIn(httpSession) ? "redirect:/" : "Login/Index";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "Login/Index";
        }
    }

    @RequestMapping(value = "/Save", method = RequestMethod.POST)
    public String Save(HttpSession httpSession, String Username, String Password) {
        try {
            if (Username.equals(appUsername) && Password.equals(appPassword)) {
                httpSession.setAttribute("LOGIN", "LOGGED_IN");
                return "redirect:/";
            }
            return "redirect:/Login/";
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
    public String Register(HttpSession httpSession) {
        try {
            httpSession.invalidate();
            return "/Login/Register/Index";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/Login/";
        }
    }

    @RequestMapping(value = "/Register", method = RequestMethod.POST)
    public String Register(String Username, String Password, String PasswordConfirm) {
        try {
            

            return "/Login/Register/Index";
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return "redirect:/Login/";
        }
    }

    public Boolean IsLoggedIn(HttpSession session) {
        try {
            return session.getAttribute("LOGIN").equals("LOGGED_IN");
        }
        catch (Exception e) {
            _functions.Logger(e.getMessage());
            return false;
        }
    }
}
