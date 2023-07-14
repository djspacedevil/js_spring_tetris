package de.hsel.tetris.coregameloop.tetrominos;

import de.hsel.tetris.coregameloop.Grid;
import de.hsel.tetris.coregameloop.PlacementResult;

import java.util.ArrayList;
import java.util.List;

import static de.hsel.tetris.coregameloop.Color.RED;
import static de.hsel.tetris.coregameloop.Color.WHITE;
import static de.hsel.tetris.coregameloop.PlacementResult.Collision;
import static de.hsel.tetris.coregameloop.PlacementResult.Success;

public class I extends Tetromino {
    protected Integer rotationState = 0;
    private final List<Integer[][]> matrices = new ArrayList<>(2);

    public I(final Integer x, final Integer y) {
        super(x, y, RED);
        var w = WHITE.value;
        var c = this.color.value;

        matrices.add(new Integer[][]{
                {w, w, w, w},
                {w, w, w, w},
                {c, c, c, c},
                {w, w, w, w},
        });

        matrices.add(new Integer[][]{
                {w, w, c, w},
                {w, w, c, w},
                {w, w, c, w},
                {w, w, c, w},
        });
    }

    @Override
    public boolean canRotate(Grid grid) {
        return !grid.checkCollision(this.x, this.y, this.matrices.get(nextState()));
    }

    private int nextState() {
        return (rotationState + 1) % 2;
    }

    @Override
    public PlacementResult rotate(Grid grid) {
        if (!this.canRotate(grid)) {
            return Collision;
        }
        rotationState = nextState();
        return Success;
    }

    @Override
    public Integer[][] getMatrix() {
        return this.matrices.get(this.rotationState);
    }

    @Override
    public Type getType() {
        return Type.I;
    }
}
