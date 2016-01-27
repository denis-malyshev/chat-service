package com.teamdev.chat.persistence;

import com.teamdev.chat.persistence.dom.Message;
import com.teamdev.chat.persistence.dom.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {

    List<Message> findByCreatingTimeAfter(Date date);

    @Query(value = "select * from message where chatRoom_id = :id and creatingTime > :date",
            nativeQuery = true)
    List<Message> findMessagesByChatRoomIdAfterDate(@Param("id") long chatRoomId, @Param("date") Date date);
}
