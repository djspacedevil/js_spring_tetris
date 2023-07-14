package de.hsel.tetris.consoleclient;

import de.hsel.tetris.coregameloop.TetrisGame;
import de.hsel.tetris.patterns.observer.Observer;
import org.apache.commons.lang3.StringUtils;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

class ConsolePrinter implements Observer<TetrisGame.GameState> {
    private static final Logger LOG = LoggerFactory.getLogger(ConsolePrinter.class);
    private TetrisGame.GameState gameState;
    private final Terminal terminal;

    public ConsolePrinter(final Terminal terminal) {

        this.terminal = terminal;
    }

    @Override
    public void update(TetrisGame.GameState data) {
        gameState = data;
        this.refreshConsole();
    }

    private void refreshConsole() {
        List<List<Integer>> buffer = null;
        StringBuilder headline = new StringBuilder();
        for (var board : gameState.boards()) {
            if (buffer == null) {
                buffer = new ArrayList<>();
                for (var ignored : board.grid()) {
                    buffer.add(new ArrayList<>());
                }
            }

            var blockMat = board.currentBlock() == null ? new Integer[0][0] : board.currentBlock().blockMatrix();
            var xOffset = board.currentBlock() == null ? 0 : board.currentBlock().x();
            var yOffset = board.currentBlock() == null ? 0 : board.currentBlock().y();
            var mat = mergeWithOffset(board.grid(), blockMat, xOffset + 3, yOffset);

            var rows = toMutableList(mat);
            var nextBlockRows = new ArrayList<List<Integer>>();
            nextBlockRows.add(new ArrayList<>());
            nextBlockRows.addAll(toMutableList(board.nextBlock().blockMatrix()));

            for (var nbRow : nextBlockRows) {
                while (nbRow.size() < 6) {
                    nbRow.add(8);
                }
            }

            while (nextBlockRows.size() < rows.size()) {
                var newRow = new Integer[6];
                Arrays.fill(newRow, 8);
                nextBlockRows.add(Arrays.asList(newRow));
            }

            extendBufferWithRows(buffer, rows, nextBlockRows);
            headline
                    .append("||    ")
                    .append("LEVEL: ")
                    .append(StringUtils.leftPad(board.level().toString(), 2, '0'))
                    .append(" | SCORE: ")
                    .append(StringUtils.leftPad(board.score().toString(), 11, '0'))
                    .append(" ")
                    .append(StringUtils.repeat(' ', 4))
                    .append("|| ");
        }
        terminal.writer().print("\033[2J");
        terminal.flush();
        if (buffer != null) {
            terminal.writer().println(headline);
            terminal.writer().println(StringUtils.repeat('-', 43));
            terminal.writer().println(stringifyBuffer(buffer));
            terminal.flush();
        }
    }

    private void extendBufferWithRows(List<List<Integer>> buffer, List<List<Integer>> rows, List<List<Integer>> nextBlockRows) {
        var bufferIt = buffer.iterator();
        var rowsIt = rows.iterator();
        var nbrIt = nextBlockRows.iterator();

        while (bufferIt.hasNext()) {
            try {
                var target = bufferIt.next();
                var row = rowsIt.next();
                var nbr = nbrIt.next();

                target.addAll(row);
                target.addAll(nbr);
            } catch (NoSuchElementException ignored) {
            }
        }
    }

    private <T> List<List<T>> toMutableList(T[][] arr) {
        return Arrays.stream(arr).map(row -> new ArrayList<>(List.of(row))).collect(Collectors.toCollection(ArrayList::new));
    }


    private Integer[][] mergeWithOffset(final Integer[][] arr1, final Integer[][] arr2, final Integer offsetX, final Integer offsetY) {
        var copy = Arrays.stream(Arrays.copyOf(arr1, arr1.length)).map(row -> Arrays.copyOf(row, row.length)).toArray(Integer[][]::new);
        for (var y = 0; y < arr2.length; y++) {
            for (var x = 0; x < arr2[y].length; x++) {
                if (copy[y + offsetY][x + offsetX] == 0) {
                    copy[y + offsetY][x + offsetX] = arr2[y][x];
                }
            }
        }
        return copy;
    }

    private String stringifyBuffer(final List<List<Integer>> buffer) {
        var result = new StringBuilder();
        for (var row : buffer) {
            for (var col : row) {
                result.append(colorize(col.toString().replace("0", "_")));
                result.append(" ");
            }
            result.append("\n");
        }
        return result.toString();
    }

    private String colorize(String text) {
        return switch (text) {
            case "1" -> "\033[36m" + text + "\033[0m";
            case "2" -> "\033[32m" + text + "\033[0m";
            case "3" -> "\033[34m" + text + "\033[0m";
            case "4" -> "\033[35m" + text + "\033[0m";
            case "5" -> "\033[38;5;208m" + text + "\033[0m";
            case "6" -> "\033[31m" + text + "\033[0m";
            case "7" -> "\033[33m" + text + "\033[0m";
            default -> text;
        };
    }
}
