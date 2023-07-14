package de.hsel.tetris.coregameloop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static de.hsel.tetris.coregameloop.Color.BLACK;
import static de.hsel.tetris.coregameloop.Color.WHITE;

public class Grid {

    public static final Integer MIN_WIDTH = 10;
    public static final Integer MAX_WIDTH = 20;
    public static final Integer MIN_HEIGHT = 18;
    public static final Integer MAX_HEIGHT = 36;

    // TODO: maybe use Integer 2D-Array over Cell abstraction because the latter is quite useless?
    private Integer[][] cells;

    private final Integer width;

    public Integer getHeight() {
        return height;
    }

    private final Integer height;

    public Grid() {
        this(Grid.MIN_WIDTH, Grid.MIN_HEIGHT);
    }

    public Grid(final Integer width, final Integer height) {
        this.width = width;
        this.height = height;

        var columns = ((width < MIN_WIDTH || width > MAX_WIDTH) ? Grid.MIN_WIDTH : width) + 6;
        var rows = ((height < MIN_HEIGHT || height > MAX_HEIGHT) ? Grid.MIN_HEIGHT : height) + 3;

        this.cells = new Integer[rows][columns];

        for (var y = 0; y < this.cells.length; y++) {
            for (var x = 0; x < this.cells[y].length; x++) {
                if (x <= 2) {
                    this.cells[y][x] = BLACK.value;
                } else if (x >= columns - 3) {
                    this.cells[y][x] = BLACK.value;
                } else if (y >= rows - 3) {
                    this.cells[y][x] = BLACK.value;
                } else {
                    this.cells[y][x] = WHITE.value;
                }
            }
        }
    }

    public Integer[][] getCells() {
        return cells;
    }

    public Integer getWidth() {
        return width;
    }

    public boolean checkCollision(final Integer refX, final Integer refY, final Integer[][] matrix) {
        var adjustedX = refX + 3;

        for (var matY = 0; matY < matrix.length; matY++) {
            for (var matX = 0; matX < matrix[matY].length; matX++) {
                if (this.cells[matY + refY][matX + adjustedX] * matrix[matY][matX] != 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setColorAt(final Integer x, final Integer y, final Color color) {

        if (x < 0 || x >= this.width) {
            throw new IndexOutOfBoundsException("Index out of width range: " + x);
        }
        if (y < 0 || y >= this.height) {
            throw new IndexOutOfBoundsException("Index out of height range: " + y);
        }
        this.cells[y][x + 3] = color.value;
    }

    public Integer removeLines() {
        var gameRows = List.of(Arrays.copyOfRange(cells, 0, cells.length - 3));
        var blackRows = List.of(Arrays.copyOfRange(cells, cells.length - 3, cells.length));

        var nonFilledRows = gameRows.stream()
                .filter(row -> Arrays.stream(row).anyMatch(colValue -> Objects.equals(colValue, WHITE.value)))
                .toList();

        var removedRowsCount = gameRows.size() - nonFilledRows.size();
        var newRows = new ArrayList<Integer[]>(removedRowsCount);

        for (var i = 0; i < removedRowsCount; i++) {
            newRows.add(this.createRow());
        }

        newRows.addAll(nonFilledRows);
        newRows.addAll(blackRows);

        this.cells = newRows.toArray(Integer[][]::new);

        return removedRowsCount;
    }

    private Integer[] createRow() {
        var cells = new ArrayList<Integer>(this.width + 6);
        cells.add(BLACK.value);
        cells.add(BLACK.value);
        cells.add(BLACK.value);

        for (var i = 0; i < this.width; i++) {
            cells.add(WHITE.value);
        }

        cells.add(BLACK.value);
        cells.add(BLACK.value);
        cells.add(BLACK.value);

        return cells.toArray(Integer[]::new);
    }

    public Integer[][] serialize() {
        return Arrays
                .stream(Arrays.copyOf(this.cells, this.cells.length))
                .map(row -> Arrays.copyOf(row, row.length))
                .toArray(Integer[][]::new);
    }
}
