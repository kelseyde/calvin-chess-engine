package com.kelseyde.calvin.service.drawcalculator;

import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.game.DrawType;
import com.kelseyde.calvin.model.game.Game;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

public class InsufficientMaterialCalculator implements DrawCalculator {

    private static final Set<Set<PieceType>> INSUFFICIENT_MATERIAL_PIECE_SETS = Set.of(
            Set.of(PieceType.KING),
            Set.of(PieceType.KING, PieceType.BISHOP),
            Set.of(PieceType.KING, PieceType.KNIGHT)
    );

    @Getter
    private final DrawType drawType = DrawType.INSUFFICIENT_MATERIAL;

    @Override
    public boolean isDraw(Game game) {

        Set<PieceType> whitePieceTypes = game.getBoard().getPieces(Colour.WHITE)
                .values().stream()
                .map(Piece::getType)
                .collect(Collectors.toSet());

        Set<PieceType> blackPieceTypes = game.getBoard().getPieces(Colour.BLACK)
                .values().stream()
                .map(Piece::getType)
                .collect(Collectors.toSet());

        return INSUFFICIENT_MATERIAL_PIECE_SETS.contains(whitePieceTypes)
                && INSUFFICIENT_MATERIAL_PIECE_SETS.contains(blackPieceTypes);
    }

}
