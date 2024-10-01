package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.File;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.evaluation.activation.Activation;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Implementation of {@link Evaluation} using an NNUE (Efficiently Updatable Neural Network) evaluation function.
 * <p>
 * The network has an input layer of 768 neurons, each representing the presence of a piece of each colour on a square
 * (64 squares * 6 pieces * 2 colours). Two versions of the hidden layer are accumulated: one from white's perspective
 * and one from black's. It is 'efficiently updatable' due to the fact that, on each move, only the features of the
 * relevant pieces need to be re-calculated, not the features of the entire board; this is a significant speed boost.
 * <p>
 * The network was trained on positions taken from a dataset of Leela Chess Zero, which were then re-scored with
 * Calvin's own search and hand-crafted evaluation.
 *
 * @see <a href="https://www.chessprogramming.org/NNUE">Chess Programming Wiki</a>
 */
public class NNUE implements Evaluation {

    public static final Network NETWORK = Network.Builder.builder()
            .file("sol.nnue")
            .inputSize(768)
            .hiddenSize(384)
            .activation(Activation.SCRELU)
            .quantisations(new int[]{255, 64})
            .scale(400)
            .build();

    private final Deque<Accumulator> accumulatorHistory = new ArrayDeque<>();
    private Accumulator accumulator;
    private Board board;

    public NNUE() {
        this.accumulator = new Accumulator(NETWORK.hiddenSize());
    }

    public NNUE(Board board) {
        this.accumulator = new Accumulator(NETWORK.hiddenSize());
        this.board = board;
        activateAll(board);
    }

    @Override
    public int evaluate() {

        final boolean white = board.isWhite();

        // Get the 'us-perspective' and 'them-perspective' feature sets, based on the side to move.
        final short[] us = white ? accumulator.whiteFeatures : accumulator.blackFeatures;
        final short[] them = white ? accumulator.blackFeatures : accumulator.whiteFeatures;

        // Pass the features through the network to get the evaluation.
        int eval = NETWORK.activation().forward(us, them);

        // Scale the evaluation based on the material and proximity to 50-move rule draw.
        eval = scaleEvaluation(board, eval);

        return eval;

    }

    private void activateAll(Board board) {

        for (int i = 0; i < NETWORK.hiddenSize(); i++) {
            accumulator.whiteFeatures[i] = NETWORK.inputBiases()[i];
            accumulator.blackFeatures[i] = NETWORK.inputBiases()[i];
        }

        activateSide(board, board.getWhitePieces(), true);
        activateSide(board, board.getBlackPieces(), false);

    }

    private void activateSide(Board board, long pieces, boolean white) {
        while (pieces != 0) {
            int square = Bits.next(pieces);
            Piece piece = board.pieceAt(square);
            int whiteIndex = featureIndex(piece, square, white, true);
            int blackIndex = featureIndex(piece, square, white, false);
            accumulator.add(whiteIndex, blackIndex);
            pieces = Bits.pop(pieces);
        }
    }

    /**
     * Efficiently update only the relevant features of the network after a move has been made.
     */
    @Override
    public void makeMove(Board board, Move move) {
        accumulatorHistory.push(accumulator.copy());
        boolean white = board.isWhite();
        int from = move.from();
        int to = move.to();
        Piece piece = board.pieceAt(from);
        if (piece == null) return;
        Piece newPiece = move.isPromotion() ? move.promoPiece() : piece;
        Piece captured = move.isEnPassant() ? Piece.PAWN : board.pieceAt(to);

        int oldWhiteIdx = featureIndex(piece, from, white, true);
        int oldBlackIdx = featureIndex(piece, from, white, false);

        int newWhiteIdx = featureIndex(newPiece, to, white, true);
        int newBlackIdx = featureIndex(newPiece, to, white, false);

        if (move.isCastling()) {
            handleCastleMove(white, to, oldWhiteIdx, oldBlackIdx, newWhiteIdx, newBlackIdx);
        } else if (captured != null) {
            handleCapture(move, captured, white, newWhiteIdx, newBlackIdx, oldWhiteIdx, oldBlackIdx);
        } else {
            accumulator.addSub(newWhiteIdx, newBlackIdx, oldWhiteIdx, oldBlackIdx);
        }
    }

