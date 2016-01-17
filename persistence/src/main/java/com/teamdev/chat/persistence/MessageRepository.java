package com.teamdev.chat.persistence;

import com.teamdev.chat.persistence.dom.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
}
