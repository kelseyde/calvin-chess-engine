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

    private final List<PseudoLegalMoveGenerator> pseudoLegalMoveGenerators = List.of(
            new PawnMoveGenerator(), new KnightMoveGenerator(), new BishopMoveGenerator(),
            new RookMoveGenerator(), new QueenMoveGenerator(), new KingMoveGenerator()
    );

    public Set<Move> generateLegalMoves(Board board) {
        Colour colour = board.getTurn();
        Set<Integer> pieceSquares = board.getPiecePositions(colour);

        Set<Move> pseudoLegal = pieceSquares.stream()
                .flatMap(square -> generatePseudoLegalMoves(board, colour, square).stream())
                .collect(Collectors.toSet());

        Set<Move> legal = pseudoLegal.stream()
                .filter(pseudoLegalMove -> !isKingCapturable(board, pseudoLegalMove))
                .collect(Collectors.toSet());

        Set<Move> checkApplied = legal.stream()
                .map(legalMove -> calculateCheck(board, legalMove))
                .collect(Collectors.toSet());

        return checkApplied;
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

        PseudoLegalMoveGenerator pseudoLegalMoveGenerator = pseudoLegalMoveGenerators.stream()
                .filter(generator -> piece.getType().equals(generator.getPieceType()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("No move generator found for piece type %s!", piece.getType())));

        return pseudoLegalMoveGenerator.generatePseudoLegalMoves(board, square);

    }

    private boolean isKingCapturable(Board board, Move move) {
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
        return !intersection.isEmpty();

    }

    private Move calculateCheck(Board board, Move move) {
        Colour colour = board.getTurn();

        // TODO refactor to one method with isKingCapturable
        Board boardCopy = board.copy();
        boardCopy.applyMove(move);

        int opponentKingSquare = getKingSquare(boardCopy, colour.oppositeColour());
        Set<Integer> attackingSquares = generateAllPseudoLegalMoves(boardCopy, colour).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        boolean isCheck = attackingSquares.contains(opponentKingSquare);
        move.setCheck(isCheck);

        return move;
    }

    private Integer getKingSquare(Board board, Colour colour) {
        return Arrays.asList(board.getSquares())
                .indexOf(new Piece(colour, PieceType.KING));
    }

}
