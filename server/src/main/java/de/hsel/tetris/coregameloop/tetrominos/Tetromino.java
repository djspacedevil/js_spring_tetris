package de.hsel.tetris.coregameloop.tetrominos;

import de.hsel.tetris.coregameloop.Color;
import de.hsel.tetris.coregameloop.Grid;
import de.hsel.tetris.coregameloop.PlacementResult;
import de.hsel.tetris.coregameloop.tetrominos.dto.TetrominoDto;

import java.util.Arrays;
import java.util.Objects;

import static de.hsel.tetris.coregameloop.PlacementResult.Collision;
import static de.hsel.tetris.coregameloop.PlacementResult.Success;

// TODO: implement rotation state as State Pattern
public abstract class Tetromino {
    protected Integer x;
    protected Integer y;

    protected Color color;

    public Tetromino(final Integer x, final Integer y, final Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public boolean canMoveLeft(final Grid grid) {
        return !grid.checkCollision(this.x - 1, this.y, this.getMatrix());
    }

    public PlacementResult moveLeft(final Grid grid) {
        if (canMoveLeft(grid)) {
            this.x -= 1;
            return Success;
        }
        return Collision;
    }

    public boolean canMoveRight(final Grid grid) {
        return !grid.checkCollision(this.x + 1, this.y, this.getMatrix());
    }

    public PlacementResult moveRight(final Grid grid) {
        if (canMoveRight(grid)) {
            this.x += 1;
            return Success;
        }

        return Collision;
    }

    public boolean canMoveDown(final Grid grid) {
        return !grid.checkCollision(this.x, this.y + 1, this.getMatrix());
    }

    public PlacementResult moveDown(final Grid grid) {
        if (canMoveDown(grid)) {
            this.y += 1;
            return Success;
        }

        return Collision;
    }

    public void drop(final Grid grid) {
        while (canMoveDown(grid)) {
            this.y++;
        }
    }

    public void place(Grid grid) {
        for (var mY = 0; mY < this.getMatrix().length; mY++) {
            for (var mX = 0; mX < this.getMatrix()[0].length; mX++) {
                if (Objects.equals(this.getMatrix()[mY][mX], this.color.value)) {
                    grid.setColorAt(this.x + mX, this.y + mY, this.color);
                }
            }
        }
    }

    abstract public Type getType();

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public abstract boolean canRotate(final Grid grid);

    public abstract PlacementResult rotate(final Grid grid);

    public abstract Integer[][] getMatrix();

    public TetrominoDto serialize() {
        var copiedMatrix = Arrays
                .stream(Arrays.copyOf(this.getMatrix(), this.getMatrix().length))
                .map(row -> Arrays.copyOf(row, row.length))
                .toArray(Integer[][]::new);
        return new TetrominoDto(this.x, this.y, this.getType(), copiedMatrix);
    }
}
