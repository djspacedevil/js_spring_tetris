package de.hsel.tetris.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserSelfDto extends UserDto {
    @JsonProperty("email")
    private String email;

    public UserSelfDto(Integer id, String username, String email) {
        super(id, username);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
