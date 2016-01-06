package com.teamdev.chat.persistence.repository;

import com.teamdev.chat.persistence.MessageRepository;
import com.teamdev.chat.persistence.dom.Message;
import com.teamdev.chat.persistence.dom.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class MessageRepositoryImpl implements MessageRepository {

    private Map<Long, Message> messages = new HashMap<>();
    private AtomicLong id = new AtomicLong(1);

    public Message findById(long id) {
        return messages.get(id);
    }

    public Collection<Message> findAll() {
        return messages.values();
    }

    public void update(Message message) {
        if (message.getId() == 0) {
            message.setId(id.getAndIncrement());
        }
        messages.put(message.getId(), message);
    }

    public Collection<Message> findBySender(User sender) {
        return Collections.emptySet();
    }

    public Collection<Message> findAllAfter(final LocalDateTime time) {
        return messages.values().stream().filter(x -> x.getTime().equals(time)).collect(Collectors.toList());
    }

    public int messageCount() {
        return messages.size();
    }

    public void delete(long id) {
        messages.remove(id);
    }

}
