package com.teamdev.chat.persistence;

import com.teamdev.chat.persistence.dom.ChatRoom;

public interface ChatRoomRepository extends Repository<ChatRoom> {

    ChatRoom findByName(String name);

    int chatRoomCount();
}
