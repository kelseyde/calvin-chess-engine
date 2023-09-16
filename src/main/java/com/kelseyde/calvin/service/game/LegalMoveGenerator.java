package com.kelseyde.calvin.service.game;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.move.MoveType;
import com.kelseyde.calvin.service.game.generator.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Evaluates the effect of a {@link Move} on a game. First checks if the move is legal. Then, checks if executing the
 * move results in the game ending, either due to checkmate or one of the draw conditions.
 */
@Slf4j
public class LegalMoveGenerator {

    private static final Map<PieceType, PseudoLegalMoveGenerator> PSEUDO_LEGAL_MOVE_GENERATORS = Map.of(
        PieceType.PAWN, new PawnMoveGenerator(),
        PieceType.KNIGHT, new KnightMoveGenerator(),
        PieceType.BISHOP, new BishopMoveGenerator(),
        PieceType.ROOK, new RookMoveGenerator(),
        PieceType.QUEEN, new QueenMoveGenerator(),
        PieceType.KING, new KingMoveGenerator()
    );

    public Set<Move> generateLegalMoves(Board board) {
        Colour colour = board.getTurn();
        return board.getPiecePositions(colour).stream()
                .flatMap(square -> generatePseudoLegalMoves(board, colour, square).stream())
                .filter(pseudoLegalMove -> isFullyLegal(board, pseudoLegalMove))
                .collect(Collectors.toSet());
    }

    public Set<Move> generateAllPseudoLegalMoves(Board board, Colour colour) {
        Set<Integer> pieceSquares = board.getPiecePositions(colour);
        return pieceSquares.stream()
                .map(square -> generatePseudoLegalMoves(board, colour, square))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private Set<Move> generatePseudoLegalMoves(Board board, Colour colour, int square) {

        Piece piece = board.getPieceAt(square)
                .filter(p -> p.getColour().isSameColour(colour))
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("No piece of colour %s on square %s!", colour, square)));

        PseudoLegalMoveGenerator pseudoLegalMoveGenerator = PSEUDO_LEGAL_MOVE_GENERATORS.get(piece.getType());

        return pseudoLegalMoveGenerator.generatePseudoLegalMoves(board, square);

    }

    private boolean isFullyLegal(Board board, Move move) {
        Colour colour = board.getTurn();

        Board boardCopy = board.copy();
        boardCopy.applyMove(move);

        Set<Integer> checkableSquares = MoveType.CASTLE.equals(move.getMoveType()) ?
                move.getKingTravelSquares() : Set.of(getKingSquare(boardCopy, colour));
        Set<Integer> attackedSquares = generateAllPseudoLegalMoves(boardCopy, colour.oppositeColour()).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());

        Set<Integer> intersection = new HashSet<>(checkableSquares);
        intersection.retainAll(attackedSquares);
        boolean isLegalMove = intersection.isEmpty();
        if (isLegalMove && isCheck(boardCopy, move)) {
            move.setCheck(true);
        }
        return isLegalMove;

    }

    private boolean isCheck(Board board, Move move) {
        // Additionally calculate whether the move is a check, store this info in Move.
        int opponentKingSquare = getKingSquare(board, board.getTurn());
        Set<Integer> attackingSquares = generateAllPseudoLegalMoves(board, board.getTurn().oppositeColour()).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        return attackingSquares.contains(opponentKingSquare);
    }

    private Integer getKingSquare(Board board, Colour colour) {
        return Arrays.asList(board.getSquares())
                .indexOf(new Piece(colour, PieceType.KING));
    }

}
