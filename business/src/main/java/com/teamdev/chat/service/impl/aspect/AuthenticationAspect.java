package com.teamdev.chat.service.impl.aspect;

import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthenticationAspect {

    @Autowired
    private AuthenticationService authenticationService;

    @Pointcut("execution (* com.teamdev.chat.service.*.*" +
            "(com.teamdev.chat.service.impl.dto.Token,com.teamdev.chat.service.impl.dto.UserId,..)) &&" +
            " !execution(* validate(..))")
    private void authPointcut() {
    }

    @Around("authPointcut()")
    public void authentication(ProceedingJoinPoint joinPoint) throws Throwable {

        Token token = (Token) joinPoint.getArgs()[0];
        UserId userId = (UserId) joinPoint.getArgs()[1];

        authenticationService.validate(token, userId);

        joinPoint.proceed();
    }
}
