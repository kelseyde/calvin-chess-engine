package com.kelseyde.calvin.movegeneration.result;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.GameState;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.bitboard.Bitwise;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.search.Search;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class ResultCalculator {

    private final MoveGenerator moveGenerator = new MoveGenerator();

    public GameResult calculateResult(Board board) {
        List<Move> legalMoves = moveGenerator.generateMoves(board);
        if (legalMoves.isEmpty()) {
            if (moveGenerator.isCheck(board, board.isWhiteToMove())) {
                return board.isWhiteToMove() ? GameResult.BLACK_WINS_BY_CHECKMATE : GameResult.WHITE_WINS_BY_CHECKMATE;
            } else {
                return GameResult.DRAW_BY_STALEMATE;
            }
        }
        if (isThreefoldRepetition(board)) {
            return GameResult.DRAW_BY_REPETITION;
        }
        if (isInsufficientMaterial(board)) {
            return GameResult.DRAW_BY_INSUFFICIENT_MATERIAL;
        }
        if (isFiftyMoveRule(board)) {
            return GameResult.DRAW_BY_FIFTY_MOVE_RULE;
        }
        return GameResult.IN_PROGRESS;
    }

    /**
     * Check for an 'effective' draw, which treats a single repetition of the position as a draw.
     * This is used during {@link Search} to quickly check for a draw; it will lead to some errors in edge cases, but the
     * gamble is that the boost in search speed is worth the potential cost.
     */
    public boolean isEffectiveDraw(Board board) {
        return isDoubleRepetition(board) || isFiftyMoveRule(board) || isInsufficientMaterial(board);
    }

    private boolean isThreefoldRepetition(Board board) {

        int repetitionCount = 0;
        long zobrist = board.getGameState().getZobristKey();
        Iterator<GameState> iterator = board.getGameStateHistory().descendingIterator();
        while (iterator.hasNext()) {
            GameState gameState = iterator.next();
            if (gameState.getZobristKey() == zobrist) {
                repetitionCount += 1;
            }
        }
        return repetitionCount >= 2;

    }

    private boolean isDoubleRepetition(Board board) {

        long zobrist = board.getGameState().getZobristKey();
        Iterator<GameState> iterator = board.getGameStateHistory().descendingIterator();
        while (iterator.hasNext()) {
            GameState gameState = iterator.next();
            if (gameState.getZobristKey() == zobrist) {
                return true;
            }
        }
        return false;

    }

    private boolean isInsufficientMaterial(Board board) {
        if (board.getWhitePawns() != 0 || board.getWhiteRooks() != 0 || board.getWhiteQueens() != 0
                || board.getBlackPawns() != 0 || board.getBlackRooks() != 0 || board.getBlackQueens() != 0) {
            return false;
        }
        long whitePieces = board.getWhiteKnights() | board.getWhiteBishops();
        long blackPieces = board.getBlackKnights() |  board.getBlackBishops();

        return (Bitwise.countBits(whitePieces) == 0 || Bitwise.countBits(whitePieces) == 1)
                && (Bitwise.countBits(blackPieces) == 0 || Bitwise.countBits(blackPieces) == 1);
    }

    private boolean isFiftyMoveRule(Board board) {
        return board.getGameState().getFiftyMoveCounter() >= 100;
    }

}
