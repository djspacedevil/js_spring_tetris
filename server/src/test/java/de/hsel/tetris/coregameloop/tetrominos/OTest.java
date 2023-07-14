package de.hsel.tetris.coregameloop.tetrominos;

import de.hsel.tetris.coregameloop.Grid;
import de.hsel.tetris.coregameloop.PlacementResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.hsel.tetris.coregameloop.Color.WHITE;
import static de.hsel.tetris.coregameloop.Color.YELLOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OTest {
    private final Integer w = WHITE.value;
    private final Integer c = YELLOW.value;
    private Integer[][] matrix;

    @BeforeEach
    void setUp() {
        matrix = new Integer[][]{
                {w, w, w, w},
                {w, c, c, w},
                {w, c, c, w},
                {w, w, w, w},
        };
    }

    @Test
    void shouldNotChangeShapeOnRotation() {
        var grid = new Grid();
        var tetromino = new O(0, 0);


        assertEquals(PlacementResult.Success, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(matrix);

        assertEquals(PlacementResult.Success, tetromino.rotate(grid));
        assertThat(tetromino.getMatrix()).isDeepEqualTo(matrix);
    }

    @Test
    void shouldTakeUpCorrectCellsWhenPlaced() {
        var grid = new Grid();
        var tetromino = new O(0, grid.getHeight() - 3);

        tetromino.place(grid);

        assertEquals(grid.getCells()[grid.getHeight() - 1][4], tetromino.color.value);
        assertEquals(grid.getCells()[grid.getHeight() - 1][5], tetromino.color.value);
        assertEquals(grid.getCells()[grid.getHeight() - 2][4], tetromino.color.value);
        assertEquals(grid.getCells()[grid.getHeight() - 2][5], tetromino.color.value);

    }

    @Test
    void shouldProvideOType() {
        var tetromino = new O(0, 0);
        assertEquals(Type.O, tetromino.getType());
    }

    @Test
    void shouldSerializeAppropriately() {
        var tetromino = new O(0, 0);

        var result = tetromino.serialize();
        assertEquals(Type.O, result.type());
        assertThat(result.blockMatrix()).isDeepEqualTo(matrix);
    }
}