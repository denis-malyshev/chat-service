package com.teamdev.chat.persistence;

import com.teamdev.chat.persistence.dom.ChatRoom;
import com.teamdev.chat.persistence.dom.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findByEmail(String email);

//    @Query(value = "select chatRooms from user where userId = ?0", nativeQuery = true)
//    Set<ChatRoom> selectChatRooms(long userId);
}
