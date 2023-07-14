package de.hsel.tetris.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Optional;

public class UserUpdateDto {

    @JsonProperty("email")
    @Email
    private String email;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    public UserUpdateDto() {
    }

    public UserUpdateDto(final String email, final String username, final String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
