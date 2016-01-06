package com.teamdev.chat.persistence;

import com.teamdev.chat.persistence.dom.Message;
import com.teamdev.chat.persistence.dom.User;

import java.time.LocalDateTime;
import java.util.Collection;

public interface MessageRepository extends Repository<Message> {

    Collection<Message> findBySender(User sender);

    Collection<Message> findAllAfter(LocalDateTime time);

    int messageCount();
}
