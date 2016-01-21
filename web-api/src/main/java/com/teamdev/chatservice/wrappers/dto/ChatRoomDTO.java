package com.teamdev.chatservice.wrappers.dto;

public class ChatRoomDTO {

    public final long id;
    public final String name;
    public final int userCount;
    public final long messageCount;

    public ChatRoomDTO(long id, String name, int userCount, long messageCount) {
        this.id = id;
        this.name = name;
        this.userCount = userCount;
        this.messageCount = messageCount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChatRoomDTO{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", userCount=").append(userCount);
        sb.append(", messageCount=").append(messageCount);
        sb.append('}');
        return sb.toString();
    }
}
