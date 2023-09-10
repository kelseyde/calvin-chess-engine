package com.kelseyde.calvin.service;

import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.Game;
import com.kelseyde.calvin.model.Piece;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.service.generator.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class LegalMoveService {

    private final List<PseudoLegalMoveGenerator> moveGenerators = List.of(
            new PawnMoveGenerator(), new KnightMoveGenerator(), new BishopMoveGenerator(),
            new RookMoveGenerator(), new QueenMoveGenerator(), new KingMoveGenerator()
    );

    public Set<Move> generateAllLegalMoves(Game game, Colour colour) {
        Board board = game.getBoard();
        return IntStream.range(0, 64)
                .filter(square -> board.pieceAt(square).isPresent() || !board.pieceAt(square).get().getColour().isSameColour(colour))
                .mapToObj(square -> generateLegalMovesForSquare(game, colour, square))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public boolean isLegalMove(Game game, Move move) {

        Colour turn = game.getTurn();
        int startSquare = move.getStartSquare();
        return generateLegalMovesForSquare(game, turn, startSquare).contains(move);

    }

    private Set<Move> generateLegalMovesForSquare(Game game, Colour colour, int square) {

        Piece piece = game.getBoard().pieceAt(square)
                .filter(p -> p.getColour().isSameColour(colour))
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("No piece of colour %s on square %s!", colour, square)));

        PseudoLegalMoveGenerator pseudoLegalMoveGenerator = moveGenerators.stream()
                .filter(generator -> piece.getType().equals(generator.getPieceType()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("No move generator found for piece type %s!", piece.getType())));

        return pseudoLegalMoveGenerator.generateLegalMoves(game, square);

    }

}
