package de.hsel.tetris.coregameloop.tetrominos.dto;

import de.hsel.tetris.coregameloop.tetrominos.Type;

public record TetrominoDto(Integer x, Integer y, Type type, Integer[][] blockMatrix) {
}
