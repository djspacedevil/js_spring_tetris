package de.hsel.tetris.coregameloop.tetrominos;

import de.hsel.tetris.coregameloop.Grid;
import de.hsel.tetris.coregameloop.PlacementResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.hsel.tetris.coregameloop.Color.GREEN;
import static de.hsel.tetris.coregameloop.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ZTest {
    private final Integer w = WHITE.value;
    private final Integer c = GREEN.value;
    private Integer[][] rotationState1;
    private Integer[][] rotationState2;

    @BeforeEach
    void setUp() {
        rotationState1 = new Integer[][]{
                {w, w, w},
                {c, c, w},
                {w, c, c},
        };

        rotationState2 = new Integer[][]{
                {w, w, c},
                {w, c, c},
                {w, c, w},
        };
    }

    @Test
    void shouldRotateTheRightWay() {
        var grid = new Grid();
        var tetromino = new Z(0, 0);


        assertEquals(PlacementResult.Success, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState2);

        assertEquals(PlacementResult.Success, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState1);
    }

    @Test
    void shouldNotRotateWhenCollidingLeft() {
        var grid = new Grid();
        var tetromino = new Z(0, 0);

        tetromino.rotate(grid);

        tetromino.x = -1;

        assertEquals(PlacementResult.Collision, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState2);
    }


    @Test
    void shouldNotRotateWhenCollidingRight() {
        var grid = new Grid();
        var tetromino = new Z(0, 0);

        tetromino.rotate(grid);

        tetromino.x = grid.getWidth() - 2;

        assertEquals(PlacementResult.Collision, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState2);
    }


    @Test
    void shouldTakeUpCorrectCellsWhenPlaced() {
        var grid = new Grid();
        var tetromino = new Z(0, grid.getHeight() - 3);

        tetromino.place(grid);

        assertEquals(grid.getCells()[grid.getHeight() - 2][3], tetromino.color.value);
        assertEquals(grid.getCells()[grid.getHeight() - 2][4], tetromino.color.value);
        assertEquals(grid.getCells()[grid.getHeight() - 1][4], tetromino.color.value);
        assertEquals(grid.getCells()[grid.getHeight() - 1][5], tetromino.color.value);
    }

    @Test
    void shouldProvideZType() {
        var tetromino = new Z(0, 0);
        assertEquals(Type.Z, tetromino.getType());
    }

    @Test
    void shouldSerializeAppropriately() {
        var tetromino = new Z(0, 0);

        var result = tetromino.serialize();
        assertEquals(Type.Z, result.type());
        assertThat(result.blockMatrix()).isDeepEqualTo(rotationState1);
    }
}