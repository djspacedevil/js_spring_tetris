package de.hsel.tetris.usermanagement.controllers;

import de.hsel.tetris.usermanagement.services.FileSystemStorageService;
import de.hsel.tetris.usermanagement.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
public class ProfilePictureController {

    private final FileSystemStorageService storageService;
    private final UserService userService;

    private final Set<String> allowedExtensions;

    public ProfilePictureController(final FileSystemStorageService storageService, UserService userService) {
        this.storageService = storageService;
        this.userService = userService;
        allowedExtensions = new HashSet<>();
        allowedExtensions.add("png");
        allowedExtensions.add("jpg");
        allowedExtensions.add("webp");
    }

    @Operation(summary = "Uploads a new profile picture for a user", security = @SecurityRequirement(name = "User Token"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The profile picture was updated properly", content = @Content),
            @ApiResponse(responseCode = "400", description = "File extension is not allowed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authorization token is missing or invalid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access to foreign profile pictures is prohibited", content = @Content),
            @ApiResponse(responseCode = "404", description = "The requested user does not exist", content = @Content),
            @ApiResponse(responseCode = "413", description = "The attached image is too large", content = @Content),
            @ApiResponse(responseCode = "500", description = "Something went wrong on the server", content = @Content)
    })
    @RequestMapping(value = "/api/users/{userId}/profile-picture", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity submit(@PathVariable Integer userId, @RequestPart(name = "profile_picture") MultipartFile file, Principal principal) throws IOException {
        var extension = FilenameUtils.getExtension(file.getOriginalFilename());
        var optionalUser = userService.getUser(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var user = optionalUser.get();

        if (!principal.getName().equals(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!allowedExtensions.contains(extension)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        var filename = String.format("%s_profile_picture.%s", userId, extension);

        storageService.store(file, filename);
        userService.updateProfilePicture(user, filename);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Retrieves the profile picture for the given user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The profile picture was updated properly", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
            @ApiResponse(responseCode = "404", description = "The requested user does not exist", content = @Content),
            @ApiResponse(responseCode = "500", description = "Something went wrong on the server", content = @Content)
    })
    @RequestMapping(value = "/api/users/{userId}/profile-picture", method = RequestMethod.GET)
    public ResponseEntity getProfilePicture(@PathVariable Integer userId) throws IOException {
        var optionalUser = userService.getUser(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var user = optionalUser.get();


        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + user.getProfilePicture());
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");


        ByteArrayResource resource = new ByteArrayResource(storageService.load(user.getProfilePicture()));

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .body(resource);
    }
}
