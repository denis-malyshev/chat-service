package com.teamdev.chat.persistence;

import com.teamdev.chat.persistence.dom.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {

    List<Message> findByCreatingTimeAfter(Date date);
}
