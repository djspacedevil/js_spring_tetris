package de.hsel.tetris.game;

import de.hsel.tetris.game.dto.GameHistoryDto;
import de.hsel.tetris.usermanagement.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "/api/game-histories/user")
public class GameHistoryController {

    private final GameHistoryService gameHistoryService;
    private final UserService userService;


    public GameHistoryController(GameHistoryService gameHistoryService, UserService userService) {
        this.gameHistoryService = gameHistoryService;
        this.userService = userService;
    }

    @Operation(summary = "User can read there own game history", security = @SecurityRequirement(name = "User Token"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "",
                    content = {
                        @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                 array = @ArraySchema(schema = @Schema(implementation = GameHistory.class))
                        )
                    }
            ),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })

    @GetMapping(path = "/{id}")
    @ResponseBody
    public List<GameHistoryDto> getUserGameHistory(@PathVariable("id") Integer id, Principal principal) {
        var user = userService.getUser(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (user.getUsername().equals(principal.getName())) {
            var history = gameHistoryService.getUserHistoryById(id);
            if (!history.isEmpty()) {
                var gamerHistory = new ArrayList<GameHistoryDto>();
                history.forEach(entry -> {
                    gamerHistory.add(new GameHistoryDto(entry));
                });
                return gamerHistory;
            }
            return new ArrayList<>();
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
