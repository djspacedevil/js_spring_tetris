package de.hsel.tetris.coregameloop;

public enum Color {
    WHITE(0),
    CYAN(1),
    GREEN(2),
    BLUE(3),
    PURPLE(4),
    ORANGE(5),
    RED(6),
    YELLOW(7),
    BLACK(8);

    public final Integer value;

    Color(Integer value) {
        this.value = value;
    }
}
