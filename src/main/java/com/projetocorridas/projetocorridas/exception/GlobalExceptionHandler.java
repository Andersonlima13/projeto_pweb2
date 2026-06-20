package com.projetocorridas.projetocorridas.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(NoResourceFoundException.class)
        public String tratar404(
                        NoResourceFoundException ex,
                        Model model) {

                model.addAttribute(
                                "mensagemErro",
                                "Página não encontrada.");

                return "error/404";
        }

        @ExceptionHandler(Exception.class)
        public String tratarGenerico(
                        Exception ex,
                        Model model) {

                model.addAttribute(
                                "mensagemErro",
                                "Erro interno.");

                return "error/erro";
        }
}