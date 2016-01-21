package com.teamdev.chat.persistence;

import com.teamdev.chat.persistence.dom.ChatRoom;
import com.teamdev.chat.persistence.dom.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ChatRoomRepository extends CrudRepository<ChatRoom, Long> {

    ChatRoom findByName(String name);

    @Query(value = "select * from user u inner join user_chatroom ch on ch.users_id = u.id and ch.chatRooms_id = :id",
            nativeQuery = true)
    Collection<User> findUsersByChatRoomId(@Param("id") long chatRoomId);
}
