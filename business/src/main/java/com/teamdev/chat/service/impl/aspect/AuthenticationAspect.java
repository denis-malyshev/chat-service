package com.teamdev.chat.service.impl.aspect;

import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chatservice.wrappers.dto.Token;
import com.teamdev.chatservice.wrappers.dto.UserId;
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
            "(com.teamdev.chatservice.wrappers.dto.Token,com.teamdev.chatservice.wrappers.dto.UserId,..)) &&" +
            " !execution(* validate(..))")
    private void authPointcut() {
    }

    @Around("authPointcut()")
    public Object authentication(ProceedingJoinPoint joinPoint) throws Throwable {

        Token token = (Token) joinPoint.getArgs()[0];
        UserId userId = (UserId) joinPoint.getArgs()[1];

        authenticationService.validate(token, userId);

        return joinPoint.proceed();
    }
}
