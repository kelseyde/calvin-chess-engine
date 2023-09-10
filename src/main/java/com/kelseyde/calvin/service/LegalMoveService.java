package com.kelseyde.calvin.service;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Game;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.service.generator.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
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
                .filter(legalMove -> legalMove.getStartSquare() == move.getStartSquare() && legalMove.getEndSquare() == move.getEndSquare())
                .findFirst();
        if (pseudoLegalMove.isEmpty()) {
            return Optional.empty();
        }
        return pseudoLegalMove
                .filter(m -> isKingInCheck(game, m));
    }

    public Optional<Move> getLegalMove(Game game, Move move) {

        Colour turn = game.getTurn();
        int startSquare = move.getStartSquare();
        Set<Move> legalMoves = generatePseudoLegalMoves(game, turn, startSquare);
        return legalMoves.stream()
                .filter(legalMove ->
                        legalMove.getStartSquare() == move.getStartSquare() &&
                        legalMove.getEndSquare() == move.getEndSquare())
                .findFirst();

    }

    private boolean isKingInCheck(Game game, Move move) {
        Board board = game.getBoard();
        board.applyMove(move);
        int kingSquare = board.getKingSquare(game.getTurn());
        Set<Move> opponentMoves = generateAllPseudoLegalMoves(game, game.getTurn().oppositeColour());
        boolean isKingCapturableOnNextMove = opponentMoves.stream().anyMatch(opponentMove -> opponentMove.getEndSquare() == kingSquare);
        board.unapplyMove(move);
        return isKingCapturableOnNextMove;
    }

    public Set<Move> generateAllPseudoLegalMoves(Game game, Colour colour) {
        Board board = game.getBoard();
        return IntStream.range(0, 64)
                .filter(square -> board.pieceAt(square).isPresent() || !board.pieceAt(square).get().getColour().isSameColour(colour))
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

}
