package com.kelseyde.calvin.service.game.generator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.utils.BoardUtils;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.BitSet;
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
    public Set<Move> generatePseudoLegalMoves(Board board, int startSquare) {

        Piece knight = board.getPieceAt(startSquare)
                .filter(piece -> piece.isType(PieceType.KNIGHT))
                .orElseThrow(() -> new NoSuchElementException(String.format("There is no knight on square %s!", startSquare)));

        return CANDIDATE_MOVE_OFFSETS.stream()
                .map(offset -> getLegalMoveForOffset(board, knight, startSquare, offset))
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());

    }

    private Optional<Move> getLegalMoveForOffset(Board board, Piece knight, int startSquare, int offset) {
        Colour colour = knight.getColour();
        int targetSquare = startSquare + offset;
        if (!BoardUtils.isValidSquareCoordinate(targetSquare)) {
            return Optional.empty();
        }
        if ((BoardUtils.isAFile(startSquare) && A_FILE_OFFSET_EXCEPTIONS.contains(offset)) ||
            (BoardUtils.isBFile(startSquare) && B_FILE_OFFSET_EXCEPTIONS.contains(offset)) ||
            (BoardUtils.isGFile(startSquare) && G_FILE_OFFSET_EXCEPTIONS.contains(offset)) ||
            (BoardUtils.isHFile(startSquare) && H_FILE_OFFSET_EXCEPTIONS.contains(offset))) {
            return Optional.empty();
        }
        Optional<Piece> pieceOnTargetSquare = board.getPieceAt(targetSquare);
        if (pieceOnTargetSquare.isEmpty()) {
            return Optional.of(moveBuilder()
                    .startSquare(startSquare)
                    .endSquare(targetSquare)
                    .build());
        } else if (pieceOnTargetSquare.get().getColour().isOppositeColour(colour)) {
            return Optional.of(moveBuilder()
                    .startSquare(startSquare)
                    .endSquare(targetSquare)
                    .isCapture(true)
                    .build());
        } else {
            return Optional.empty();
        }
    }

}
