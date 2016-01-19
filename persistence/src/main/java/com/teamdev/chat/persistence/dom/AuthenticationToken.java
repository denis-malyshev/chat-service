package com.teamdev.chat.persistence.dom;

import com.teamdev.utils.Hasher;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
public class AuthenticationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date expirationTime;
    @OneToOne(mappedBy = "token")
    private User user;
    private String tokenKey;

    AuthenticationToken() {
    }

    public AuthenticationToken(long userId) {
        this.expirationTime = new Date(System.currentTimeMillis() + 1000 * 60 * 15);
        this.tokenKey = Hasher.createHash(System.nanoTime() * Math.random() * 1000 + userId + "");
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public long getId() {
        return id;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthenticationToken that = (AuthenticationToken) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AuthenticationToken{");
        sb.append("id=").append(id);
        sb.append(", expirationTime=").append(expirationTime);
        sb.append('}');
        return sb.toString();
    }
}
