package com.teamdev.chat.persistence;

import com.teamdev.chat.persistence.dom.AuthenticationToken;

public interface AuthenticationTokenRepository extends Repository<AuthenticationToken> {

    AuthenticationToken findByKey(String key);
}
