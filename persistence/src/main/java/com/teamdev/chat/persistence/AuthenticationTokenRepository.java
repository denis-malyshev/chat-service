package com.teamdev.chat.persistence;

import com.teamdev.chat.persistence.dom.AuthenticationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationTokenRepository extends CrudRepository<AuthenticationToken, Long> {

    AuthenticationToken findByKey(String key);
}
