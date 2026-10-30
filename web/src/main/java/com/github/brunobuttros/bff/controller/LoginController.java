package com.github.brunobuttros.bff.controller;

import com.github.brunobuttros.bff.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping({"/", "/login"})
    public String loginForm(Model model) {
        model.addAttribute("error", "");
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String login, @RequestParam String password, Model model, HttpSession session, jakarta.servlet.http.HttpServletRequest request) {
        try {
            String token = loginService.authenticate(login, password);
            request.changeSessionId();
            session.setAttribute("token", token);
            return "redirect:/lista"; // redireciona para página após login bem sucedido
        } catch (RuntimeException e) {
            model.addAttribute("error", "Não foi possível entrar. Verifique as credenciais e tente novamente.");
            return "login"; // retorna para página de login com mensagem de erro
        }
    }
}
