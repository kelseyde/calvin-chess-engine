package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.*;
import com.kelseyde.calvin.board.Bits.File;
import com.kelseyde.calvin.evaluation.Accumulator.AccumulatorUpdate;
import com.kelseyde.calvin.evaluation.activation.Activation;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.uci.UCI;

/**
 * Calvin's evaluation function is an Efficiently Updatable Neural Network (NNUE).
 * <p>
 * The network has an input layer of 768 neurons, each representing the presence of a piece of each colour on a square
 * (64 squares * 6 pieces * 2 colours). Two versions of the hidden layer are accumulated: one from white's perspective
 * and one from black's. It is 'efficiently updatable' due to the fact that, on each move, only the features of the
 * relevant pieces need to be re-calculated, not the features of the entire board; this is a significant speed boost.
 * <p>
 * The network was trained on positions taken from a dataset of Leela Chess Zero, which were then re-scored with
 * Calvin's own search and evaluation.
 *
 * @see <a href="https://www.chessprogramming.org/NNUE">Chess Programming Wiki</a>
 */
public class NNUE {

    public static final Network NETWORK = Network.builder()
            .file("calvin1024_2.nnue")
            .inputSize(768)
            .hiddenSize(1024)
            .activation(Activation.SCReLU)
            .horizontalMirror(true)
            .quantisations(new int[]{255, 64})
            .scale(400)
            .build();

    private Accumulator[] accumulatorStack;
    private int current;
    private Board board;

    public NNUE() {
        this.current = 0;
        this.accumulatorStack = new Accumulator[Search.MAX_DEPTH];
        this.accumulatorStack[current] = new Accumulator(NETWORK.hiddenSize());
    }

    public NNUE(Board board) {
        this.board = board;
        this.current = 0;
        this.accumulatorStack = new Accumulator[Search.MAX_DEPTH];
        this.accumulatorStack[current] = new Accumulator(NETWORK.hiddenSize());
        fullRefresh(board);
    }

    public int evaluate() {

        final boolean white = board.isWhite();
        final Accumulator acc = accumulatorStack[current];

        // Get the 'us-perspective' and 'them-perspective' feature sets, based on the side to move.
        final short[] us = white ? acc.whiteFeatures : acc.blackFeatures;
        final short[] them = white ? acc.blackFeatures : acc.whiteFeatures;

        // Pass the features through the network to get the evaluation.
        int eval = NETWORK.activation().forward(us, them);

        // Scale the evaluation based on the material and proximity to 50-move rule draw.
        eval = scaleEvaluation(board, eval);

        return eval;

    }

    private void fullRefresh(Board board) {

        // Fully refresh the accumulator from both perspectives with the features of all pieces on the board.
        final Accumulator acc = accumulatorStack[current];
        final boolean whiteMirror = shouldMirror(board.kingSquare(true));
        final boolean blackMirror = shouldMirror(board.kingSquare(false));
        fullRefresh(board, acc, true, whiteMirror);
        fullRefresh(board, acc, false, blackMirror);

    }

    private void fullRefresh(Board board, Accumulator acc, boolean whitePerspective, boolean mirror) {

        // Fully refresh the accumulator for one perspective with the features of all pieces on the board.
        acc.mirrored[Colour.index(whitePerspective)] = mirror;
        // Reset every feature in the accumulator to the initial bias value.
        acc.reset(whitePerspective);

        long pieces = board.getOccupied();
        while (pieces != 0) {
            // For each piece on the board, activate the corresponding feature in the accumulator.
            int square = Bits.next(pieces);
            final Piece piece = board.pieceAt(square);
            final boolean whitePiece = Bits.contains(board.getWhitePieces(), square);
            final Feature feature = new Feature(piece, square, whitePiece);
            acc.add(feature, whitePerspective);
            pieces = Bits.pop(pieces);
        }

    }


    public void makeMove(Board board, Move move) {

        // Efficiently update only the relevant features of the network after a move has been made.
        final Accumulator acc = accumulatorStack[current];
        final boolean white = board.isWhite();

        // Determine which features need to be updated based on the move type (standard, capture, or castle).
        final AccumulatorUpdate update = switch (moveType(board, move)) {
            case STANDARD -> handleStandardMove(board, move, white);
            case CASTLE -> handleCastleMove(move, white);
            case CAPTURE -> handleCapture(board, move, white);
        };

        if (mustRefresh(board, move)) {
            // If the network is horizontally mirrored, and the king has just crossed the central axis,
            // then a full accumulator refresh is required for the side-to-move before applying the move.
            final Accumulator newAcc = acc.copy();
            final boolean mirror = !shouldMirror(board.kingSquare(white));
            fullRefresh(board, newAcc, white, mirror);
            accumulatorStack[++current] = newAcc.apply(update);

        } else {
            // Apply the update to the accumulator.
            accumulatorStack[++current] = acc.apply(update);

        }

    }

