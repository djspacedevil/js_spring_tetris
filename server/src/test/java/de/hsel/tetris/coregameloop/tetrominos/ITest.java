package de.hsel.tetris.coregameloop.tetrominos;

import de.hsel.tetris.coregameloop.Grid;
import de.hsel.tetris.coregameloop.PlacementResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.hsel.tetris.coregameloop.Color.RED;
import static de.hsel.tetris.coregameloop.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ITest {
    private final Integer w = WHITE.value;
    private final Integer c = RED.value;
    private Integer[][] rotationState1;
    private Integer[][] rotationState2;

    @BeforeEach
    void setUp() {
        rotationState1 = new Integer[][]{
                {w, w, w, w},
                {w, w, w, w},
                {c, c, c, c},
                {w, w, w, w},
        };

        rotationState2 = new Integer[][]{
                {w, w, c, w},
                {w, w, c, w},
                {w, w, c, w},
                {w, w, c, w},
        };
    }

    @Test
    void shouldRotateTheRightWay() {
        var grid = new Grid();
        var tetromino = new I(0, 0);


        assertEquals(PlacementResult.Success, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState2);

        assertEquals(PlacementResult.Success, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState1);
    }

    @Test
    void shouldNotRotateWhenCollidingLeft() {
        var grid = new Grid();
        var tetromino = new I(-1, 0);
        tetromino.rotate(grid);

        assertEquals(PlacementResult.Collision, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState2);
    }


    @Test
    void shouldNotRotateWhenCollidingRight() {
        var grid = new Grid();
        var tetromino = new I(grid.getWidth() - 3, 0);
        tetromino.rotate(grid);

        assertEquals(PlacementResult.Collision, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState2);
    }


    @Test
    void shouldNotRotateWhenCollidingBottom() {
        var grid = new Grid();
        var tetromino = new I(0, grid.getHeight() - 3);

        assertEquals(PlacementResult.Collision, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState1);
    }

    @Test
    void shouldTakeUpCorrectCellsWhenPlaced() {
        var grid = new Grid();
        var tetromino = new I(0, grid.getHeight() - 3);

        tetromino.place(grid);

        assertEquals(grid.getCells()[grid.getHeight() - 1][3], tetromino.color.value);
        assertEquals(grid.getCells()[grid.getHeight() - 1][4], tetromino.color.value);
        assertEquals(grid.getCells()[grid.getHeight() - 1][5], tetromino.color.value);
        assertEquals(grid.getCells()[grid.getHeight() - 1][6], tetromino.color.value);
    }

    @Test
    void shouldProvideIType() {
        var tetromino = new I(0, 0);
        assertEquals(Type.I, tetromino.getType());
    }

    @Test
    void shouldSerializeAppropriately() {
        var tetromino = new I(0, 0);

        var result = tetromino.serialize();
        assertEquals(Type.I, result.type());
        assertThat(result.blockMatrix()).isDeepEqualTo(rotationState1);
    }
}