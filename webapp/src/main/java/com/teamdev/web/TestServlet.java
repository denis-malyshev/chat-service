package com.teamdev.web;

import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.ChatRoomService;
import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.chat.service.impl.exception.*;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import static com.teamdev.utils.JsonHelper.toJson;

public class TestServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(TestServlet.class);

    private BeanProvider beanProvider;

    @Override
    public void init() throws ServletException {
        LOG.info("Init servlet.");
        beanProvider = BeanProvider.getInstance();

        LOG.info("Generate sample data.");
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        resp.setContentType("application/json;charset=UTF-8");

        PrintWriter printWriter = resp.getWriter();

        Map<String, String[]> parameterMap = req.getParameterMap();

        UserId userId = new UserId(Long.parseLong(parameterMap.get("userId")[0]));

        UserService userService = beanProvider.getBean(UserService.class);
        ArrayList<ChatRoomDTO> availableChats = userService.findAvailableChats(userId);

        printWriter.write(toJson(availableChats));
    }

    private void generateSampleData() throws ChatRoomAlreadyExistsException, AuthenticationException, RegistrationException, UserNotFoundException, ChatRoomNotFoundException {

        ChatRoomService chatRoomService = beanProvider.getBean(ChatRoomService.class);
        ChatRoomDTO chatRoomDTO = chatRoomService.create("test-chat");

        UserService userService = beanProvider.getBean(UserService.class);

        UserName userName = new UserName("Vasya");
        UserEmail userEmail = new UserEmail("vasya@gmail.com");
        UserPassword userPassword = new UserPassword("pwd");
        UserDTO userDTO1 = userService.register(userName, userEmail, userPassword);

        AuthenticationService tokenService = beanProvider.getBean(AuthenticationService.class);
        Token token = tokenService.login(userEmail, userPassword);
        chatRoomService.joinToChatRoom(token, new UserId(userDTO1.id), new ChatRoomId(chatRoomDTO.id));
    }
}