    private AccumulatorUpdate handleStandardMove(Board board, Move move, boolean white) {

        // For standard moves we simply need to remove the piece from the 'from' square and add it to the 'to' square.
        final Piece piece = board.pieceAt(move.from());
        final Piece newPiece = move.isPromotion() ? move.promoPiece() : piece;

        AccumulatorUpdate update = new AccumulatorUpdate();
        update.pushAdd(new Feature(newPiece, move.to(), white));
        update.pushSub(new Feature(piece, move.from(), white));
        return update;

    }

    private AccumulatorUpdate handleCastleMove(Move move, boolean white) {

        // For castling moves we need to move both the king and the rook, with some special handling for Chess960.
        AccumulatorUpdate update = new AccumulatorUpdate();
        final boolean kingside = Castling.isKingside(move.from(), move.to());

        // In Chess960, castling is encoded as 'king captures rook'.
        final int kingFrom = move.from();
        final int kingTo = UCI.Options.chess960 ? Castling.kingTo(kingside, white) : move.to();
        final int rookFrom = UCI.Options.chess960 ? move.to() : Castling.rookFrom(kingside, white);
        final int rookTo = Castling.rookTo(kingside, white);

        update.pushSub(new Feature(Piece.KING, kingFrom, white));
        update.pushSub(new Feature(Piece.ROOK, rookFrom, white));
        update.pushAdd(new Feature(Piece.KING, kingTo, white));
        update.pushAdd(new Feature(Piece.ROOK, rookTo, white));

        return update;

    }

    private AccumulatorUpdate handleCapture(Board board, Move move, boolean white) {

        // For captures, we need to remove the captured piece as well as updating the capturing piece.
        final Piece piece = board.pieceAt(move.from());
        final Piece newPiece = move.isPromotion() ? move.promoPiece() : piece;
        final Piece captured = move.isEnPassant() ? Piece.PAWN : board.pieceAt(move.to());

        AccumulatorUpdate update = new AccumulatorUpdate();
        int captureSquare = move.to();
        if (move.isEnPassant()) {
            captureSquare = white ? move.to() - 8 : move.to() + 8;
        }
        update.pushSub(new Feature(piece, move.from(), white));
        update.pushAdd(new Feature(newPiece, move.to(), white));
        update.pushSub(new Feature(captured, captureSquare, !white));
        return update;

    }

    public void unmakeMove() {
        current--;
    }

    public void setPosition(Board board) {
        clearHistory();
        this.board = board;
        fullRefresh(board);
    }

    private int scaleEvaluation(Board board, int eval) {

        // Scale down the evaluation when there's not much material left on the board - this creates an incentive
        // to keep pieces on the board when we have winning chances, and trade them off when we're under pressure.
        final int materialPhase = materialPhase(board);
        eval = eval * (22400 + materialPhase) / 32768;

        // Scale down the evaluation as we approach the 50-move rule draw - this gives the engine an understanding
        // of when no progress is being made in the position.
        eval = eval * (200 - board.getState().getHalfMoveClock()) / 200;

        return eval;

    }

    private int materialPhase(Board board) {
        final int knights = Bits.count(board.getKnights());
        final int bishops = Bits.count(board.getBishops());
        final int rooks = Bits.count(board.getRooks());
        final int queens = Bits.count(board.getQueens());
        return 3 * knights + 3 * bishops + 5 * rooks + 10 * queens;
    }

    private boolean mustRefresh(Board board, Move move) {
        if (!NETWORK.horizontalMirror()) {
            return false;
        }
        if (board.pieceAt(move.from()) != Piece.KING) {
            return false;
        }
        int prevKingSquare = move.from();
        int currKingSquare = move.to();
        if (move.isCastling() && UCI.Options.chess960) {
            final boolean kingside = Castling.isKingside(move.from(), move.to());
            currKingSquare = Castling.kingTo(kingside, board.isWhite());
        }
        return shouldMirror(prevKingSquare) != shouldMirror(currKingSquare);
    }

    private boolean shouldMirror(int kingSquare) {
        return NETWORK.horizontalMirror() && File.of(kingSquare) > 3;
    }

    private MoveType moveType(Board board, Move move) {
        if (move.isCastling()) {
            return MoveType.CASTLE;
        } else if (move.isEnPassant() || board.pieceAt(move.to()) != null) {
            return MoveType.CAPTURE;
        } else {
            return MoveType.STANDARD;
        }
    }

    public void clearHistory() {
        this.current = 0;
        this.accumulatorStack = new Accumulator[Search.MAX_DEPTH];
        this.accumulatorStack[0] = new Accumulator(NETWORK.hiddenSize());
    }

    private enum MoveType {
        STANDARD,
        CAPTURE,
        CASTLE
    }

}
