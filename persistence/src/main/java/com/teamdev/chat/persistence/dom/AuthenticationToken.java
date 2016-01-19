package com.teamdev.chat.persistence.dom;

import com.teamdev.utils.Hasher;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "TOKEN")
public class AuthenticationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tokenId")
    private long id;
    private Date expirationTime;
    @OneToOne
    @JoinColumn(name = "token")
    private User user;
    private String key;

    AuthenticationToken() {
    }

    public AuthenticationToken(User user) {
        this.expirationTime = new Date(System.currentTimeMillis() + 1000 * 60 * 15);
        this.user = user;
        this.key = Hasher.createHash(System.nanoTime() * Math.random() * 1000 + user.getId() + "");
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

    public Date getExpirationTime() {
        return expirationTime;
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
