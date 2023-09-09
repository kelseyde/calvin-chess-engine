package com.kelseyde.calvin.service.generator;

import com.kelseyde.calvin.model.piece.PieceType;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RookMoveGenerator extends SlidingMoveGenerator {

    @Getter
    private final PieceType pieceType = PieceType.ROOK;

    @Override
    protected Set<Integer> getMoveVectors() {
        return ORTHOGONAL_MOVE_VECTORS;
    }
}
