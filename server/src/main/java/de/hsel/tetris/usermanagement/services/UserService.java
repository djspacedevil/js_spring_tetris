package de.hsel.tetris.usermanagement.services;

import de.hsel.tetris.exception.ResourceNotFoundException;
import de.hsel.tetris.usermanagement.User;
import de.hsel.tetris.usermanagement.repositories.UserRepository;
import de.hsel.tetris.usermanagement.dto.UserRegistrationDto;
import de.hsel.tetris.usermanagement.dto.UserUpdateDto;
import de.hsel.tetris.usermanagement.exception.UserValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(UserRegistrationDto userRegistrationDto) throws UserValidationException {
        var email = userRegistrationDto.getEmail();
        var username = userRegistrationDto.getUsername();

        var errors = new HashMap<String, String>();

        if (userRepository.findByEmail(email).isPresent()) {
            errors.put("email", "taken");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            errors.put("username", "taken");
        }

        if (errors.size() > 0) {
            throw new UserValidationException(errors);
        }

        User n = new User();
        n.setUsername(userRegistrationDto.getUsername());
        n.setEmail(userRegistrationDto.getEmail());
        n.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        n.setRoles("ROLE_USER");
        return userRepository.save(n);
    }

    public User updateProfilePicture(Integer id, UserUpdateDto userUpdateDto) throws ResourceNotFoundException, UserValidationException {
        var user = userRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        var errors = new HashMap<String, String>();

        userUpdateDto.getEmail().ifPresent(email -> userRepository.findByEmail(email).ifPresentOrElse(
                __ -> errors.put("email", "taken"),
                () -> user.setEmail(email)
        ));

        userUpdateDto.getUsername().ifPresent(username -> userRepository.findByUsername(username).ifPresentOrElse(
                __ -> errors.put("username", "taken"),
                () -> user.setUsername(username)
        ));

        if (errors.size() > 0) {
            throw new UserValidationException(errors);
        }

        userUpdateDto.getPassword().ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));
        return userRepository.save(user);
    }

    public Optional<User> getUser(Integer id) {
        return userRepository.findById(id);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateProfilePicture(User user, String profilePictureFileName) {
        user.setProfilePicture(profilePictureFileName);
        userRepository.save(user);

        return user;
    }

    public Optional<User> getUserByName(String name) {
        return userRepository.findByUsername(name);
    }

    public boolean deleteUserById(Integer id) {
        userRepository.deleteById(id);
        var user = userRepository.findById(id);
        return user.isEmpty();
    }
}
