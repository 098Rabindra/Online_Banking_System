package com.bank.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Open Login only from Main Page
    @GetMapping("/open-login")
    public String openLogin(HttpSession session) {

        session.setAttribute("allowLogin", true);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {

        Boolean allow =
                (Boolean) session.getAttribute("allowLogin");

        if (allow == null || !allow) {
            return "redirect:/main_page.html";
        }

        session.removeAttribute("allowLogin");

        return "login";
    }

    // Open Register only from Login Page
    @GetMapping("/open-register")
    public String openRegister(HttpSession session) {

        session.setAttribute("allowRegistration", true);

        return "redirect:/register";
    }

    @GetMapping("/register")
    public String register(HttpSession session) {

        Boolean allow =
                (Boolean) session.getAttribute("allowRegistration");

        if (allow == null || !allow) {
            return "redirect:/login";
        }

        session.removeAttribute("allowRegistration");

        return "register";
    }
}