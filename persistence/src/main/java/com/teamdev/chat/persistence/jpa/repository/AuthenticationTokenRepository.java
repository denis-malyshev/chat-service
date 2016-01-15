package com.teamdev.chat.persistence.jpa.repository;

import com.teamdev.chat.persistence.jpa.dom.AuthenticationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationTokenRepository extends CrudRepository<AuthenticationToken, Long> {
}
