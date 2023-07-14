package de.hsel.tetris.coregameloop.tetrominos;

import de.hsel.tetris.coregameloop.Grid;
import de.hsel.tetris.coregameloop.PlacementResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.hsel.tetris.coregameloop.Color.BLUE;
import static de.hsel.tetris.coregameloop.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JTest {
    private final Integer w = WHITE.value;
    private final Integer c = BLUE.value;
    private Integer[][] rotationState1;
    private Integer[][] rotationState2;
    private Integer[][] rotationState3;
    private Integer[][] rotationState4;

    @BeforeEach
    void setUp() {
        rotationState1 = new Integer[][]{
                {w, w, w},
                {c, c, c},
                {w, w, c}
        };
        rotationState2 = new Integer[][]{
                {w, c, w},
                {w, c, w},
                {c, c, w}
        };
        rotationState3 = new Integer[][]{
                {c, w, w},
                {c, c, c},
                {w, w, w}
        };
        rotationState4 = new Integer[][]{
                {w, c, c},
                {w, c, w},
                {w, c, w}
        };
    }

    @Test
    void shouldRotateTheRightWay() {
        var grid = new Grid();
        var tetromino = new J(0, 0);


        assertEquals(PlacementResult.Success, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState2);

        assertEquals(PlacementResult.Success, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState3);

        assertEquals(PlacementResult.Success, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState4);

        assertEquals(PlacementResult.Success, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState1);
    }

    @Test
    void shouldNotRotateWhenCollidingLeft() {
        var grid = new Grid();
        var tetromino = new J(0, 0);
        tetromino.rotate(grid);
        tetromino.rotate(grid);
        tetromino.rotate(grid);

        tetromino.x = -1;

        assertEquals(PlacementResult.Collision, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState4);
    }


    @Test
    void shouldNotRotateWhenCollidingRight() {
        var grid = new Grid();
        var tetromino = new J(0, 0);

        tetromino.rotate(grid);

        tetromino.x = grid.getWidth() - 2;

        assertEquals(PlacementResult.Collision, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState2);
    }


    @Test
    void shouldNotRotateWhenCollidingBottom() {
        var grid = new Grid();
        var tetromino = new J(0, 0);

        tetromino.rotate(grid);
        tetromino.rotate(grid);

        tetromino.y = grid.getHeight() - 2;

        assertEquals(PlacementResult.Collision, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(rotationState3);
    }

    @Test
    void shouldTakeUpCorrectCellsWhenPlaced() {
        var grid = new Grid();
        var tetromino = new J(0, grid.getHeight() - 3);

        tetromino.place(grid);

        assertEquals(grid.getCells()[grid.getHeight() - 2][3], tetromino.color.value);
        assertEquals(grid.getCells()[grid.getHeight() - 2][4], tetromino.color.value);
        assertEquals(grid.getCells()[grid.getHeight() - 2][5], tetromino.color.value);
        assertEquals(grid.getCells()[grid.getHeight() - 1][5], tetromino.color.value);
    }

    @Test
    void shouldProvideJType() {
        var tetromino = new J(0, 0);
        assertEquals(Type.J, tetromino.getType());
    }

    @Test
    void shouldSerializeAppropriately() {
        var tetromino = new J(0, 0);

        var result = tetromino.serialize();
        assertEquals(Type.J, result.type());
        assertThat(result.blockMatrix()).isDeepEqualTo(rotationState1);
    }
}