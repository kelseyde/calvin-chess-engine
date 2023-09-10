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
public class KnightMoveGenerator implements PseudoLegalMoveGenerator {

    // All the possible candidate move offsets for a knight on a 1-dimensional board array, i.e., a knight in the middle
    // of an empty board can move to the squares indicated by these offsets.
    private static final Set<Integer> CANDIDATE_MOVE_OFFSETS = Set.of(-17, -15, -10, -6, 6, 10, 15, 17);

    // The following sets are exceptions to the initial rule, in scenarios where the knight is placed on the a, b, g or h-files.
    // These exceptions prevent the piece from 'wrapping' around to the other side of the board.
    private static final Set<Integer> A_FILE_OFFSET_EXCEPTIONS = Set.of(-17, -10, 6, 15);
    private static final Set<Integer> B_FILE_OFFSET_EXCEPTIONS = Set.of(-10, 6);
    private static final Set<Integer> G_FILE_OFFSET_EXCEPTIONS = Set.of(-6, 10);
    private static final Set<Integer> H_FILE_OFFSET_EXCEPTIONS = Set.of(-15, -6, 10, 17);

    @Getter
    private final PieceType pieceType = PieceType.KNIGHT;

    @Override
    public Set<Move> generateLegalMoves(Game game, int startSquare) {

        Board board = game.getBoard();
        Piece knight = board.pieceAt(startSquare)
                .filter(piece -> piece.isType(PieceType.KNIGHT))
                .orElseThrow(() -> new NoSuchElementException(String.format("There is no knight on square %s!", startSquare)));

        Set<Integer> legalOffsets = CANDIDATE_MOVE_OFFSETS.stream()
                .filter(offset -> isLegalOffset(board, knight, startSquare, offset))
                .collect(Collectors.toSet());

        Set<Move> legalMoves = legalOffsets.stream()
                .map(offset -> Move.builder().startSquare(startSquare).endSquare(startSquare + offset).build())
                .collect(Collectors.toSet());

        return legalMoves;

    }

    private boolean isLegalOffset(Board board, Piece knight, int startSquare, int offset) {
        Colour colour = knight.getColour();
        int targetSquare = startSquare + offset;
        if (!BoardUtils.isValidSquareCoordinate(targetSquare)) {
            return false;
        }
        if ((BoardUtils.isAFile(startSquare) && A_FILE_OFFSET_EXCEPTIONS.contains(offset)) ||
            (BoardUtils.isBFile(startSquare) && B_FILE_OFFSET_EXCEPTIONS.contains(offset)) ||
            (BoardUtils.isGFile(startSquare) && G_FILE_OFFSET_EXCEPTIONS.contains(offset)) ||
            (BoardUtils.isHFile(startSquare) && H_FILE_OFFSET_EXCEPTIONS.contains(offset))) {
            return false;
        }
        Optional<Piece> pieceOnTargetSquare = board.pieceAt(targetSquare);
        return pieceOnTargetSquare.isEmpty() || pieceOnTargetSquare.get().getColour().isOppositeColour(colour);
    }

}
