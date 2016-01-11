package com.teamdev.web.controller;

import com.teamdev.chat.service.impl.exception.AuthenticationException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
public final class ExceptionHandlingController {

    private static final Logger LOG = Logger.getLogger(ExceptionHandlingController.class);

    @ExceptionHandler(AuthenticationException.class)
    public void exceptionHandle() {
        LOG.error("AuthenticationException -----------------------------");
    }
}