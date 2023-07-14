package de.hsel.tetris.consoleclient;

import ch.qos.logback.classic.Level;
import de.hsel.tetris.coregameloop.TetrisGame;
import de.hsel.tetris.coregameloop.services.BlockService;
import de.hsel.tetris.game.Game;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.jline.keymap.KeyMap.key;

public class ConsoleClient {
    private static boolean running = true;
    private static final Logger LOG = LoggerFactory.getLogger(ConsoleClient.class);

    public static void main(String[] args) throws IOException {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ERROR);

        try (Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build()) {

            var observer = new ConsolePrinter(terminal);
            var tetrisGame = new TetrisGame(new Game(), new BlockService());
            tetrisGame.register(observer);

            terminal.enterRawMode();
            terminal.puts(Capability.enter_ca_mode);
            terminal.puts(Capability.keypad_xmit);
            terminal.puts(Capability.clear_screen);

            BindingReader bindingReader = new BindingReader(terminal.reader());

            KeyMap<Operation> keyMap = new KeyMap<>();
            keyMap.bind(Operation.ROTATE_P1, key(terminal, Capability.key_up));
            keyMap.bind(Operation.LEFT_P1, key(terminal, Capability.key_left));
            keyMap.bind(Operation.RIGHT_P1, key(terminal, Capability.key_right));
            keyMap.bind(Operation.DROP_P1, key(terminal, Capability.key_down));
            keyMap.bind(Operation.ROTATE_P2, "w");
            keyMap.bind(Operation.LEFT_P2, "a");
            keyMap.bind(Operation.RIGHT_P2, "d");
            keyMap.bind(Operation.DROP_P2, "s");
            keyMap.bind(Operation.QUIT, "q");
            keyMap.bind(Operation.ONE_PLAYER, "1");
            keyMap.bind(Operation.TWO_PLAYERS, "2");

            terminal.writer().println("\033[2J");
            terminal.writer().println("How many players?: ");
            terminal.flush();

            Thread outputThread = new Thread(tetrisGame);

            Thread inputThread = new Thread(() -> {
                var gameStarted = false;
                var players = 0;

                while (running) {
                    Operation op = bindingReader.readBinding(keyMap);


                    switch (op) {
                        case ROTATE_P1 -> {
                            if (gameStarted) tetrisGame.rotate(0);
                        }
                        case LEFT_P1 -> {
                            if (gameStarted) tetrisGame.left(0);
                        }
                        case RIGHT_P1 -> {
                            if (gameStarted) tetrisGame.right(0);
                        }
                        case DROP_P1 -> {
                            if (gameStarted) tetrisGame.drop(0);
                        }

                        case ROTATE_P2 -> {
                            if (gameStarted && players == 2) tetrisGame.rotate(1);
                        }
                        case LEFT_P2 -> {
                            if (gameStarted && players == 2) tetrisGame.left(1);
                        }
                        case RIGHT_P2 -> {
                            if (gameStarted && players == 2) tetrisGame.right(1);
                        }
                        case DROP_P2 -> {
                            if (gameStarted && players == 2) tetrisGame.drop(1);
                        }

                        case ONE_PLAYER -> {
                            if (players == 0) {
                                players = 1;
                                tetrisGame.addPlayer(0);
                                gameStarted = true;
                                outputThread.start();
                                tetrisGame.start();
                            }
                        }
                        case TWO_PLAYERS -> {
                            if (players == 0) {
                                players = 2;
                                tetrisGame.addPlayer(0);
                                tetrisGame.addPlayer(1);
                                gameStarted = true;
                                outputThread.start();
                                tetrisGame.start();
                            }
                        }
                        case QUIT -> {
                            running = false;
                            tetrisGame.finish();
                        }
                    }
                }
            });

            inputThread.start();

            try {
                inputThread.join();
                outputThread.join();
            } catch (InterruptedException e) {
                LOG.error("InterruptedException: {}", e.getMessage());
            }

            System.exit(0);
        }
    }

    public enum Operation {
        ROTATE_P1,
        LEFT_P1,
        RIGHT_P1,
        DROP_P1,
        START,
        ONE_PLAYER, TWO_PLAYERS, ROTATE_P2, LEFT_P2, RIGHT_P2, DROP_P2, QUIT
    }

}
