package com.teamdev.chat.persistence.dom;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date creatingTime;
    private String text;
    @ManyToOne
    private User sender;
    @ManyToOne
    private User receiver;
    @ManyToOne
    private ChatRoom chatRoom;

    Message() {
    }

    public Message(String text, User sender, User receiver) {
        this.creatingTime = new Date();
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Message(String text, User sender, ChatRoom chatRoom) {
        this.creatingTime = new Date();
        this.text = text;
        this.sender = sender;
        this.chatRoom = chatRoom;
    }

    public long getId() {
        return id;
    }

    public Date getCreatingTime() {
        return creatingTime;
    }

    public String getText() {
        return text;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return id == message.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("creatingTime=").append(creatingTime);
        sb.append(", text='").append(text).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
