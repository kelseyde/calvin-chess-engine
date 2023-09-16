package com.kelseyde.calvin.service.game.generator;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.move.MoveType;
import com.kelseyde.calvin.utils.BoardUtils;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KingMoveGenerator implements PseudoLegalMoveGenerator {

    // All the possible candidate move offsets for a king on a 1-dimensional board array, i.e., a king in the middle
    // of an empty board can move to the squares indicated by these offsets.
    private static final Set<Integer> CANDIDATE_MOVE_OFFSETS = Set.of(-9, -8, -7, -1, 1, 7, 8, 9);

    // The following sets are exceptions to the initial rule, in scenarios where the king is placed on the a or h-files.
    // These exceptions prevent the piece from 'wrapping' around to the other side of the board.
    private static final Set<Integer> A_FILE_OFFSET_EXCEPTIONS = Set.of(-9, -1, 7);
    private static final Set<Integer> H_FILE_OFFSET_EXCEPTIONS = Set.of(-7, 1, 9);

    @Getter
    private final PieceType pieceType = PieceType.KING;

    @Override
    public Set<Move> generatePseudoLegalMoves(Board board, int startSquare) {

        Piece king = board.getPieceAt(startSquare)
                .filter(piece -> piece.isType(PieceType.KING))
                .orElseThrow(() -> new NoSuchElementException(String.format("There is no king on square %s!", startSquare)));

        Set<Move> legalMoves = CANDIDATE_MOVE_OFFSETS.stream()
                .map(offset -> getLegalMoveForOffset(board, king, startSquare, offset))
                .flatMap(Optional::stream).collect(Collectors.toSet());

        checkKingsideCastling(board, king, startSquare).ifPresent(legalMoves::add);
        checkQueensideCastling(board, king, startSquare).ifPresent(legalMoves::add);

        return legalMoves;

    }

    private Optional<Move> getLegalMoveForOffset(Board board, Piece king, int startSquare, int offset) {
        Colour colour = king.getColour();
        int targetSquare = startSquare + offset;
        if (!BoardUtils.isValidSquareCoordinate(targetSquare)) {
            return Optional.empty();
        }
        if ((BoardUtils.isAFile(startSquare) && A_FILE_OFFSET_EXCEPTIONS.contains(offset)) ||
            (BoardUtils.isHFile(startSquare) && H_FILE_OFFSET_EXCEPTIONS.contains(offset))) {
            return Optional.empty();
        }
        Optional<Piece> pieceOnTargetSquare = board.getPieceAt(targetSquare);
        if (pieceOnTargetSquare.isEmpty()) {
            return Optional.of(createKingMove(startSquare, targetSquare).build());
        } else if (pieceOnTargetSquare.get().getColour().isOppositeColour(colour)) {
            return Optional.of(createKingMove(startSquare, targetSquare).isCapture(true).build());
        } else {
            return Optional.empty();
        }
    }

    private Optional<Move> checkKingsideCastling(Board board, Piece king, int startSquare) {
        Colour colour = king.getColour();

        int kingStartingSquare = getKingStartingSquare(colour);
        int kingTargetSquare = getKingsideCastlingKingSquare(colour);
        Set<Integer> kingTravelSquares = getKingsideCastlingTravelSquares(colour);

        int rookStartingSqure = getKingsideRookStartingSquare(colour);
        Set<Integer> rookTravelSquares = getKingsideCastlingRookTravelSquares(colour);

        boolean isKingsideCastlingDisallowed = board.getCastlingRights().get(colour).isKingSide();
        boolean isKingOnStartSquare = startSquare == kingStartingSquare;
        boolean isRookOnStartSquare = pieceIs(board, rookStartingSqure, colour, PieceType.ROOK);
        boolean travelSquaresEmpty = rookTravelSquares.stream()
                .allMatch(square -> board.getPieceAt(square).isEmpty());

        if (isKingsideCastlingDisallowed && isKingOnStartSquare && isRookOnStartSquare && travelSquaresEmpty) {
            return Optional.of(createKingMove(startSquare, kingTargetSquare)
                    .moveType(MoveType.CASTLE)
                    .rookStartSquare(getKingsideRookStartingSquare(colour))
                    .rookEndSquare(getKingsideRookCastlingSquare(colour))
                    .kingTravelSquares(kingTravelSquares)
                    .build());
        }
        return Optional.empty();
    }

    private Optional<Move> checkQueensideCastling(Board board, Piece king, int startSquare) {
        Colour colour = king.getColour();

        int kingStartingSquare = getKingStartingSquare(colour);
        int kingTargetSquare = getQueensideCastlingKingSquare(colour);
        Set<Integer> kingTravelSquares = getQueensideCastlingTravelSquares(colour);

        int rookStartingSquare = getQueensideRookStartingSquare(colour);
        Set<Integer> rookTravelSquares = getQueensideCastlingRookTravelSquares(colour);

        boolean isQueensideCastlingDisallowed = board.getCastlingRights().get(colour).isQueenSide();
        boolean isKingOnStartSquare = startSquare == kingStartingSquare;
        boolean isRookOnStartSquare = pieceIs(board, rookStartingSquare, colour, PieceType.ROOK);

        boolean travelSquaresEmpty = rookTravelSquares.stream()
                .allMatch(square -> board.getPieceAt(square).isEmpty());

        if (isQueensideCastlingDisallowed && isKingOnStartSquare && isRookOnStartSquare && travelSquaresEmpty) {
            return Optional.of(createKingMove(startSquare, kingTargetSquare)
                    .moveType(MoveType.CASTLE)
                    .rookStartSquare(getQueensideRookStartingSquare(colour))
                    .rookEndSquare(getQueensideRookCastlingSquare(colour))
                    .kingTravelSquares(kingTravelSquares)
                    .build());
        }
        return Optional.empty();
    }

    private Move.MoveBuilder createKingMove(int startSquare, int endSquare) {
        return moveBuilder()
                .startSquare(startSquare)
                .endSquare(endSquare)
                // Any king move (including castling) precludes castling rights for the remainder of the game.
                .negatesKingsideCastling(true)
                .negatesQueensideCastling(true);
    }

    private boolean pieceIs(Board board, int square, Colour colour, PieceType type) {
        return board.getPieceAt(square)
                .filter(piece -> piece.getColour().equals(colour) && piece.getType().equals(type))
                .isPresent();
    }

    private int getKingStartingSquare(Colour colour) {
        return Colour.WHITE.equals(colour) ? 4 : 60;
    }

    private int getKingsideRookStartingSquare(Colour colour) {
        return Colour.WHITE.equals(colour) ? 7 : 63;
    }

    private int getQueensideRookStartingSquare(Colour colour) {
        return Colour.WHITE.equals(colour) ? 0 : 56;
    }

    private int getKingsideCastlingKingSquare(Colour colour) {
        return Colour.WHITE.equals(colour) ? 6 : 62;
    }

    private int getQueensideCastlingKingSquare(Colour colour) {
        return Colour.WHITE.equals(colour) ? 2 : 58;
    }

    private int getKingsideRookCastlingSquare(Colour colour) {
        return Colour.WHITE.equals(colour) ? 5 : 61;
    }

    private int getQueensideRookCastlingSquare(Colour colour) {
        return Colour.WHITE.equals(colour) ? 3 : 59;
    }

    private Set<Integer> getKingsideCastlingRookTravelSquares(Colour colour) {
        return Colour.WHITE.equals(colour) ? Set.of(5, 6) : Set.of(61, 62);
    }

    private Set<Integer> getQueensideCastlingRookTravelSquares(Colour colour) {
        return Colour.WHITE.equals(colour) ? Set.of(1, 2, 3) : Set.of(57, 58, 59);
    }

    private Set<Integer> getKingsideCastlingTravelSquares(Colour colour) {
        return Colour.WHITE.equals(colour) ? Set.of(4, 5, 6) : Set.of(60, 61, 62);
    }

    private Set<Integer> getQueensideCastlingTravelSquares(Colour colour) {
        return Colour.WHITE.equals(colour) ? Set.of(2, 3, 4) : Set.of(58, 59, 60);
    }

}
