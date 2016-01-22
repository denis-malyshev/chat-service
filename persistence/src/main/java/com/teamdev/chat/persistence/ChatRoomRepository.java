package com.teamdev.chat.persistence;

import com.teamdev.chat.persistence.dom.ChatRoom;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ChatRoomRepository extends CrudRepository<ChatRoom, Long> {

    ChatRoom findByName(String name);

    @Query(value = "select * from chatroom c inner join user_chatroom ch on ch.chatRooms_id = c.id and ch.users_id = :id",
            nativeQuery = true)
    Collection<ChatRoom> findChatRoomsByUserId(@Param("id") long userId);
}
