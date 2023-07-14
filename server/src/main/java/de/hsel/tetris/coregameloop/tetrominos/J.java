package de.hsel.tetris.coregameloop.tetrominos;

import de.hsel.tetris.coregameloop.Grid;
import de.hsel.tetris.coregameloop.PlacementResult;

import java.util.ArrayList;
import java.util.List;

import static de.hsel.tetris.coregameloop.Color.BLUE;
import static de.hsel.tetris.coregameloop.Color.WHITE;
import static de.hsel.tetris.coregameloop.PlacementResult.Collision;
import static de.hsel.tetris.coregameloop.PlacementResult.Success;

public class J extends Tetromino {
    protected Integer rotationState = 0;
    private final List<Integer[][]> matrices = new ArrayList<>(4);

    public J(final Integer x, final Integer y) {
        super(x, y, BLUE);
        var w = WHITE.value;
        var c = this.color.value;

        matrices.add(new Integer[][]{
                {w, w, w},
                {c, c, c},
                {w, w, c}
        });

        matrices.add(new Integer[][]{
                {w, c, w},
                {w, c, w},
                {c, c, w}
        });
        matrices.add(new Integer[][]{
                {c, w, w},
                {c, c, c},
                {w, w, w}
        });
        matrices.add(new Integer[][]{
                {w, c, c},
                {w, c, w},
                {w, c, w}
        });
    }

    @Override
    public boolean canRotate(Grid grid) {
        return !grid.checkCollision(this.x, this.y, this.matrices.get(nextState()));

    }

    private int nextState() {
        return (rotationState + 1) % 4;
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
        return Type.J;
    }
}

