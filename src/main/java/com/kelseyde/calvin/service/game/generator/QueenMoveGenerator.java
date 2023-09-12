package com.kelseyde.calvin.service.game.generator;

import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.move.Move;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class QueenMoveGenerator extends SlidingMoveGenerator {

    @Getter
    private final PieceType pieceType = PieceType.QUEEN;

    @Override
    protected Set<Integer> getMoveVectors() {
        Set<Integer> queenVectors = new HashSet<>(DIAGONAL_MOVE_VECTORS);
        queenVectors.addAll(ORTHOGONAL_MOVE_VECTORS);
        return queenVectors;
    }

    @Override
    protected void applyPostGenerationConfig(Piece piece, Set<Move> legalMoves) {
        // no additional config required
    }

}
