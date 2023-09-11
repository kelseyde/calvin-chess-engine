package com.kelseyde.calvin.service;

import com.google.common.collect.Sets;
import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.game.Game;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.move.MoveType;
import com.kelseyde.calvin.service.generator.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class LegalMoveService {

    private final List<PseudoLegalMoveGenerator> pseudoLegalMoveGenerators = List.of(
            new PawnMoveGenerator(), new KnightMoveGenerator(), new BishopMoveGenerator(),
            new RookMoveGenerator(), new QueenMoveGenerator(), new KingMoveGenerator()
    );

    public Optional<Move> isLegalMove(Game game, Move move) {
        Colour turn = game.getTurn();
        int startSquare = move.getStartSquare();
        Set<Move> pseudoLegalMoves = generatePseudoLegalMoves(game, turn, startSquare);
        Optional<Move> pseudoLegalMove = pseudoLegalMoves.stream()
                .filter(pseudoLegal -> moveMatches(move, pseudoLegal))
                .findFirst();
        if (pseudoLegalMove.isEmpty()) {
            return Optional.empty();
        }
        return pseudoLegalMove
                .filter(m -> !isKingCapturable(game, m));
    }

    public Set<Integer> getAttackedSquares(Game game, Colour attackingColour) {
        return generateAllPseudoLegalMoves(game, attackingColour).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
    }

    private boolean isKingCapturable(Game game, Move move) {
        Board board = game.getBoard();
        Colour colour = game.getTurn();

        board.applyMove(move);
        Set<Integer> checkableSquares = MoveType.CASTLE.equals(move.getType()) ?
                move.getCastlingConfig().getKingTravelSquares() : Set.of(board.getKingSquare(colour));
        Set<Integer> attackedSquares = getAttackedSquares(game, colour.oppositeColour());
        boolean isKingCapturableOnNextMove = !Sets.intersection(checkableSquares, attackedSquares).isEmpty();
        board.unapplyMove(move);

        return isKingCapturableOnNextMove;
    }

    public Set<Move> generateAllPseudoLegalMoves(Game game, Colour colour) {
        Board board = game.getBoard();
        return IntStream.range(0, 64)
                .filter(square -> board.pieceAt(square).isPresent() && board.pieceAt(square).get().getColour().isSameColour(colour))
                .mapToObj(square -> generatePseudoLegalMoves(game, colour, square))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private Set<Move> generatePseudoLegalMoves(Game game, Colour colour, int square) {

        Piece piece = game.getBoard().pieceAt(square)
                .filter(p -> p.getColour().isSameColour(colour))
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("No piece of colour %s on square %s!", colour, square)));

        PseudoLegalMoveGenerator pseudoLegalMoveGenerator = pseudoLegalMoveGenerators.stream()
                .filter(generator -> piece.getType().equals(generator.getPieceType()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("No move generator found for piece type %s!", piece.getType())));

        return pseudoLegalMoveGenerator.generateLegalMoves(game, square);

    }

    private boolean moveMatches(Move a, Move b) {
        return a.getStartSquare() == b.getStartSquare()
                && a.getEndSquare() == b.getEndSquare()
                && a.getPromotionConfig().getPromotionPieceType() == b.getPromotionConfig().getPromotionPieceType();
    }

}
