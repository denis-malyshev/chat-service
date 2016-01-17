package com.teamdev.chat.persistence;

import com.teamdev.chat.persistence.dom.ChatRoom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends CrudRepository<ChatRoom, Long> {

    ChatRoom findByName(String name);
}
