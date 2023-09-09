package com.kelseyde.calvin.service;

import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.board.Board;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.model.piece.Piece;
import com.kelseyde.calvin.service.generator.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class LegalMoveService {

    @Resource
    private List<LegalMoveGenerator> moveGenerators;

    public Set<Move> generateAllLegalMoves(Board board, Colour colour) {
        return IntStream.range(0, 64)
                .mapToObj(square -> generateLegalMovesForSquare(board, colour, square))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private Set<Move> generateLegalMovesForSquare(Board board, Colour colour, int square) {

        if (board.pieceAt(square).isEmpty() || !board.pieceAt(square).get().getColour().equals(colour)) {
            return Collections.emptySet();
        }

        Piece piece = board.pieceAt(square).get();

        LegalMoveGenerator moveGenerator = moveGenerators.stream()
                .filter(generator -> piece.getType().equals(generator.getPieceType()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("No move generator found for piece type %s", piece.getType())));

        return moveGenerator.generateLegalMoves(board, square);

    }


}
