package com.kelseyde.calvin.service.generator;

import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.board.Board;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.piece.Piece;
import com.kelseyde.calvin.model.piece.PieceType;
import com.kelseyde.calvin.utils.BoardUtils;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PawnMoveGenerator implements LegalMoveGenerator {

    @Getter
    private final PieceType pieceType = PieceType.PAWN;

    @Override
    public Set<Move> generateLegalMoves(Board board, int startSquare) {

        Piece pawn = board.pieceAt(startSquare)
                .filter(piece -> piece.isType(PieceType.PAWN))
                .orElseThrow(() -> new NoSuchElementException(String.format("There is no pawn on square %s!", startSquare)));

        return generateLegalOffsets(board, pawn, startSquare).stream()
                .map(offset -> new Move(startSquare, startSquare + offset))
                .collect(Collectors.toSet());

    }

    private Set<Integer> generateLegalOffsets(Board board, Piece piece, int startSquare) {

        Set<Integer> legalOffsets = new HashSet<>();
        checkStandardMove(board, piece, startSquare).ifPresent(legalOffsets::add);
        checkDoubleMove(board, piece, startSquare).ifPresent(legalOffsets::add);
        legalOffsets.addAll(checkCaptures(board, piece, startSquare));
        return legalOffsets;

    }

    private Optional<Integer> checkStandardMove(Board board, Piece piece, int startSquare) {

        int standardMoveOffset = getStandardMoveOffset(piece.getColour());
        int standardMoveSquare = startSquare + standardMoveOffset;

        if (BoardUtils.isValidSquareCoordinate(standardMoveSquare)) {
            Optional<Piece> pieceOnStandardMoveSquare = board.pieceAt(standardMoveSquare);
            if (pieceOnStandardMoveSquare.isEmpty()) {
                return Optional.of(standardMoveOffset);
            }
        }
        return Optional.empty();

    }

    private Optional<Integer> checkDoubleMove(Board board, Piece piece, int startSquare) {

        if ((Colour.WHITE.equals(piece.getColour()) && BoardUtils.isSecondRank(startSquare)) ||
            (Colour.BLACK.equals(piece.getColour()) && BoardUtils.isSeventhRank(startSquare))) {

            int standardMoveOffset = getStandardMoveOffset(piece.getColour());
            int standardMoveSquare = startSquare + standardMoveOffset;

            int doubleMoveOffset = getDoubleMoveOffset(piece.getColour());
            int doubleMoveSquare = startSquare + doubleMoveOffset;

            if (BoardUtils.isValidSquareCoordinate(doubleMoveSquare)) {
                Optional<Piece> pieceOnStandardMoveSquare = board.pieceAt(standardMoveSquare);
                Optional<Piece> pieceOnDoubleMoveSquare = board.pieceAt(doubleMoveSquare);
                if (pieceOnStandardMoveSquare.isEmpty() && pieceOnDoubleMoveSquare.isEmpty()) {
                    return Optional.of(doubleMoveOffset);
                }
            }

        }
        return Optional.empty();

    }

    private Set<Integer> checkCaptures(Board board, Piece piece, int startSquare) {
        return getCaptureOffsets(piece.getColour()).stream()
                .filter(offset ->
                        !(BoardUtils.isAFile(startSquare) && offset == getAFileCaptureOffsetException(piece.getColour())) &&
                        !(BoardUtils.isHFile(startSquare) && offset == getHFileCaptureOffsetException(piece.getColour())))
                .filter(offset ->
                        BoardUtils.isValidSquareCoordinate(startSquare + offset) &&
                        board.pieceAt(startSquare + offset)
                        .filter(capturePiece -> capturePiece.getColour().isOppositeColour(piece.getColour()))
                        .isPresent())
                .collect(Collectors.toSet());
    }

    private int getStandardMoveOffset(Colour colour) {
        return Colour.WHITE.equals(colour) ? 8 : -8;
    }

    private int getDoubleMoveOffset(Colour colour) {
        return Colour.WHITE.equals(colour) ? 16 : -16;
    }

    private Set<Integer> getCaptureOffsets(Colour colour) {
        return Colour.WHITE.equals(colour) ? Set.of(7, 9) : Set.of(-7, -9);
    }

    private int getAFileCaptureOffsetException(Colour colour) {
        return Colour.WHITE.equals(colour) ? 7 : -9;
    }

    private int getHFileCaptureOffsetException(Colour colour) {
        return Colour.WHITE.equals(colour) ? 9 : -7;
    }

}
