package de.hsel.tetris.coregameloop.services;

import de.hsel.tetris.coregameloop.tetrominos.I;
import de.hsel.tetris.coregameloop.tetrominos.J;
import de.hsel.tetris.coregameloop.tetrominos.L;
import de.hsel.tetris.coregameloop.tetrominos.O;
import de.hsel.tetris.coregameloop.tetrominos.S;
import de.hsel.tetris.coregameloop.tetrominos.T;
import de.hsel.tetris.coregameloop.tetrominos.Tetromino;
import de.hsel.tetris.coregameloop.tetrominos.Type;
import de.hsel.tetris.coregameloop.tetrominos.Z;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockService {

    private final List<Type> blockList = List.of(Type.I, Type.J, Type.L, Type.O, Type.S, Type.T, Type.Z);

    public Tetromino getRandomBlock(int x, int y) {
        var type = blockList.get((int) Math.floor(Math.random() * 7));
        return switch (type) {
            case I -> new I(x, y);
            case J -> new J(x, y);
            case L -> new L(x, y);
            case O -> new O(x, y);
            case S -> new S(x, y);
            case T -> new T(x, y);
            case Z -> new Z(x, y);
        };
    }
}
