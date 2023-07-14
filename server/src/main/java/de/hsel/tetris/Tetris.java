package de.hsel.tetris;

import de.hsel.tetris.security.RsaKeyProperties;
import de.hsel.tetris.usermanagement.User;
import de.hsel.tetris.usermanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Files;
import java.nio.file.Path;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class Tetris {

    public static void main(String[] args) {
        SpringApplication.run(Tetris.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder, @Value("${files.location}") String location) {
        return args -> {
            var user1email = "user1@tetris.de";
            var user2email = "user2@tetris.de";
            var user3email = "user3@tetris.de";
            var user1name = "user1";
            var user2name = "user2";
            var user3name = "user3";

            var u1 = userRepository.findByEmail(user1email).or(() -> userRepository.findByUsername(user1name));
            var u2 = userRepository.findByEmail(user2email).or(() -> userRepository.findByUsername(user2name));
            var u3 = userRepository.findByEmail(user3email).or(() -> userRepository.findByUsername(user3name));

            if (u1.isEmpty())
                userRepository.save(new User(user1email, user1name, passwordEncoder.encode("password1"), "ROLE_USER"));

            if (u2.isEmpty())
                userRepository.save(new User(user2email, user2name, passwordEncoder.encode("password2"), "ROLE_USER"));

            if (u3.isEmpty())
                userRepository.save(new User(user3email, user3name, passwordEncoder.encode("password3"), "ROLE_USER"));

            var avatarFilename = "avatar.png";
            var targetPath = Path.of(location, avatarFilename);

            if (!Files.exists(targetPath)) {
                var avatarStream = Tetris.class.getClassLoader().getResourceAsStream("static/assets/images/avatar.png");
                Files.copy(avatarStream, targetPath);
            }

            userRepository.findAll().forEach(user -> {
                if (user.getProfilePicture() == null) {
                    user.setProfilePicture(avatarFilename);
                    userRepository.save(user);
                }
            });
        };
    }
}
