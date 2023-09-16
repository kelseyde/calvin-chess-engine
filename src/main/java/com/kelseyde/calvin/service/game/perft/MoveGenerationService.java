package com.kelseyde.calvin.service.game.perft;

import com.kelseyde.calvin.model.Game;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.service.game.LegalMoveGenerator;
import com.kelseyde.calvin.utils.MoveUtils;

import java.util.List;
import java.util.Set;

public class MoveGenerationService {

    private final LegalMoveGenerator legalMoveGenerator = new LegalMoveGenerator();

    public int generateMoves(Game game, int depth) {
        if (depth == 0) {
            return 1;
        }
        int totalMoveCount = 0;
        Set<Move> moves = legalMoveGenerator.generateLegalMoves(game);
        log(game, depth, moves);
        for (Move move : moves) {
            game.applyMove(move);
            totalMoveCount += generateMoves(game, depth - 1);
            game.unapplyLastMove();
        }
        return totalMoveCount;
    }

    private void log(Game game, int depth, Set<Move> legalMoves) {

        String lastMoveString = null;
        if (!game.getMoveHistory().isEmpty()) {
            Move lastMove = game.getMoveHistory().peek();
            lastMoveString = String.format("%s %s",
                    MoveUtils.toNotation(lastMove.getStartSquare()), MoveUtils.toNotation(lastMove.getEndSquare()));
        }
        List<String> legalMovesStrings = legalMoves.stream()
                .map(move -> String.format("%s %s",
                        MoveUtils.toNotation(move.getStartSquare()), MoveUtils.toNotation(move.getEndSquare())))
                .toList();
        System.out.printf("Depth: %s, Last move: %s, Legal moves: %s%n", depth, lastMoveString, legalMovesStrings);

    }

}
