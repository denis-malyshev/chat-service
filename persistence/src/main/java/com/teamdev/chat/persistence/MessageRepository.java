package com.teamdev.chat.persistence;

import com.teamdev.chat.persistence.dom.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {

    List<Message> findByTimeAfter(LocalDateTime dateTime);
}
