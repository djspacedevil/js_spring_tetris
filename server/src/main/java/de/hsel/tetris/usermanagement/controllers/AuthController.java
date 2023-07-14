package de.hsel.tetris.usermanagement.controllers;

import de.hsel.tetris.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Operation(summary = "Login for users via HTTP Basic Auth", security = @SecurityRequirement(name = "User Login"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The JWT for the logged-in user",
                    content = {@Content(mediaType = "text/plain")}),
            @ApiResponse(responseCode = "401", description = "Credentials are invalid",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Something went wrong on the server",
                    content = @Content)
    })
    @PostMapping("/api/login")
    public String token(Authentication authentication) {
        LOG.debug("Token requested for user: '{}'", authentication.getName());
        var token = tokenService.generateToken(authentication);
        LOG.debug("Token granted {}", token);
        return token;
    }
}
