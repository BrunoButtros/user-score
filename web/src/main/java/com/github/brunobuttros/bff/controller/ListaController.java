package com.github.brunobuttros.bff.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.brunobuttros.bff.dto.UserListDTO;
import com.github.brunobuttros.bff.service.ListaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ListaController {

    private final ListaService listaService;

    @Autowired
    public ListaController(ListaService listaService) {
        this.listaService = listaService;
    }

    @GetMapping("/lista")
    public String listarNomesEScores(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null || token.isEmpty()) {
            return "redirect:/login";
        }

        try {
            List<UserListDTO> userScores = listaService.getAllUserScores(session);
            model.addAttribute("userScores", userScores);
            return "lista";
        } catch (HttpClientErrorException erx) {
            HttpStatusCode statusCode = erx.getStatusCode();
            if (statusCode.value() == HttpStatus.UNAUTHORIZED.value() || statusCode.value() == HttpStatus.FORBIDDEN.value()) {
                session.invalidate();
                return "redirect:/login";
            }
            model.addAttribute("error", "Failed to retrieve user scores: " + erx.getMessage());
            return "error";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to retrieve user scores: " + e.getMessage());
            return "error";
        }
    }
}
