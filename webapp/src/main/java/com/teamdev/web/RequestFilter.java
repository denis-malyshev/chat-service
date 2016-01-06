package com.teamdev.web;

import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserId;
import com.teamdev.chat.service.impl.exception.AuthenticationException;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class RequestFilter implements Filter {

    private BeanProvider beanProvider;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        beanProvider = BeanProvider.getInstance();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        Map<String, String[]> parameterMap = servletRequest.getParameterMap();

        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!parameterMap.containsKey("token")) {
            response.sendError(403, "Can't find the token.");
            return;
        }

        if (!parameterMap.containsKey("userId")) {
            response.sendError(403, "Can't find the userId.");
            return;
        }

        Token token = new Token(parameterMap.get("token")[0]);
        UserId userId = new UserId(Long.parseLong(parameterMap.get("userId")[0]));

        UserService userService = beanProvider.getBean(UserService.class);

        if (userService.findById(userId) == null) {
            response.sendError(403, "User with this id not existing.");
            return;
        }

        AuthenticationService tokenService = beanProvider.getBean(AuthenticationService.class);

        try {
            tokenService.validate(token, userId);
        } catch (AuthenticationException e) {

            if (e.getMessage().equals("Invalid token.")) {
                response.sendError(403, "Access denied.");
                return;
            }

            if (e.getMessage().equals("Token has been expired.")) {
                response.sendError(403, "Token has been expired.");
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
