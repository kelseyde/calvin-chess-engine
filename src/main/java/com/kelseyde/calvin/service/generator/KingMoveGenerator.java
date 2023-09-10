package com.kelseyde.calvin.service.generator;

import com.kelseyde.calvin.model.*;
import com.kelseyde.calvin.utils.BoardUtils;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KingMoveGenerator implements PseudoLegalMoveGenerator {

    // All the possible candidate move offsets for a knight on a 1-dimensional board array, i.e., a knight in the middle
    // of an empty board can move to the squares indicated by these offsets.
    private static final Set<Integer> CANDIDATE_MOVE_OFFSETS = Set.of(-9, -8, -7, -1, 1, 7, 8, 9);

    // The following sets are exceptions to the initial rule, in scenarios where the king is placed on the a or h-files.
    // These exceptions prevent the piece from 'wrapping' around to the other side of the board.
    private static final Set<Integer> A_FILE_OFFSET_EXCEPTIONS = Set.of(-9, -1, 7);
    private static final Set<Integer> H_FILE_OFFSET_EXCEPTIONS = Set.of(-7, 1, 9);

    @Getter
    private final PieceType pieceType = PieceType.KING;

    @Override
    public Set<Move> generateLegalMoves(Game game, int startSquare) {

        Board board = game.getBoard();
        Piece king = board.pieceAt(startSquare)
                .filter(piece -> piece.isType(PieceType.KING))
                .orElseThrow(() -> new NoSuchElementException(String.format("There is no king on square %s!", startSquare)));

        Set<Integer> legalOffsets = CANDIDATE_MOVE_OFFSETS.stream()
                .filter(offset -> isLegalOffset(board, king, startSquare, offset))
                .collect(Collectors.toSet());

        Set<Move> legalMoves = legalOffsets.stream()
                .map(offset -> Move.builder().startSquare(startSquare).endSquare(startSquare + offset).build())
                .collect(Collectors.toSet());

        return legalMoves;

    }

    private boolean isLegalOffset(Board board, Piece king, int startSquare, int offset) {
        Colour colour = king.getColour();
        int targetSquare = startSquare + offset;
        if (!BoardUtils.isValidSquareCoordinate(targetSquare)) {
            return false;
        }
        if ((BoardUtils.isAFile(startSquare) && A_FILE_OFFSET_EXCEPTIONS.contains(offset)) ||
                (BoardUtils.isHFile(startSquare) && H_FILE_OFFSET_EXCEPTIONS.contains(offset))) {
            return false;
        }
        Optional<Piece> pieceOnTargetSquare = board.pieceAt(targetSquare);
        return pieceOnTargetSquare.isEmpty() || pieceOnTargetSquare.get().getColour().isOppositeColour(colour);
    }

}
