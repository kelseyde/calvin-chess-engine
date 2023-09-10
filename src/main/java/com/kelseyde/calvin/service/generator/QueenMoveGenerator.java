package com.kelseyde.calvin.service.generator;

import com.google.common.collect.Sets;
import com.kelseyde.calvin.model.PieceType;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class QueenMoveGenerator extends SlidingMoveGenerator {

    @Getter
    private final PieceType pieceType = PieceType.QUEEN;

    @Override
    protected Set<Integer> getMoveVectors() {
        return Sets.union(DIAGONAL_MOVE_VECTORS, ORTHOGONAL_MOVE_VECTORS);
    }
}
