package com.teamdev.web;

import com.teamdev.chat.persistence.AuthenticationTokenRepository;
import com.teamdev.chat.persistence.ChatRoomRepository;
import com.teamdev.chat.persistence.UserRepository;
import com.teamdev.chat.persistence.dom.AuthenticationToken;
import com.teamdev.chat.persistence.dom.ChatRoom;
import com.teamdev.chat.persistence.dom.User;
import com.teamdev.chat.service.UserService;
import com.teamdev.chat.service.impl.dto.ChatRoomDTO;
import com.teamdev.chat.service.impl.dto.UserId;

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

    private BeanProvider beanProvider;

    @Override
    public void init() throws ServletException {

        beanProvider = BeanProvider.getInstance();

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

        ChatRoomRepository chatRoomRepository = beanProvider.getBean(ChatRoomRepository.class);
        ChatRoom chatRoom = new ChatRoom("test-room");
        chatRoomRepository.update(chatRoom);

        UserRepository userRepository = beanProvider.getBean(UserRepository.class);

        User user1 = new User("Vasya", "vasya@gmail.com", passwordHash("pwd"));
        User user2 = new User("Masha", "masha@gmail.com", passwordHash("pwd"));

        userRepository.update(user1);
        userRepository.update(user2);

        AuthenticationTokenRepository tokenRepository = beanProvider.getBean(AuthenticationTokenRepository.class);
        AuthenticationToken token = new AuthenticationToken(user1.getId());
        tokenRepository.update(token);
        user1.setToken(token.getKey());

        chatRoom.getUsers().add(user1);
        user1.getChatRooms().add(chatRoom);
    }
}
