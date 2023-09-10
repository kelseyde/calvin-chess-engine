package com.kelseyde.calvin.service.generator;

import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.move.Move;
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

    @Override
    protected void applyPostGenerationConfig(Piece piece, Set<Move> legalMoves) {
        // no additional config required
    }

}
