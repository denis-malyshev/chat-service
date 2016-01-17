package com.teamdev.chat.persistence.dom;

import com.teamdev.utils.Hasher;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TOKEN")
public class AuthenticationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tokenId")
    private long id;
    private LocalDateTime expirationTime;
    private long userId;
    @OneToOne
    @JoinColumn(name = "token")
    private User user;
    private String key;

    public AuthenticationToken() {
    }

    public AuthenticationToken(long userId) {
        this.expirationTime = LocalDateTime.now().plusMinutes(15L);
        this.userId = userId;
        this.key = Hasher.createHash(System.nanoTime() * Math.random() * 100 + userId + "");
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
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
