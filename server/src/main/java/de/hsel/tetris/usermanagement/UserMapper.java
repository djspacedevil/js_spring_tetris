package de.hsel.tetris.usermanagement;

import de.hsel.tetris.usermanagement.dto.UserDto;
import de.hsel.tetris.usermanagement.dto.UserSelfDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserSelfDto toSelfDto(final User user) {
        return new UserSelfDto(user.getId(), user.getUsername(), user.getEmail());
    }

    public UserDto toDto(final User user) {
        return new UserDto(user.getId(), user.getUsername());
    }
}
