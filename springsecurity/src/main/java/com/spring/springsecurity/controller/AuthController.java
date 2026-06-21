package com.spring.springsecurity.controller;

import com.spring.springsecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
@RequiredArgsConstructor
@Controller
public class AuthController {
private final UserService userService;
@GetMapping("/login")
public String loginPage(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
    if (error != null)  model.addAttribute("error",  "Invalid username or password or salary.");
    if (logout != null) model.addAttribute("message", "You have been logged out.");
    return "login";   // → templates/login.html
}
    @GetMapping("/register")
    public String registerPage() {
        return "register";   // → templates/register.html
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam Long salary,
                               Model model) {
        try {
            userService.register(username, password,salary);
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
    @GetMapping("/home")
    public String homePage(Model model,
                           org.springframework.security.core.Authentication auth) {
        model.addAttribute("username", auth.getName());
        return "home";   // → templates/home.html
    }
}
