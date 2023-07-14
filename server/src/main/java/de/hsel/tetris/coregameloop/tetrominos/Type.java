package de.hsel.tetris.coregameloop.tetrominos;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Type {
    I, O, J, L, S, T, Z;

    @JsonValue
    public String getType() {
        return name();
    }
}
