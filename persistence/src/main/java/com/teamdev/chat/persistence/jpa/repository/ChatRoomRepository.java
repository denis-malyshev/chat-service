package com.teamdev.chat.persistence.jpa.repository;

import com.teamdev.chat.persistence.jpa.dom.ChatRoom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends CrudRepository<ChatRoom, Long> {
}
