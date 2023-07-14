package de.hsel.tetris.rawwebsocket;

import de.hsel.tetris.coregameloop.TetrisGame;
import de.hsel.tetris.coregameloop.services.BlockService;
import de.hsel.tetris.game.Game;
import de.hsel.tetris.game.GameHistoryRepository;
import de.hsel.tetris.game.GameRepository;
import de.hsel.tetris.rawwebsocket.dto.WebsocketConnectionGameInit;
import de.hsel.tetris.rawwebsocket.dto.WebsocketConnectionGameInitPayload;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class ServerWebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable {

    private static final Logger logger = LoggerFactory.getLogger(ServerWebSocketHandler.class);

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    static Map<Integer, List<WebSocketSession>> gameRooms = Collections.synchronizedMap(new HashMap<Integer, List<WebSocketSession>>());
    static Map<Integer, TetrisGame> runningTetrisGames = Collections.synchronizedMap(new HashMap<Integer, TetrisGame>());

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameHistoryRepository gameHistoryRepository;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Server connection opened");
        var token = session.getHandshakeHeaders().get("authentication");
        if (!token.isEmpty()) {
            //TODO validate token
            sessions.add(session);
            TextMessage message = new TextMessage("Tetris Server connection open");
            logger.info("Server sends: {}", message);
            session.sendMessage(message);
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("No 'authentication' token found in header."));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("Tetris Server connection closed: {}", status);
        sessions.remove(session);
    }

    @Scheduled(fixedRate = 1)
    void sendPeriodicMessages() throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                String broadcast = "server periodic message " + LocalTime.now();
                logger.info("Server sends: {}", broadcast);
                session.sendMessage(new TextMessage(broadcast));
            }
        }
    }


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String request = message.getPayload();
        logger.info("Server received: {}", request);
        try {
            if(!request.isEmpty()) {
                request = request.replaceAll("(\\r|\\n|\\t)", "");
                JSONObject jsonObject = new JSONObject(request);
                WebsocketConnectionGameInit init = getConnectionGameInit(jsonObject);

                Optional<Game> game = gameRepository.findById(init.getGameID());
                if (game.isPresent()) {
                    List<WebSocketSession> room = getGameRooms(game.get().getId());
                    List<Integer> players = gameRepository.findAllPlayersByGameID(game.get().getId());
                    switch (init.getType()) {
                        case "join":
                            if (game.get().getOpen() && players.contains(init.getClientID())) {
                                logger.info("Game: "+game.get().getId()+ " - New Player with ID "+init.getClientID()+" join.");
                                room.add(session);

                                gameRooms.put(game.get().getId(), room);
                                session.sendMessage(new TextMessage("{\"info\": \"Game is ready, wait for start...\"}"));
                            } else {
                                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("{\"error\": \"Game is not open. Connection close.\"}"));
                            }
                            break;
                        case "start":
                            if (game.get().getOpen() && room.contains(session)) {
                                if (Objects.equals(init.getClientID(), game.get().getOwnerID())) {
                                    logger.info("Game: "+game.get().getId()+ " - Owner with ID "+init.getClientID()+" ready to start.");

                                    Iterator<WebSocketSession> iterator = room.iterator();
                                    while (iterator.hasNext()) {
                                        var current = iterator.next();
                                        if (current.isOpen()) {
                                            current.sendMessage(new TextMessage("{\"game\": \"start\"}"));
                                        }
                                    }
                                    ownerStartGame(game.get(), room, players);
                                } else {
                                    session.sendMessage(new TextMessage("{\"info\": \"Game is ready, wait for start...\"}"));
                                }
                            }
                            break;
                        case "command":
                            if(game.get().getRunning() && room.contains(session)) {
                                if (!init.getPayload().isEmpty()) {
                                    WebsocketConnectionGameInitPayload payload = getPayload(init.getPayload());
                                    TetrisGame runningTetrisGame = runningTetrisGames.get(game.get().getId());
                                    if(runningTetrisGame != null) {
                                        if (runningTetrisGame.isRunning()) {
                                            switch (payload.getAction()) {
                                                case "rotate":
                                                    if (!payload.getDirection().isEmpty()) {
                                                        runningTetrisGame.rotate(init.getClientID());
                                                    }
                                                    break;
                                                case "move":
                                                    if (Objects.equals(payload.getDirection(), "left")) {
                                                        runningTetrisGame.left(init.getClientID());
                                                    } else if (Objects.equals(payload.getDirection(), "right")) {
                                                        runningTetrisGame.right(init.getClientID());
                                                    }
                                                    break;
                                                case "drop":
                                                    runningTetrisGame.drop(init.getClientID());
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("{\"error\": \"Game is not running.\"}"));
                            }
                            break;
                        case "give-up":
                            if((game.get().getOpen() || game.get().getRunning()) && room.contains(session)) {
                                room.remove(session);
                                TetrisGame runningTetrisGame = runningTetrisGames.get(game.get().getId());
                                if(runningTetrisGame != null) {
                                    runningTetrisGame.giveUp(init.getClientID());
                                }
                                gameRooms.put(game.get().getId(), room);
                                session.close(CloseStatus.NORMAL.withReason("{\"error\": \"You give up...\"}"));
                            }
                            break;
                        default:
                            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("{\"error\": \"Request type is wrong (Only 'join', 'start', 'command' or 'give-up' allowed.\"}"));
                            break;
                    }
                }
            }


        } catch (Exception e) {
            logger.info("Client send wrong JSON.");
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Wrong JSON send to Server. Connection close."));
        }
    }

    private List<WebSocketSession> getGameRooms(Integer id) {
        List<WebSocketSession> room = gameRooms.get(id);
        if (room == null) {
            gameRooms.put(id, new ArrayList<WebSocketSession>());
            room = gameRooms.get(id);
        }
        return room;
    }

    private void ownerStartGame(Game game, List<WebSocketSession> room, List<Integer> players) {
        boolean running = true;
        game.setRunning(running);
        game.setOpen(false);
        game.setState("running");
        gameRepository.save(game);

        var observer = new ServerWebSocketGameState(game, room, gameRepository, gameHistoryRepository);
        TetrisGame tetrisGame = new TetrisGame(game, new BlockService());
        tetrisGame.register(observer);
        runningTetrisGames.put(game.getId(), tetrisGame);

        Thread outputThread = new Thread(tetrisGame);

        players.forEach(tetrisGame::addPlayer);
        outputThread.start();
        tetrisGame.start();
    }

    private WebsocketConnectionGameInitPayload getPayload(String payload) {
        JSONObject jsonPayload = new JSONObject(payload);
        return new WebsocketConnectionGameInitPayload(
                jsonPayload.get("action").toString(),
                ((jsonPayload.has("direction"))?jsonPayload.get("direction").toString():""));
    }

    private WebsocketConnectionGameInit getConnectionGameInit(JSONObject jsonObject) {
        return new WebsocketConnectionGameInit(
                jsonObject.get("token").toString(),
                Integer.parseInt(jsonObject.get("client_id").toString()),
                Integer.parseInt(jsonObject.get("game_id").toString()),
                jsonObject.get("type").toString(),
                ((jsonObject.has("payload"))?jsonObject.get("payload").toString():""));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        logger.info("Server transport error: {}", exception.getMessage());
    }

    @Override
    public List<String> getSubProtocols() {
        return Collections.singletonList("subprotocol.demo.websocket");
    }
}