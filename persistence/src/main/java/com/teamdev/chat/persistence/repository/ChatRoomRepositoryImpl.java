package com.teamdev.chat.persistence.repository;

import com.teamdev.chat.persistence.ChatRoomRepository;
import com.teamdev.chat.persistence.dom.ChatRoom;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ChatRoomRepositoryImpl implements ChatRoomRepository {

    private Map<Long, ChatRoom> chatRooms = new HashMap<>();
    private AtomicLong id = new AtomicLong(1);

    public ChatRoomRepositoryImpl() {
    }

    public ChatRoom findById(long id) {
        return chatRooms.get(id);
    }

    @Override
    public ChatRoom findByName(String name) {
        return chatRooms.values().stream().filter(x -> x.getName().equals(name)).findFirst().orElse(null);
    }

    public Collection<ChatRoom> findAll() {
        return chatRooms.values();
    }

    public void update(ChatRoom chatRoom) {
        if (chatRoom.getId() == 0) {
            chatRoom.setId(id.getAndIncrement());
        }
        chatRooms.put(chatRoom.getId(), chatRoom);
    }

    @Override
    public int chatRoomCount() {
        return chatRooms.size();
    }

    public void delete(long id) {
        chatRooms.remove(id);
    }
}
