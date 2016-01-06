package com.teamdev.chat.persistence;

import com.teamdev.chat.persistence.dom.User;

public interface UserRepository extends Repository<User> {

    User findByMail(String mail);

    int userCount();
}
