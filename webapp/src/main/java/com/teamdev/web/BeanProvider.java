package com.teamdev.web;

import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.ChatRoomService;
import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.chat.service.impl.exception.*;
import com.teamdev.web.config.ApplicationConfig;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class BeanProvider {

    private static final Logger LOG = Logger.getLogger(BeanProvider.class);

    private static BeanProvider ourInstance = new BeanProvider();

    private ApplicationContext context;

    public static BeanProvider getInstance() {
        return ourInstance;
    }

    private BeanProvider() {
        context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        try {
            generateSampleData();
        } catch (ChatRoomAlreadyExistsException e) {
            LOG.error("ChatRoomAlreadyExistsException: ", e);
        } catch (AuthenticationException e) {
            LOG.error("AuthenticationException: ", e);
        } catch (RegistrationException e) {
            LOG.error("RegistrationException: ", e);
        } catch (UserNotFoundException e) {
            LOG.error("UserNotFoundException: ", e);
        } catch (ChatRoomNotFoundException e) {
            LOG.error("ChatRoomNotFoundException: ", e);
        }
    }

    public <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    private void generateSampleData() throws ChatRoomAlreadyExistsException, AuthenticationException, RegistrationException, UserNotFoundException, ChatRoomNotFoundException {

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
