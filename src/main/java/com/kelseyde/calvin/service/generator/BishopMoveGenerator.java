package com.kelseyde.calvin.service.generator;

import com.kelseyde.calvin.model.PieceType;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BishopMoveGenerator extends SlidingMoveGenerator {

    @Getter
    private final PieceType pieceType = PieceType.BISHOP;

    @Override
    protected Set<Integer> getMoveVectors() {
        return DIAGONAL_MOVE_VECTORS;
    }

}
