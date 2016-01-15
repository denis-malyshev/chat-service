package com.teamdev.chat.persistence.jpa.repository;

import com.teamdev.chat.persistence.jpa.dom.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
}
