package com.kelseyde.calvin.tuning.copy;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.evaluation.Evaluator;
import com.kelseyde.calvin.evaluation.score.PieceValues;
import com.kelseyde.calvin.movegeneration.MoveGeneration;
import com.kelseyde.calvin.movegeneration.MoveGeneration.MoveFilter;
import com.kelseyde.calvin.movegeneration.MoveGenerator;
import com.kelseyde.calvin.movegeneration.result.ResultCalculator;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.search.SearchResult;
import com.kelseyde.calvin.search.moveordering.MoveOrdering;
import com.kelseyde.calvin.search.moveordering.StaticExchangeEvaluator;
import com.kelseyde.calvin.search.transposition.HashEntry;
import com.kelseyde.calvin.search.transposition.HashFlag;
import com.kelseyde.calvin.search.transposition.TranspositionTable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Iterative deepening is a search strategy that does a full search at a depth of 1 ply, then a full search at 2 ply,
 * then 3 ply and so on, until the time limit is exhausted. In case the timeout is reached in the middle of an iteration,
 * the search can still fall back on the best move found in the previous iteration. By prioritising searching the best
 * move found in the previous iteration, as well as the other ordering heuristics in the {@link MoveOrderer2} -- and by
 * using a {@link TranspositionTable} -- the iterative approach is much more efficient than it might sound.
 *
 * @see <a href="https://www.chessprogramming.org/Iterative_Deepening">Chess Programming Wiki</a>
 */
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Searcher2 implements Search {

    static final int MAX_DEPTH = 256;
    static final int MIN_EVAL = Integer.MIN_VALUE + 1;
    static final int MAX_EVAL = Integer.MAX_VALUE - 1;
    static final int MATE_SCORE = 1000000;
    static final int DRAW_SCORE = 0;
    static final int ASP_MARGIN = 50;
    static final int ASP_FAIL_MARGIN = 150;
    static final int NMP_DEPTH = 3;
    static final int FP_DEPTH = 4;
    static final int RFP_DEPTH = 4;
    static final int LMR_DEPTH = 3;
    static final int NMP_MARGIN = 50;
    static final int DP_MARGIN = 140;
    static final int[] FP_MARGIN = new int[] { 0, 170, 260, 450, 575 };
    static final int[] RFP_MARGIN = new int[] { 0, 120, 240, 360, 480 };

    MoveGeneration moveGenerator;
    MoveOrdering moveOrderer;
    Evaluation evaluator;
    StaticExchangeEvaluator see;
    TranspositionTable2 transpositionTable;
    ResultCalculator resultCalculator;

    Board board;
    Instant timeout;
    boolean cancelled;
    SearchResult resultCurrentDepth;
    SearchResult result;

    public Searcher2(Board board) {
        init(board);
    }

    public Searcher2(Board board, TranspositionTable2 transpositionTable) {
        this.board = board;
        this.transpositionTable = transpositionTable;
        this.moveGenerator = new MoveGenerator();
        this.moveOrderer = new MoveOrderer2();
        this.see = new StaticExchangeEvaluator();
        this.resultCalculator = new ResultCalculator();
        this.evaluator = new Evaluator2(board);
    }

    @Override
    public void init(Board board) {
        this.board = board;
        if (this.transpositionTable == null) {
            this.transpositionTable = new TranspositionTable2();
        } else {
            this.transpositionTable.clear();
        }
        this.moveGenerator = new MoveGenerator();
        this.moveOrderer = new MoveOrderer2();
        this.see = new StaticExchangeEvaluator();
        this.resultCalculator = new ResultCalculator();
        this.evaluator = new Evaluator(board);
    }

    @Override
    public void setPosition(Board board) {
        this.board = board;
        this.evaluator = new Evaluator(board);
    }

    /**
     * Search for the best move in the current {@link Board} position.
     * @param duration How long we should search
     * @return A {@link SearchResult} containing the best move and the current evaluation.
     */
    @Override
    public SearchResult search(Duration duration) {

        evaluator.init(board);
        timeout = Instant.now().plus(duration);
        result = null;
        resultCurrentDepth = null;
        cancelled = false;

        int currentDepth = 1;
        int alpha = MIN_EVAL;
        int beta = MAX_EVAL;
        int retryMultiplier = 0;

        while (!isCancelled()  && currentDepth < MAX_DEPTH) {
            resultCurrentDepth = null;

            int eval = search(currentDepth, 0, alpha, beta, true);

            if (resultCurrentDepth != null) {
                result = resultCurrentDepth;
            }

            if (isCancelled() || isCheckmateFoundAtCurrentDepth(currentDepth)) {
                // Exit early if time runs out, or we already found forced mate
                break;
            }

            if (eval <= alpha) {
                // The result is less than alpha, so search again at the same depth with an expanded aspiration window.
                retryMultiplier++;
                alpha -= ASP_FAIL_MARGIN * retryMultiplier;
                beta += ASP_MARGIN;
                continue;
            }
            if (eval >= beta) {
                // The result is greater than alpha, so search again at the same depth with an expanded aspiration window.
                retryMultiplier++;
                beta += ASP_FAIL_MARGIN * retryMultiplier;
                alpha -= ASP_MARGIN;
                continue;
            }

            alpha = eval - ASP_MARGIN;
            beta = eval + ASP_MARGIN;
            retryMultiplier = 0;

            currentDepth++;
        }

        if (result == null) {
            // If we did not find a single move during search (almost impossible), just return a random legal move.
            System.out.println("Time expired before a move was found!");
            Move move = moveGenerator.generateMoves(board).get(0);
            result = new SearchResult(0, move, currentDepth);
        }
        moveOrderer.clear();
        return result;

    }

    /**
     * Run a single iteration of the iterative deepening search for a specific depth. Since this function is called
     * recursively until the depth limit is reached, 'depth' needs to be split into two parameters: 'ply remaining' and
     * 'ply from root'.
     *
     * @param depth               The number of ply deeper left to go in the current search
     * @param ply                 The number of ply already examined in this iteration of the search.
     * @param alpha               The lower bound for child nodes at the current search depth.
     * @param beta                The upper bound for child nodes at the current search depth.
     * @param allowNull           Whether to allow null-move pruning in this search iteration.
     */
    public int search(int depth, int ply, int alpha, int beta, boolean allowNull) {

        if (isCancelled()) {
            return alpha;
        }
        if (depth <= 0) {
            return quiescenceSearch(alpha, beta, 1, ply);
        }
        if (ply > 0 && isDraw()) {
            return DRAW_SCORE;
        }

        // Mate distance pruning: exit early if we have already found a forced mate at an earlier ply
        alpha = Math.max(alpha, -MATE_SCORE + ply);
        beta = Math.min(beta, MATE_SCORE - ply);
        if (alpha >= beta) {
            return alpha;
        }

        boolean pvNode = beta - alpha > 1;
        Move previousBestMove = ply == 0 && result != null ? result.move() : null;

        HashEntry transposition = transpositionTable.get(getKey(), ply);
        if (isUsefulTransposition(transposition, depth, alpha, beta)) {
            if (ply == 0 && transposition.getMove() != null) {
                resultCurrentDepth = new SearchResult(transposition.getScore(), transposition.getMove(), depth);
            }
            return transposition.getScore();
        }
        if (hasBestMove(transposition)) {
            previousBestMove = transposition.getMove();
        }

        List<Move> moves = moveGenerator.generateMoves(board);
        boolean isInCheck = moveGenerator.isCheck(board, board.isWhiteToMove());
        int staticEval = evaluator.get();

        if (moves.isEmpty()) {
            // Found checkmate / stalemate
            return isInCheck ? -MATE_SCORE + ply : DRAW_SCORE;
        }
        if (ply == 0 && moves.size() == 1) {
            // Exit immediately if there is only one legal move at the root node
            resultCurrentDepth = new SearchResult(staticEval, moves.get(0), depth);
            cancelled = true;
            return staticEval;
        }

        if (!pvNode && !isInCheck) {

            // Reverse futility pruning: if the static evaluation - some margin is still > beta, then let's assume
            // there's no point searching any moves, since it's likely to produce a cut-off no matter what we do.
            boolean isMateHunting = Math.abs(alpha) >= MATE_SCORE - 100;
            if (depth <= RFP_DEPTH && staticEval - RFP_MARGIN[depth] > beta && !isMateHunting) {
                return staticEval - RFP_MARGIN[depth];
            }

            // Null move pruning: if the static eval > beta - some margin, then let's test this theory by giving the
            // opponent an extra move (making a 'null' move). If the result still fails high, prune this node.
            boolean isPawnEndgame = !evaluator.getMaterial(board.isWhiteToMove()).hasPiecesRemaining();
            if (allowNull && staticEval >= beta - NMP_MARGIN && !isPawnEndgame) {
                board.makeNullMove();
                int eval = -search(depth - 1 - (2 + depth / 7), ply + 1, -beta, -beta + 1, false);
                board.unmakeNullMove();
                if (eval >= beta) {
                    transpositionTable.put(getKey(), HashFlag.LOWER, depth, ply, previousBestMove, beta);
                    return eval;
                }
            }

        }

        moves = moveOrderer.orderMoves(board, moves, previousBestMove, true, ply);
        Move bestMove = null;
        HashFlag flag = HashFlag.UPPER;

        for (int i = 0; i < moves.size(); i++) {

            Move move = moves.get(i);
            boolean isCapture = board.pieceAt(move.getEndSquare()) != null;
            boolean isPromotion = move.getPromotionPieceType() != null;

            makeMove(move);

            boolean isCheck = moveGenerator.isCheck(board, board.isWhiteToMove());

            // Futility pruning: if the static eval + some margin is still < alpha, and the current move is not interesting
            // (checks, captures, promotions), then let's assume it will fail low and prune this node.
            if (!pvNode && depth <= FP_DEPTH && staticEval + FP_MARGIN[depth] < alpha && !isInCheck && !isCheck && !isCapture && !isPromotion) {
                unmakeMove();
                continue;
            }

            // Search extensions: in certain interesting cases (promotions, checks that do not immediately lose material),
            // let's extend the search depth by one ply.
            int extension = 0;
            if (isPromotion || (isCheck && see.evaluateAfterMove(board, move) >= 0)) {
                extension = 1;
            }

            int eval;
            if (pvNode && i == 0) {
                // Principal variation search: the first move must be searched with the full alpha-beta window. If our move
                // ordering is any good then we expect this to be the best move, and so we need to retrieve the exact score.
                eval = -search(depth - 1 + extension, ply + 1, -beta, -alpha, true);

            } else {
                // Late move reductions: if the move is ordered late in the list, and isn't a 'noisy' move like a check,
                // capture or promotion, let's save time by assuming it's less likely to be good, and reduce the search depth.
                int reduction = 0;
                if (depth >= LMR_DEPTH && i >= 2 && !isCapture && !isCheck && !isPromotion) {
                    reduction = i < 5 ? 1 : depth / 3;
                }

                // For all other moves apart from the principal variation, search with a null window (-alpha - 1, -alpha),
                // to try and prove the move will fail low while saving the time spent on a full search.
                eval = -search(depth - 1 + extension - reduction, ply + 1, -alpha - 1, -alpha, true);

                if (eval > alpha && (eval < beta || reduction > 0)) {
                    // If we reduced the depth and/or used a null window, and the score beat alpha, we need to do a
                    // re-search with the full window and depth. This is costly, but hopefully doesn't happen too often.
                    eval = -search(depth - 1 + extension, ply + 1, -beta, -alpha, true);
                }
            }

            unmakeMove();

            if (isCancelled()) {
                return alpha;
            }

            if (eval >= beta) {
                // This is a beta cut-off - the opponent won't let us get here as they already have better alternatives
                transpositionTable.put(getKey(), HashFlag.LOWER, depth, ply, move, beta);
                if (!isCapture) {
                    // Non-captures which cause a beta cut-off are stored as 'killer' and 'history' moves for future move ordering
                    moveOrderer.addKillerMove(ply, move);
                    moveOrderer.addHistoryMove(depth, move, board.isWhiteToMove());
                }
                return beta;
            }

            if (eval > alpha) {
                // We have found a new best move
                bestMove = move;
                alpha = eval;
                flag = HashFlag.EXACT;
                if (ply == 0) {
                    resultCurrentDepth = new SearchResult(eval, move, depth);
                }
            }
        }
        transpositionTable.put(getKey(), flag, depth, ply, bestMove, alpha);
        return alpha;

    }

    /**
     * Extend the search by searching captures until a 'quiet' position is reached, where there are no further captures
     * and therefore limited potential for winning tactics that drastically alter the evaluation. Used to mitigate the
     * worst of the 'horizon effect'.
     *
     * @see <a href="https://www.chessprogramming.org/Quiescence_Search">Chess Programming Wiki</a>
     */
    int quiescenceSearch(int alpha, int beta, int depth, int plyFromRoot) {
        if (isCancelled()) {
            return alpha;
        }
        // Exit the quiescence search early if we already have an accurate score stored in the hash table.
        Move previousBestMove = null;
        HashEntry transposition = transpositionTable.get(getKey(), plyFromRoot);
        if (isUsefulTransposition(transposition, 1, alpha, beta)) {
            return transposition.getScore();
        }
        if (hasBestMove(transposition)) {
            previousBestMove = transposition.getMove();
        }

        int eval = evaluator.get();
        int standPat = eval;

        boolean isInCheck = moveGenerator.isCheck(board, board.isWhiteToMove());

        List<Move> moves;
        if (isInCheck) {
            // If we are in check, we need to generate 'all' legal moves that evade check, not just captures. Otherwise,
            // we risk missing simple mate threats.
            moves = moveGenerator.generateMoves(board, MoveFilter.ALL);
            if (moves.isEmpty()) {
                return -MATE_SCORE + plyFromRoot;
            }
        } else {
            // If we are not in check, then we have the option to 'stand pat', i.e. decline to continue the capture chain,
            // if the static evaluation of the position is good enough.
            if (eval >= beta) {
                return beta;
            }
            if (eval > alpha) {
                alpha = eval;
            }
            MoveFilter filter = depth == 1 ? MoveFilter.CAPTURES_AND_CHECKS : MoveFilter.CAPTURES_ONLY;
            moves = moveGenerator.generateMoves(board, filter);
        }

        List<Move> orderedMoves = moveOrderer.orderMoves(board, moves, previousBestMove, false, 0);

        for (Move move : orderedMoves) {
            if (!isInCheck) {
                // Futility pruning: if the captured piece + a margin still has no potential of raising alpha, prune this node.
                Piece capturedPiece = move.isEnPassant() ? Piece.PAWN : board.pieceAt(move.getEndSquare());
                boolean isFutile = capturedPiece != null && (standPat + PieceValues.valueOf(capturedPiece) + DP_MARGIN < alpha) && !move.isPromotion();
                if (isFutile) {
                    continue;
                }
                // Static exchange evaluation: filter out likely bad captures (e.g. QxP -> PxQ)
                int seeScore = see.evaluate(board, move);
                boolean isBadCapture = (depth <= 3 && seeScore < 0) || (depth > 3 && seeScore <= 0);
                if (isBadCapture) {
                    continue;
                }
            }

            makeMove(move);
            eval = -quiescenceSearch(-beta, -alpha, depth + 1, plyFromRoot + 1);
            unmakeMove();

            if (eval >= beta) {
                return beta;
            }
            if (eval > alpha) {
                alpha = eval;
            }
        }

        return alpha;

    }

    private void makeMove(Move move) {
        board.makeMove(move);
        evaluator.makeMove(move);
    }

    private void unmakeMove() {
        board.unmakeMove();
        evaluator.unmakeMove();
    }

    /**
     * Check if the hit from the transposition table is 'useful' in the current search. A TT-hit is useful either if it
     * 1) contains an exact evaluation, so we don't need to search any further, 2) contains a fail-high greater than our
     * current beta value, or 3) contains a fail-low lesser than our current alpha value.
     */
    private boolean isUsefulTransposition(HashEntry entry, int plyRemaining, int alpha, int beta) {
        return entry != null &&
                entry.getDepth() >= plyRemaining &&
                ((entry.getFlag().equals(HashFlag.EXACT)) ||
                 (entry.getFlag().equals(HashFlag.UPPER) && entry.getScore() <= alpha) ||
                 (entry.getFlag().equals(HashFlag.LOWER) && entry.getScore() >= beta));
    }

    private boolean hasBestMove(HashEntry transposition) {
        return transposition != null && transposition.getMove() != null;
    }

    private boolean isCheckmateFoundAtCurrentDepth(int currentDepth) {
        return Math.abs(result.eval()) >= MATE_SCORE - currentDepth;
    }

    private boolean isCancelled() {
        return cancelled || (timeout != null && !Instant.now().isBefore(timeout));
    }

    private boolean isDraw() {
        return resultCalculator.isEffectiveDraw(board);
    }

    private long getKey() {
        return board.getGameState().getZobristKey();
    }

    @Override
    public void clearHistory() {
        if (transpositionTable != null) {
            transpositionTable.clear();
        }
    }

    @Override
    public void logStatistics() {
    }

}
