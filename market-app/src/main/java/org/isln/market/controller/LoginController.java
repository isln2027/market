package org.isln.market.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(
            @RequestParam Map<String, String> parameters,
            Model model) {
        if (parameters.containsKey("error")) {
            model.addAttribute("error", true);
        }
        if (parameters.containsKey("logout")) {
            model.addAttribute("logout", true);
        }
        return "login";
    }
}