package de.hsel.tetris.game;

import de.hsel.tetris.game.dto.CreateGameDto;
import de.hsel.tetris.game.dto.GameUserActionDto;
import de.hsel.tetris.usermanagement.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.*;


@Controller
@RequestMapping(path = "/api/games")
public class GameController {

    private final GameService gameService;
    private final UserService userService;

    public GameController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @Operation(summary = "User can read game information", security = @SecurityRequirement(name = "User Token"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "",
                    content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Game.class))
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
    public Game getGameData(@PathVariable("id") Integer id, Principal principal) {
        Game game = getGame(id);
        if (game == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return game;
    }

    @Operation(summary = "User can participate in an open game", security = @SecurityRequirement(name = "User Token"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User Informations for a join or change", required = true, content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = GameUserActionDto.class)),
                    examples = @ExampleObject(value ="{\n" +
                            "  \"action\": \"add_player | remove_player | game_close\",\n" +
                            "  \"payload\": {\n" +
                            "    \"game_id\": 1\n" +
                            "  }\n" +
                            "}")
            )
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "",
                    content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Map.class)),
                                    examples = @ExampleObject(value ="{\n" +
                                            "  \"successful\": true\n" +
                                            "}")
                            ),
                    }
            ),
            @ApiResponse(responseCode = "304", description = "Not Modified", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PatchMapping(path = "/{id}")
    @ResponseBody
    public Map<String, Object> updateGameData(@PathVariable("id") Integer id, @RequestBody GameUserActionDto body, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("successful", false);

        if (!principal.getName().isEmpty()) {
            var user = userService.getUserByName(principal.getName());
            Game game = getGame(id);
            String status = "";
            if(user.isPresent() && game != null && game.getOpen()) {
                switch (body.getAction()) {
                    case "add_player":
                        status = gameService.addPlayerToGame(game, user.get().getId());
                        break;
                    case "remove_player":
                        status = gameService.removePlayerFromGame(game, user.get().getId());
                        break;
                    case "game_close":
                        status = gameService.closeGame(game, user.get().getId());
                        break;
                    default: status = "Unknown action";
                    break;
                }

                if(status.contains("added") || status.contains("removed") || status.contains("closed")) {
                    response.put("successful", true);
                    return response;
                }
                response.put("reason", status);
            }
            response.put("reason", ((game.getRunning())?"running":"over"));
        }
        return response;
    }

    @Operation(summary = "User can create a new game", security = @SecurityRequirement(name = "User Token"))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "create Game", required = true, content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                     array = @ArraySchema(schema = @Schema(implementation = CreateGameDto.class)),
                     examples = @ExampleObject(value ="{\n" +
                             "  \"owner\": 10,\n" +
                             "  \"owner_client_id\": 11,\n" +
                             "  \"max_players\": 2\n" +
                             "}")
            )
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "",
                    content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Map.class)),
                                    examples = @ExampleObject(value ="{\n" +
                                            "  \"game_id\": 10\n" +
                                            "}")
                            ),
                    }
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping
    //@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Map<String, Integer> createGame(@RequestBody Optional<CreateGameDto> body, Principal principal) {
        Map<String, Integer> response = new HashMap<>();
        if (!body.isEmpty()) {
            var user = userService.getUserByName(principal.getName());
            if (user.get().getId() == body.get().getOwner()) {
                Optional<Game> game = gameService.createNewGame(
                        body.get().getOwner(),
                        body.get().getMaxPlayers()
                );
                if (game.isPresent()) {
                    response.put("game_id", game.get().getId());
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }


        }
        return response;
    }

    private Game getGame(Integer id) {
        return gameService.getGame(id);
    }
}
