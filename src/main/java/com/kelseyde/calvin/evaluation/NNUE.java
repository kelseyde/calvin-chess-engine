package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bits.Castling;
import com.kelseyde.calvin.board.Bits.File;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.evaluation.Accumulator.FeatureUpdate;
import com.kelseyde.calvin.evaluation.activation.Activation;
import com.kelseyde.calvin.search.Search;

/**
 * Calvin's evaluation function is an Efficiently Updatable Neural Network (NNUE).
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
public class NNUE {

    public static final Network NETWORK = Network.builder()
            .file("argonaut.nnue")
            .inputSize(768)
            .hiddenSize(768)
            .activation(Activation.SCReLU)
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
        activateAll(board);
    }

    public int evaluate() {

        applyLazyUpdates();
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

    private void activateAll(Board board) {

        final Accumulator acc = accumulatorStack[current];
        for (int i = 0; i < NETWORK.hiddenSize(); i++) {
            acc.whiteFeatures[i] = NETWORK.inputBiases()[i];
            acc.blackFeatures[i] = NETWORK.inputBiases()[i];
        }

        activateSide(acc, board, board.getWhitePieces(), true);
        activateSide(acc, board, board.getBlackPieces(), false);

    }

    private void activateSide(Accumulator acc, Board board, long pieces, boolean white) {
        while (pieces != 0) {
            final int square = Bits.next(pieces);
            final Piece piece = board.pieceAt(square);
            final int whiteIndex = featureIndex(piece, square, white, true);
            final int blackIndex = featureIndex(piece, square, white, false);
            acc.add(whiteIndex, blackIndex);
            pieces = Bits.pop(pieces);
        }
    }

    /**
     * Efficiently update only the relevant features of the network after a move has been made.
     */
    public void makeMove(Board board, Move move) {

        final Accumulator acc = accumulatorStack[++current] = accumulatorStack[current - 1].copy();
        final boolean white = board.isWhite();
        final int from = move.from();
        final int to = move.to();
        final Piece piece = board.pieceAt(from);
        if (piece == null) return;
        final Piece newPiece = move.isPromotion() ? move.promoPiece() : piece;
        final Piece captured = move.isEnPassant() ? Piece.PAWN : board.pieceAt(to);

        if (move.isCastling()) {
            handleCastleMove(acc, move, white);
        } else if (captured != null) {
            handleCapture(acc, move, piece, newPiece, captured, white);
        } else {
            handleStandardMove(acc, move, piece, newPiece, white);
        }
        acc.correct = false;

    }

    private void handleStandardMove(Accumulator acc, Move move, Piece piece, Piece newPiece, boolean white) {
        final FeatureUpdate add = new FeatureUpdate(move.to(), newPiece, white);
        final FeatureUpdate sub = new FeatureUpdate(move.from(), piece, white);
        acc.update.pushAddSub(add, sub);
    }

    private void handleCastleMove(Accumulator acc, Move move, boolean white) {
        final boolean kingside = File.of(move.to()) == 6;
        final int rookFrom = Castling.rookFrom(kingside, white);
        final int rookTo = Castling.rookTo(kingside, white);
        final FeatureUpdate kingAdd = new FeatureUpdate(move.to(), Piece.KING, white);
        final FeatureUpdate kingSub = new FeatureUpdate(move.from(), Piece.KING, white);
        final FeatureUpdate rookAdd = new FeatureUpdate(rookTo, Piece.ROOK, white);
        final FeatureUpdate rookSub = new FeatureUpdate(rookFrom, Piece.ROOK, white);
        acc.update.pushAddAddSubSub(kingAdd, rookAdd, kingSub, rookSub);
    }

    private void handleCapture(Accumulator acc, Move move, Piece piece, Piece newPiece, Piece captured, boolean white) {
        int captureSquare = move.to();
        if (move.isEnPassant()) captureSquare = white ? move.to() - 8 : move.to() + 8;
        final FeatureUpdate add = new FeatureUpdate(move.to(), newPiece, white);
        final FeatureUpdate sub1 = new FeatureUpdate(captureSquare, captured, !white);
        final FeatureUpdate sub2 = new FeatureUpdate(move.from(), piece, white);
        acc.update.pushAddSubSub(add, sub1, sub2);
    }

    public void unmakeMove() {
        current--;
    }

    public void setPosition(Board board) {
        clearHistory();
        this.board = board;
        activateAll(board);
    }

    private void applyLazyUpdates() {

        // Scan back to the last non-dirty accumulator.
        if (accumulatorStack[current].correct) return;
        int i = current - 1;
        while (i >= 0 && !accumulatorStack[i].correct) i--;

        while (i < current) {
            if (i + 1 >= accumulatorStack.length) break;
            Accumulator prev = accumulatorStack[i];
            Accumulator curr = accumulatorStack[i + 1];
            Accumulator.AccumulatorUpdate update = curr.update;
            if (update.addCount == 1 && update.subCount == 1) {
                lazyUpdateAddSub(prev, curr);
            } else if (update.addCount == 1 && update.subCount == 2) {
                lazyUpdateAddSubSub(prev, curr);
            } else if (update.addCount == 2 && update.subCount == 2) {
                lazyUpdateAddAddSubSub(prev, curr);
            }
            curr.correct = true;
            i++;
        }

    }

    private void lazyUpdateAddSub(Accumulator prev, Accumulator curr) {
        final Accumulator.AccumulatorUpdate update = curr.update;
        final Accumulator.FeatureUpdate add = update.adds[0];
        final Accumulator.FeatureUpdate sub = update.subs[0];
        final int whiteAddIdx = featureIndex(add.piece(), add.square(), add.white(), true);
        final int blackAddIdx = featureIndex(add.piece(), add.square(), add.white(), false);
        final int whiteSubIdx = featureIndex(sub.piece(), sub.square(), sub.white(), true);
        final int blackSubIdx = featureIndex(sub.piece(), sub.square(), sub.white(), false);
        curr.addSub(prev.whiteFeatures, prev.blackFeatures,
                whiteAddIdx, blackAddIdx, whiteSubIdx, blackSubIdx);
    }

    private void lazyUpdateAddSubSub(Accumulator prev, Accumulator curr) {
        final Accumulator.AccumulatorUpdate update = curr.update;
        final Accumulator.FeatureUpdate add1 = update.adds[0];
        final Accumulator.FeatureUpdate sub1 = update.subs[0];
        final Accumulator.FeatureUpdate sub2 = update.subs[1];
        final int whiteAdd1Idx = featureIndex(add1.piece(), add1.square(), add1.white(), true);
        final int blackAdd1Idx = featureIndex(add1.piece(), add1.square(), add1.white(), false);
        final int whiteSub1Idx = featureIndex(sub1.piece(), sub1.square(), sub1.white(), true);
        final int blackSub1Idx = featureIndex(sub1.piece(), sub1.square(), sub1.white(), false);
        final int whiteSub2Idx = featureIndex(sub2.piece(), sub2.square(), sub2.white(), true);
        final int blackSub2Idx = featureIndex(sub2.piece(), sub2.square(), sub2.white(), false);
        curr.addSubSub(prev.whiteFeatures, prev.blackFeatures,
                whiteAdd1Idx, blackAdd1Idx, whiteSub1Idx, blackSub1Idx, whiteSub2Idx, blackSub2Idx);
    }

    private void lazyUpdateAddAddSubSub(Accumulator prev, Accumulator curr) {
        Accumulator.AccumulatorUpdate update = curr.update;
        Accumulator.FeatureUpdate add1 = update.adds[0];
        Accumulator.FeatureUpdate add2 = update.adds[1];
        Accumulator.FeatureUpdate sub1 = update.subs[0];
        Accumulator.FeatureUpdate sub2 = update.subs[1];
        int whiteAdd1Idx = featureIndex(add1.piece(), add1.square(), add1.white(), true);
        int blackAdd1Idx = featureIndex(add1.piece(), add1.square(), add1.white(), false);
        int whiteAdd2Idx = featureIndex(add2.piece(), add2.square(), add2.white(), true);
        int blackAdd2Idx = featureIndex(add2.piece(), add2.square(), add2.white(), false);
        int whiteSub1Idx = featureIndex(sub1.piece(), sub1.square(), sub1.white(), true);
        int blackSub1Idx = featureIndex(sub1.piece(), sub1.square(), sub1.white(), false);
        int whiteSub2Idx = featureIndex(sub2.piece(), sub2.square(), sub2.white(), true);
        int blackSub2Idx = featureIndex(sub2.piece(), sub2.square(), sub2.white(), false);
        curr.addAddSubSub(prev.whiteFeatures, prev.blackFeatures,
                whiteAdd1Idx, blackAdd1Idx, whiteAdd2Idx, blackAdd2Idx, whiteSub1Idx, blackSub1Idx, whiteSub2Idx, blackSub2Idx);
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
        final long knights = Bits.count(board.getKnights());
        final long bishops = Bits.count(board.getBishops());
        final long rooks = Bits.count(board.getRooks());
        final long queens = Bits.count(board.getQueens());
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

    public void clearHistory() {
        this.current = 0;
        this.accumulatorStack = new Accumulator[Search.MAX_DEPTH];
        this.accumulatorStack[0] = new Accumulator(NETWORK.hiddenSize());
    }

}
