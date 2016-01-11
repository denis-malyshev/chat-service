package com.teamdev.chat.service.impl.dto;

public class UserDTO {

    public final long id;
    public final String firstName;
    public final String email;

    public String password;

    public UserDTO(long id, String firstName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.email = email;
    }

    public UserDTO(String firstName, String email, String password) {
        this.id = 0;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserDTO{");
        sb.append("firstName='").append(firstName).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
