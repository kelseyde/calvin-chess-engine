package com.kelseyde.calvin.service.generator;

import com.kelseyde.calvin.model.*;
import com.kelseyde.calvin.utils.BoardUtils;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class PawnMoveGenerator implements PseudoLegalMoveGenerator {

    @Getter
    private final PieceType pieceType = PieceType.PAWN;

    @Override
    public Set<Move> generateLegalMoves(Game game, int startSquare) {

        Board board = game.getBoard();
        Piece pawn = board.pieceAt(startSquare)
                .filter(piece -> piece.isType(PieceType.PAWN))
                .orElseThrow(() -> new NoSuchElementException(String.format("There is no pawn on square %s!", startSquare)));

        Set<Move> legalMoves = new HashSet<>();
        legalMoves.addAll(getLegalStandardMoves(board, pawn, startSquare));
        getLegalDoubleMove(board, pawn, startSquare).ifPresent(legalMoves::add);
        legalMoves.addAll(getLegalCaptures(game, pawn, startSquare));

        return legalMoves;

    }

    private Set<Move> getLegalStandardMoves(Board board, Piece piece, int startSquare) {

        Set<Move> legalMoves = new HashSet<>();
        int standardMoveOffset = getStandardMoveOffset(piece.getColour());
        int standardMoveSquare = startSquare + standardMoveOffset;

        if (BoardUtils.isValidSquareCoordinate(standardMoveSquare)) {
            Optional<Piece> pieceOnStandardMoveSquare = board.pieceAt(standardMoveSquare);
            if (pieceOnStandardMoveSquare.isEmpty()) {
                if (isPromotingMove(piece, startSquare)) {
                    // Covering piece promotion
                    legalMoves.addAll(getPromotionMoves(startSquare, standardMoveSquare));
                } else {
                    // Covering standard move
                    legalMoves.add(Move.builder().startSquare(startSquare).endSquare(standardMoveSquare).build());
                }
            }
        }
        return legalMoves;

    }

    private Optional<Move> getLegalDoubleMove(Board board, Piece piece, int startSquare) {

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
                    Move move = Move.builder().startSquare(startSquare).endSquare(doubleMoveSquare).build();
                    move.setEnPassantTargetSquare(standardMoveSquare);
                    return Optional.of(move);
                }
            }

        }
        return Optional.empty();

    }

    private Set<Move> getLegalCaptures(Game game, Piece piece, int startSquare) {

        Set<Move> legalCaptures = new HashSet<>();

        for (int offset : getCaptureOffsets(piece.getColour())) {
            if ((BoardUtils.isAFile(startSquare) && offset == getAFileCaptureOffsetException(piece.getColour())) ||
                (BoardUtils.isHFile(startSquare) && offset == getHFileCaptureOffsetException(piece.getColour()))) {
                continue;
            }

            // Covering standard capture offset
            int targetCaptureSquare = startSquare + offset;
            Optional<Piece> pieceOnTargetSquare = game.getBoard().pieceAt(targetCaptureSquare);
            if (pieceOnTargetSquare.isPresent() && pieceOnTargetSquare.get().getColour().isOppositeColour(piece.getColour())) {
                if (isPromotingMove(piece, startSquare)) {
                    // Covering capturing + piece promotion
                   legalCaptures.addAll(getPromotionMoves(startSquare, targetCaptureSquare));
                } else {
                    // Covering standard capture
                    legalCaptures.add(Move.builder().startSquare(startSquare).endSquare(targetCaptureSquare).build());
                }

            }

            // Covering en passant
            if (pieceOnTargetSquare.isEmpty() && game.getEnPassantTargetSquare() == targetCaptureSquare) {
                Move move = Move.builder().startSquare(startSquare).endSquare(targetCaptureSquare).build();
                move.setEnPassantCapture(true);
                legalCaptures.add(move);
            }
        }

        return legalCaptures;

    }

    private boolean isPromotingMove(Piece piece, int startSquare) {
        return (Colour.WHITE.equals(piece.getColour()) && BoardUtils.isSeventhRank(startSquare)) ||
                (Colour.BLACK.equals(piece.getColour()) && BoardUtils.isSecondRank(startSquare));
    }

    private Set<Move> getPromotionMoves(int startSquare, int targetSquare) {
        return Set.of(
                Move.builder().startSquare(startSquare).endSquare(targetSquare).promotionPieceType(PieceType.QUEEN).build(),
                Move.builder().startSquare(startSquare).endSquare(targetSquare).promotionPieceType(PieceType.ROOK).build(),
                Move.builder().startSquare(startSquare).endSquare(targetSquare).promotionPieceType(PieceType.BISHOP).build(),
                Move.builder().startSquare(startSquare).endSquare(targetSquare).promotionPieceType(PieceType.KNIGHT).build()
        );
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
