package com.teamdev.chat.persistence.dom;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    @OneToMany(mappedBy = "chatRoom")
    private Set<Message> messages = new HashSet<>();
    @ManyToMany(mappedBy = "chatRooms")
    private Set<User> users = new HashSet<>();

    ChatRoom() {
    }

    public ChatRoom(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public Set<User> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatRoom chatRoom = (ChatRoom) o;

        return id == chatRoom.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChatRoom{");
        sb.append("name='").append(name).append('\'');
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
