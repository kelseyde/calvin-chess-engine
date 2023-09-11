package com.kelseyde.calvin.service.generator;

import com.kelseyde.calvin.model.*;
import com.kelseyde.calvin.model.game.Game;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.move.MoveType;
import com.kelseyde.calvin.model.move.config.CastlingConfig;
import com.kelseyde.calvin.utils.BoardUtils;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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
    public Set<Move> generateLegalMoves(Game game, int startSquare) {

        Board board = game.getBoard();
        Piece king = board.pieceAt(startSquare)
                .filter(piece -> piece.isType(PieceType.KING))
                .orElseThrow(() -> new NoSuchElementException(String.format("There is no king on square %s!", startSquare)));

        Set<Move> legalMoves = new HashSet<>();

        Set<Move> legalStandardMoves = CANDIDATE_MOVE_OFFSETS.stream()
                .filter(offset -> isLegalOffset(board, king, startSquare, offset))
                .map(offset -> createKingMove(startSquare, startSquare + offset).build())
                .collect(Collectors.toSet());
        legalMoves.addAll(legalStandardMoves);

        checkKingsideCastling(game, king, startSquare).ifPresent(legalMoves::add);
        checkQueensideCastling(game, king, startSquare).ifPresent(legalMoves::add);

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

    private Optional<Move> checkKingsideCastling(Game game, Piece king, int startSquare) {
        Colour colour = king.getColour();
        Board board = game.getBoard();

        int kingStartingSquare = getKingStartingSquare(colour);
        int kingTargetSquare = getKingsideCastlingKingSquare(colour);
        Set<Integer> kingTravelSquares = getKingsideCastlingTravelSquares(colour);

        int rookStartingSqure = getKingsideRookStartingSquare(colour);
        Set<Integer> rookTravelSquares = getKingsideCastlingRookTravelSquares(colour);

        boolean isKingsideCastlingDisallowed = game.getCastlingRights().get(colour).isKingSide();
        boolean isKingOnStartSquare = startSquare == kingStartingSquare;
        boolean isRookOnStartSquare = board.pieceIs(rookStartingSqure, colour, PieceType.ROOK);
        boolean travelSquaresEmpty = rookTravelSquares.stream().allMatch(board::isSquareEmpty);

        if (isKingsideCastlingDisallowed && isKingOnStartSquare && isRookOnStartSquare && travelSquaresEmpty) {
            return Optional.of(createKingMove(startSquare, kingTargetSquare)
                    .type(MoveType.CASTLE)
                    .castlingConfig(CastlingConfig.builder()
                            .rookStartSquare(getKingsideRookStartingSquare(colour))
                            .rookEndSquare(getKingsideRookCastlingSquare(colour))
                            .kingTravelSquares(kingTravelSquares)
                            .build())
                    .build());
        }
        return Optional.empty();
    }

    private Optional<Move> checkQueensideCastling(Game game, Piece king, int startSquare) {
        Colour colour = king.getColour();
        Board board = game.getBoard();

        int kingStartingSquare = getKingStartingSquare(colour);
        int kingTargetSquare = getQueensideCastlingKingSquare(colour);
        Set<Integer> kingTravelSquares = getQueensideCastlingTravelSquares(colour);

        int rookStartingSquare = getQueensideRookStartingSquare(colour);
        Set<Integer> rookTravelSquares = getQueensideCastlingRookTravelSquares(colour);

        boolean isQueensideCastlingDisallowed = game.getCastlingRights().get(colour).isQueenSide();
        boolean isKingOnStartSquare = startSquare == kingStartingSquare;
        boolean isRookOnStartSquare = board.pieceIs(rookStartingSquare, colour, PieceType.ROOK);
        boolean travelSquaresEmpty = rookTravelSquares.stream().allMatch(board::isSquareEmpty);

        if (isQueensideCastlingDisallowed && isKingOnStartSquare && isRookOnStartSquare && travelSquaresEmpty) {
            return Optional.of(createKingMove(startSquare, kingTargetSquare)
                    .type(MoveType.CASTLE)
                    .castlingConfig(CastlingConfig.builder()
                            .rookStartSquare(getQueensideRookStartingSquare(colour))
                            .rookEndSquare(getQueensideRookCastlingSquare(colour))
                            .kingTravelSquares(kingTravelSquares)
                            .build())
                    .build());
        }
        return Optional.empty();
    }

    private Move.MoveBuilder createKingMove(int startSquare, int endSquare) {
        return Move.builder()
                .startSquare(startSquare)
                .endSquare(endSquare)
                // Any king move (including castling) precludes castling rights for the remainder of the game.
                .castlingConfig(CastlingConfig.builder()
                        .negatesKingsideCastling(true)
                        .negatesQueensideCastling(true)
                        .build());
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
