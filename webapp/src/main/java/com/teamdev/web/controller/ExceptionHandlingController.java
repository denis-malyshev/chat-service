package com.teamdev.web.controller;

import com.teamdev.chat.service.impl.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public final class ExceptionHandlingController {

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public String authenticationException(AuthenticationException e) {
        return e.getMessage();
    }

    @ResponseStatus(code = HttpStatus.CONFLICT)
    @ExceptionHandler(RegistrationException.class)
    @ResponseBody
    public String registrationException(RegistrationException e) {
        return e.getMessage();
    }

    @ResponseStatus(code = HttpStatus.CONFLICT)
    @ExceptionHandler(ChatRoomAlreadyExistsException.class)
    @ResponseBody
    public String chatRoomAlreadyExistsException(ChatRoomAlreadyExistsException e) {
        return e.getMessage();
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(ChatRoomNotFoundException.class)
    @ResponseBody
    public String chatRoomNotFoundException(ChatRoomNotFoundException e) {
        return e.getMessage();
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public String userNotFoundException(UserNotFoundException e) {
        return e.getMessage();
    }
}
