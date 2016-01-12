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
        rootContext.refresh();

        try {
            generateSampleData(rootContext);
        } catch (ChatRoomAlreadyExistsException |
                ChatRoomNotFoundException |
                UserNotFoundException |
                RegistrationException |
                AuthenticationException e) {
            LOG.error(e.getMessage(), e);
        }

        servletContext.addListener(new ContextLoaderListener(rootContext));

        AnnotationConfigWebApplicationContext dispatcherServlet = new AnnotationConfigWebApplicationContext();
        dispatcherServlet.register(WebConfig.class);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(dispatcherServlet));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }

    private void generateSampleData(ApplicationContext context) throws ChatRoomAlreadyExistsException, AuthenticationException, RegistrationException, UserNotFoundException, ChatRoomNotFoundException {

        UserService userService = context.getBean(UserService.class);

        UserDTO userDTO = new UserDTO("Vasya", "vasya1@gmail.com", "pwd");
        UserDTO registeredUser = userService.register(userDTO);

        LoginInfo loginInfo = new LoginInfo(userDTO.email, userDTO.password);

        AuthenticationService tokenService = context.getBean(AuthenticationService.class);
        Token token = tokenService.login(loginInfo);

        ChatRoomService chatRoomService = context.getBean(ChatRoomService.class);
        ChatRoomDTO chatRoomDTO = chatRoomService.create(token, new UserId(registeredUser.id), "test-chat");
        chatRoomService.joinToChatRoom(token, new UserId(registeredUser.id), new ChatRoomId(chatRoomDTO.id));
    }
}
