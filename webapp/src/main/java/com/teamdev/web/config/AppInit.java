package com.teamdev.web.config;

import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.ChatRoomService;
import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.chat.service.impl.exception.*;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class AppInit implements WebApplicationInitializer {

    private static final Logger LOG = Logger.getLogger(AppInit.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(ApplicationConfig.class);

        servletContext.addListener(new ContextLoaderListener(rootContext));

        AnnotationConfigWebApplicationContext dispatcherServlet = new AnnotationConfigWebApplicationContext();
        dispatcherServlet.register(WebConfig.class);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(dispatcherServlet));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }

    private void generateSampleData(ApplicationContext context) throws ChatRoomAlreadyExistsException, AuthenticationException, RegistrationException, UserNotFoundException, ChatRoomNotFoundException {

        UserService userService = context.getBean(UserService.class);

        UserName userName = new UserName("Vasya");
        UserEmail userEmail = new UserEmail("vasya@gmail.com");
        UserPassword userPassword = new UserPassword("pwd");
        UserDTO userDTO1 = userService.register(userName, userEmail, userPassword);

        AuthenticationService tokenService = context.getBean(AuthenticationService.class);
        Token token = tokenService.login(userEmail, userPassword);

        ChatRoomService chatRoomService = context.getBean(ChatRoomService.class);
        ChatRoomDTO chatRoomDTO = chatRoomService.create(token, new UserId(userDTO1.id), "test-chat");
        chatRoomService.joinToChatRoom(token, new UserId(userDTO1.id), new ChatRoomId(chatRoomDTO.id));
    }
}
