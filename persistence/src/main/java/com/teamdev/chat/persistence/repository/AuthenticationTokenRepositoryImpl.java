package com.teamdev.chat.persistence.repository;

import com.teamdev.chat.persistence.dom.AuthenticationToken;
import com.teamdev.chat.persistence.AuthenticationTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class AuthenticationTokenRepositoryImpl implements AuthenticationTokenRepository {

    private Map<Long, AuthenticationToken> tokens = new HashMap<>();
    private AtomicLong id = new AtomicLong(1);

    public AuthenticationTokenRepositoryImpl() {
    }

    public AuthenticationToken findById(long id) {
        return tokens.get(id);
    }

    @Override
    public AuthenticationToken findByKey(String key) {
        return tokens.values().stream().filter(x -> x.getKey().equals(key)).findFirst().orElse(null);
    }

    public Collection<AuthenticationToken> findAll() {
        return tokens.values();
    }

    public void update(AuthenticationToken token) {
        if (token.getId() == 0) {
            token.setId(id.getAndIncrement());
        }
        tokens.put(token.getId(), token);
    }

    public void delete(long id) {
        tokens.remove(id);
    }
}
