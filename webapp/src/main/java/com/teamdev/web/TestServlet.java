package com.teamdev.web;

import com.teamdev.chat.persistence.dom.User;
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

import static com.teamdev.utils.ToolsProvider.passwordHash;
import static com.teamdev.utils.ToolsProvider.toJson;

public class TestServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(TestServlet.class);

    private BeanProvider beanProvider;

    @Override
    public void init() throws ServletException {
        LOG.info("Init servlet.");
        beanProvider = BeanProvider.getInstance();

        LOG.info("Generate sample data.");
        generateSampleData();
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

    private void generateSampleData() {

        ChatRoomService chatRoomService = beanProvider.getBean(ChatRoomService.class);
        ChatRoomDTO chatRoomDTO = null;
        try {
            chatRoomDTO = chatRoomService.create("test-chat");
        } catch (ChatRoomAlreadyExistsException e) {
            LOG.error("ChatRoomAlreadyExistsException: ", e);
        }

        UserService userService = beanProvider.getBean(UserService.class);

        User user1 = new User("Vasya", "vasya@gmail.com", passwordHash("pwd"));
        UserDTO userDTO1 = null;
        try {
            userDTO1 = userService.register(new UserName(user1.getFirstName()), new UserEmail(user1.getEmail()), new UserPassword("pwd"));
        } catch (AuthenticationException e) {
            LOG.error("AuthenticationException: ", e);
        } catch (RegistrationException e) {
            LOG.error("RegistrationException: ", e);
        }

        AuthenticationService tokenService = beanProvider.getBean(AuthenticationService.class);
        try {
            Token token = tokenService.login(new UserEmail(user1.getEmail()), new UserPassword("pwd"));
            chatRoomService.joinToChatRoom(token, new UserId(userDTO1.id), new ChatRoomId(chatRoomDTO.id));
        } catch (AuthenticationException e) {
            LOG.error("AuthenticationException: ", e);
        } catch (UserNotFoundException e) {
            LOG.error("UserNotFoundException: ", e);
        } catch (ChatRoomNotFoundException e) {
            LOG.error("ChatRoomNotFoundException: ", e);
        }
    }
}
