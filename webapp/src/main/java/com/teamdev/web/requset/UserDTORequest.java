package com.teamdev.web.requset;

import com.teamdev.chat.service.impl.dto.Token;
import com.teamdev.chat.service.impl.dto.UserDTO;

public class UserDTORequest {

    public final UserDTO userDTO;
    public final Token token;

    public UserDTORequest(UserDTO userDTO, Token token) {
        this.userDTO = userDTO;
        this.token = token;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserDTORequest{");
        sb.append("userDTO=").append(userDTO);
        sb.append(", token=").append(token);
        sb.append('}');
        return sb.toString();
    }
}