    private void handleCastleMove(boolean white, int to, int oldWhiteIdx, int oldBlackIdx, int newWhiteIdx, int newBlackIdx) {
        final boolean isKingside = File.of(to) == 6;
        final int rookStart = isKingside ? white ? 7 : 63 : white ? 0 : 56;
        final int rookEnd = isKingside ? white ? 5 : 61 : white ? 3 : 59;
        final int rookStartWhiteIdx = featureIndex(Piece.ROOK, rookStart, white, true);
        final int rookStartBlackIdx = featureIndex(Piece.ROOK, rookStart, white, false);
        final int rookEndWhiteIdx = featureIndex(Piece.ROOK, rookEnd, white, true);
        final int rookEndBlackIdx = featureIndex(Piece.ROOK, rookEnd, white, false);
        accumulator.addAddSubSub(newWhiteIdx, newBlackIdx, rookEndWhiteIdx, rookEndBlackIdx, oldWhiteIdx, oldBlackIdx, rookStartWhiteIdx, rookStartBlackIdx);
    }

    private void handleCapture(Move move, Piece captured, boolean white, int newWhiteIdx, int newBlackIdx, int oldWhiteIdx, int oldBlackIdx) {
        int captureSquare = move.to();
        if (move.isEnPassant()) captureSquare = white ? move.to() - 8 : move.to() + 8;
        int capturedWhiteIdx = featureIndex(captured, captureSquare, !white, true);
        int capturedBlackIdx = featureIndex(captured, captureSquare, !white, false);
        accumulator.addSubSub(newWhiteIdx, newBlackIdx, oldWhiteIdx, oldBlackIdx, capturedWhiteIdx, capturedBlackIdx);
    }

    @Override
    public void unmakeMove() {
        this.accumulator = accumulatorHistory.pop();
    }

    @Override
    public void setPosition(Board board) {
        this.board = board;
        activateAll(board);
    }

    private int scaleEvaluation(Board board, int eval) {

        // Scale down the evaluation when there's not much material left on the board - this creates an incentive
        // to keep pieces on the board when we have winning chances, and trade them off when we're under pressure.
        int materialPhase = materialPhase(board);
        eval = eval * (22400 + materialPhase) / 32768;

        // Scale down the evaluation as we approach the 50-move rule draw - this gives the engine an understanding
        // of when no progress is being made in the position.
        eval = eval * (200 - board.getState().getHalfMoveClock()) / 200;

        return eval;
    }

    private int materialPhase(Board board) {
        long knights = Bits.count(board.getKnights());
        long bishops = Bits.count(board.getBishops());
        long rooks = Bits.count(board.getRooks());
        long queens = Bits.count(board.getQueens());
        return (int) (3 * knights + 3 * bishops + 5 * rooks + 10 * queens);
    }

    /**
     * Compute the index of the feature vector for a given piece, colour and square. Features from black's perspective
     * are mirrored (the square index is vertically flipped) in order to preserve symmetry.
     */
    private static int featureIndex(Piece piece, int square, boolean whitePiece, boolean whitePerspective) {
        final int squareIndex = whitePerspective ? square : square ^ 56;
        final int pieceIndex = piece.index();
        final int pieceOffset = pieceIndex * Square.COUNT;
        final boolean ourPiece = whitePiece == whitePerspective;
        final int colourOffset = ourPiece ? 0 : (Square.COUNT * Piece.COUNT);
        return colourOffset + pieceOffset + squareIndex;
    }

    @Override
    public void clearHistory() {
        this.accumulator = new Accumulator(NETWORK.hiddenSize());
        this.accumulatorHistory.clear();
    }

}
