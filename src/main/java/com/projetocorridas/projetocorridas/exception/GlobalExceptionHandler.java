package com.projetocorridas.projetocorridas.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String tratarErroGenerico(
            Exception ex,
            Model model) {

        model.addAttribute(
                "mensagemErro",
                "Ocorreu um erro inesperado.");

        model.addAttribute(
                "detalhesErro",
                ex.getMessage());

        return "error/erro";
    }
}