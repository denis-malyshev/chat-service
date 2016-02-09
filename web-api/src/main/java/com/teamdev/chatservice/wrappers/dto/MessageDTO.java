package com.teamdev.chatservice.wrappers.dto;

import java.util.Date;

public class MessageDTO {
    public final long id;
    public final String sender;
    public final String text;
    public final Date time;

    public MessageDTO(long id, String sender, String text, Date time) {
        this.id = id;
        this.sender = sender;
        this.text = text;
        this.time = time;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageDTO{");
        sb.append("text='").append(text).append('\'');
        sb.append(", time=").append(time);
        sb.append('}');
        return sb.toString();
    }
}
