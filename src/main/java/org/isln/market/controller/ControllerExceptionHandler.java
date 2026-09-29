package org.isln.market.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(Exception.class)
    public String handle(Exception error, Model model) {
        String message = error.getMessage();
        model.addAttribute("message", message == null ? "Что то пошло не так" : message);
        return "error";
    }
}
