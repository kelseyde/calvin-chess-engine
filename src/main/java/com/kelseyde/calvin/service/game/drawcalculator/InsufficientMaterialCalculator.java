package com.kelseyde.calvin.service.game.drawcalculator;

import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.DrawType;
import com.kelseyde.calvin.model.Game;
import lombok.Getter;

import java.util.List;
import java.util.Set;

public class InsufficientMaterialCalculator implements DrawCalculator {

    private static final Set<List<PieceType>> INSUFFICIENT_MATERIAL_PIECE_COMBINATIONS = Set.of(
            List.of(PieceType.KING),
            List.of(PieceType.KING, PieceType.BISHOP),
            List.of(PieceType.KING, PieceType.KNIGHT)
    );

    @Getter
    private final DrawType drawType = DrawType.INSUFFICIENT_MATERIAL;

    @Override
    public boolean isDraw(Game game) {

        List<PieceType> whitePieceTypes = game.getBoard().getPieces(Colour.WHITE)
                .values().stream()
                .map(Piece::getType)
                .toList();

        List<PieceType> blackPieceTypes = game.getBoard().getPieces(Colour.BLACK)
                .values().stream()
                .map(Piece::getType)
                .toList();

        return INSUFFICIENT_MATERIAL_PIECE_COMBINATIONS.stream().anyMatch(list -> isSamePieceList(whitePieceTypes, list))
                && INSUFFICIENT_MATERIAL_PIECE_COMBINATIONS.stream().anyMatch(list -> isSamePieceList(list, blackPieceTypes));
    }

    private boolean isSamePieceList(List<PieceType> list1, List<PieceType> list2) {
        return list1.size() == list2.size()
                && list1.containsAll(list2);
    }

}
