package com.kelseyde.calvin.service.game.generator;

import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.move.Move;
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

    @Override
    protected void applyPostGenerationConfig(Piece piece, Set<Move> legalMoves) {
        legalMoves.forEach(legalMove -> {
            boolean negatesKingsideCastling = legalMove.getStartSquare() == getKingsideRookStartingSquare(piece.getColour());
            boolean negatesQueensideCastling = legalMove.getStartSquare() == getQueensideRookStartingSquare(piece.getColour());
            legalMove.setNegatesKingsideCastling(negatesKingsideCastling);
            legalMove.setNegatesQueensideCastling(negatesQueensideCastling);
        });
    }

    private int getKingsideRookStartingSquare(Colour colour) {
        return Colour.WHITE.equals(colour) ? 7 : 63;
    }

    private int getQueensideRookStartingSquare(Colour colour) {
        return Colour.WHITE.equals(colour) ? 0 : 56;
    }

}
