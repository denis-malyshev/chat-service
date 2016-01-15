package com.teamdev.chat.persistence.jpa.repository;

import com.teamdev.chat.persistence.jpa.dom.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findByEmail(String email);
}
