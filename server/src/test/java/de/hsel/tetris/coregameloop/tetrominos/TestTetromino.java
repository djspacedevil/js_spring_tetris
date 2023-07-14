package de.hsel.tetris.coregameloop.tetrominos;

import de.hsel.tetris.coregameloop.Grid;
import de.hsel.tetris.coregameloop.PlacementResult;

import static de.hsel.tetris.coregameloop.Color.BLACK;

public class TestTetromino extends Tetromino {

    public TestTetromino(Integer x, Integer y) {
        super(x, y, BLACK);
    }

    @Override
    public Integer[][] getMatrix() {
        return new Integer[][]{{this.color.value}};
    }

    @Override
    public void place(Grid grid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public boolean canRotate(Grid grid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlacementResult rotate(Grid grid) {
        throw new UnsupportedOperationException();
    }
}
