package com.teamdev.web;

import com.google.gson.Gson;
import com.teamdev.chat.service.AuthenticationService;
import com.teamdev.chat.service.ChatRoomService;
import com.teamdev.chat.service.MessageService;
import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.dto.*;
import com.teamdev.chat.service.impl.exception.AuthenticationException;
import com.teamdev.chat.service.impl.exception.ChatRoomAlreadyExistsException;
import com.teamdev.chat.service.impl.exception.ChatRoomNotFoundException;
import com.teamdev.chat.service.impl.exception.UserNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class TestServlet extends HttpServlet {

    private BeanProvider beanProvider;

    @Override
    public void init() throws ServletException {

        beanProvider = BeanProvider.getInstance();

        try {
            generateSampleData();
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (ChatRoomAlreadyExistsException e) {
            e.printStackTrace();
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (ChatRoomNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        resp.setContentType("application/json;charset=UTF-8");

        PrintWriter printWriter = resp.getWriter();

        Map<String, String[]> parameterMap = req.getParameterMap();

        UserId userId = new UserId(Long.parseLong(parameterMap.get("userId")[0]));

        UserService userService = beanProvider.getBean(UserService.class);

        Set<ChatRoomDTO> availableChats = userService.findAvailableChats(userId);

        String json = toJson(availableChats);

        printWriter.write(json);
    }

    private void generateSampleData() throws AuthenticationException, ChatRoomAlreadyExistsException, UserNotFoundException, ChatRoomNotFoundException {

        ChatRoomService chatRoomService = beanProvider.getBean(ChatRoomService.class);

        ChatRoomDTO chatRoomDTO = chatRoomService.create("TestRoom");

        UserService userService = beanProvider.getBean(UserService.class);

        UserDTO userDTO1 = userService.register(new UserName("Vasya"), new UserEmail("vasya@gmail.com"), new UserPassword("pwd"));
        UserDTO userDTO2 = userService.register(new UserName("Masha"), new UserEmail("masha@gmail.com"), new UserPassword("pwd1"));

        AuthenticationService tokenService = beanProvider.getBean(AuthenticationService.class);

        Token token1 = tokenService.login(new UserEmail(userDTO1.email), new UserPassword("pwd"));

        UserId id1 = new UserId(userDTO1.id);
        UserId id2 = new UserId(userDTO2.id);

        chatRoomService.joinToChatRoom(token1, new UserId(userDTO1.id), new ChatRoomId(chatRoomDTO.id));

        MessageService messageService = beanProvider.getBean(MessageService.class);

        messageService.sendPrivateMessage(token1, id1, id2, "Hello, Masha!");
    }

    private String toJson(Collection<ChatRoomDTO> chatRoomDTOs) {

        Gson gson = new Gson();
        return gson.toJson(chatRoomDTOs);
    }
}
