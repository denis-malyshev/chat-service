package com.teamdev.chat.persistence.dom;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MESSAGE")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "messageId")
    private long id;
    private LocalDateTime time;
    @NotNull
    private String text;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User sender;
    //    @ManyToOne(fetch = FetchType.EAGER)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User receiver;
    @ManyToOne(fetch = FetchType.EAGER)
    private ChatRoom chat;

    public Message() {
    }

    public Message(String text, User sender, User receiver) {
        this.time = LocalDateTime.now();
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Message(String text, User sender, ChatRoom chat) {
        this.time = LocalDateTime.now();
        this.text = text;
        this.sender = sender;
        this.chat = chat;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public ChatRoom getChat() {
        return chat;
    }

    public void setChat(ChatRoom chat) {
        this.chat = chat;
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
        sb.append("time=").append(time);
        sb.append(", text='").append(text).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
