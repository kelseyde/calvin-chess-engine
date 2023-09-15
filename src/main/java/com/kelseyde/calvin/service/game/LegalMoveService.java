package com.kelseyde.calvin.service.game;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.PieceType;
import com.kelseyde.calvin.model.game.Game;
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
public class LegalMoveService {

    private final List<PseudoLegalMoveGenerator> pseudoLegalMoveGenerators = List.of(
            new PawnMoveGenerator(), new KnightMoveGenerator(), new BishopMoveGenerator(),
            new RookMoveGenerator(), new QueenMoveGenerator(), new KingMoveGenerator()
    );

    public Optional<Move> calculateLegalMove(Game game, Move move) {
        return generateLegalMoves(game).stream()
                .filter(move::moveMatches)
                .findAny();
    }

    public Set<Move> generateLegalMoves(Game game) {
        Colour colour = game.getTurn();
        Set<Integer> pieceSquares = game.getBoard().getPiecePositions(colour);

        Set<Move> pseudoLegal = pieceSquares.stream()
                .flatMap(square -> generatePseudoLegalMoves(game, colour, square).stream())
                .collect(Collectors.toSet());

        Set<Move> legal = pseudoLegal.stream()
                .filter(pseudoLegalMove -> !isKingCapturable(game, pseudoLegalMove))
                .collect(Collectors.toSet());

        Set<Move> checkApplied = legal.stream()
                .map(legalMove -> calculateCheck(game, legalMove))
                .collect(Collectors.toSet());

        return checkApplied;
    }

    public Set<Move> generateAllPseudoLegalMoves(Game game, Colour colour) {
        Set<Integer> pieceSquares = game.getBoard().getPiecePositions(colour);
        return pieceSquares.stream()
                .map(square -> generatePseudoLegalMoves(game, colour, square))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private Set<Move> generatePseudoLegalMoves(Game game, Colour colour, int square) {

        Piece piece = game.getBoard().getPieceAt(square)
                .filter(p -> p.getColour().isSameColour(colour))
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("No piece of colour %s on square %s!", colour, square)));

        PseudoLegalMoveGenerator pseudoLegalMoveGenerator = pseudoLegalMoveGenerators.stream()
                .filter(generator -> piece.getType().equals(generator.getPieceType()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("No move generator found for piece type %s!", piece.getType())));

        return pseudoLegalMoveGenerator.generatePseudoLegalMoves(game, square);

    }

    private boolean isKingCapturable(Game game, Move move) {
        Board board = game.getBoard();
        Colour colour = game.getTurn();

        game.applyMove(move);

        Set<Integer> checkableSquares = MoveType.CASTLE.equals(move.getMoveType()) ?
                move.getKingTravelSquares() : Set.of(getKingSquare(board, colour));
        Set<Integer> attackedSquares = generateAllPseudoLegalMoves(game, colour.oppositeColour()).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());

        Set<Integer> intersection = new HashSet<>(checkableSquares);
        intersection.retainAll(attackedSquares);
        boolean isKingCapturable = !intersection.isEmpty();

        game.unapplyLastMove();

        return isKingCapturable;
    }

    private Move calculateCheck(Game game, Move move) {
        Board board = game.getBoard();
        Colour colour = game.getTurn();

        game.applyMove(move);

        int opponentKingSquare = getKingSquare(board, colour.oppositeColour());
        Set<Integer> attackingSquares = generateAllPseudoLegalMoves(game, colour).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        boolean isCheck = attackingSquares.contains(opponentKingSquare);
        move.setCheck(isCheck);

        game.unapplyLastMove();
        return move;
    }

    private Integer getKingSquare(Board board, Colour colour) {
        return Arrays.asList(board.getSquares())
                .indexOf(new Piece(colour, PieceType.KING));
    }

}
