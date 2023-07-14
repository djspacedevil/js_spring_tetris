package de.hsel.tetris.coregameloop.tetrominos;

import de.hsel.tetris.coregameloop.Grid;
import de.hsel.tetris.coregameloop.PlacementResult;

import static de.hsel.tetris.coregameloop.Color.WHITE;
import static de.hsel.tetris.coregameloop.Color.YELLOW;
import static de.hsel.tetris.coregameloop.PlacementResult.Success;

public class O extends Tetromino {
    private final Integer[][] matrix;

    public O(final Integer x, final Integer y) {
        super(x, y, YELLOW);
        var w = WHITE.value;
        var c = this.color.value;


        matrix = new Integer[][]{
                {w, w, w, w},
                {w, c, c, w},
                {w, c, c, w},
                {w, w, w, w},
        };
    }

    @Override
    public boolean canRotate(Grid grid) {
        return true;
    }

    @Override
    public PlacementResult rotate(Grid grid) {
        return Success;
    }

    @Override
    public Integer[][] getMatrix() {
        return this.matrix;
    }

    @Override
    public Type getType() {
        return Type.O;
    }
}
