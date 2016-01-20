package com.teamdev.chat.service.impl.dto;

import java.util.Date;

public class MessageDTO {
    public final long id;
    public final String text;
    public final Date time;

    public MessageDTO(long id, String text, Date time) {
        this.id = id;
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
