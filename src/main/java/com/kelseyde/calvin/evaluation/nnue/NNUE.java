package com.kelseyde.calvin.evaluation.nnue;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.notation.FEN;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.util.ArrayDeque;
import java.util.Deque;

public class NNUE implements Evaluation {

    private static final int COLOUR_STRIDE = 64 * 6;
    private static final int PIECE_STRIDE = 64;
    private static final int SCALE = 350;
    private static final int Q = 255 * 64;
    private static final VectorSpecies<Short> SPECIES = ShortVector.SPECIES_PREFERRED;

    private Accumulator accumulator;
    private Deque<Accumulator> accumulatorHistory = new ArrayDeque<>();
    public int score;

    public NNUE() {
        this.accumulator = new Accumulator(Network.HIDDEN_LAYER_SIZE);
    }

    public NNUE(Board board) {
        this.accumulator = new Accumulator(Network.HIDDEN_LAYER_SIZE);
        activateAll(board);
    }

    @Override
    public int evaluate(Board board) {
        boolean white = board.isWhiteToMove();
        int output = white ?
                forward(accumulator.whiteFeatures, accumulator.blackFeatures, Network.DEFAULT.outputWeights) :
                forward(accumulator.blackFeatures, accumulator.whiteFeatures, Network.DEFAULT.outputWeights);
        return (output + Network.DEFAULT.outputBias) * SCALE / Q;
    }

    @Override
    public void makeMove(Board board, Move move) {
        accumulatorHistory.push(accumulator.copy());
        boolean white = board.isWhiteToMove();
        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();
        Piece piece = board.pieceAt(startSquare);
        Piece newPiece = move.isPromotion() ? move.getPromotionPiece() : piece;
        deactivate(piece, startSquare, white);
        activate(newPiece, endSquare, white);

        Piece capturedPiece = move.isEnPassant() ? Piece.PAWN : board.pieceAt(endSquare);
        if (capturedPiece != null) {
            int captureSquare = move.getEndSquare();
            if (move.isEnPassant()) captureSquare = white ? endSquare - 8 : endSquare + 8;
            deactivate(capturedPiece, captureSquare, !white);
        }
        if (move.isCastling()) {
            boolean isKingside = BoardUtils.getFile(endSquare) == 6;
            if (isKingside) {
                deactivate(Piece.ROOK, white ? 7 : 63, white);
                activate(Piece.ROOK, white ? 5 : 61, white);
            } else {
                deactivate(Piece.ROOK, white ? 0 : 56, white);
                activate(Piece.ROOK, white ? 3 : 59, white);
            }
        }
    }

    @Override
    public void unmakeMove() {
        this.accumulator = accumulatorHistory.pop();
    }

    public void activateAll(Board board) {
        int length = Network.HIDDEN_LAYER_SIZE;
        int i = 0;

        // Process in chunks of vector length
        for (; i < SPECIES.loopBound(length); i += SPECIES.length()) {
            var vBiases = ShortVector.fromArray(SPECIES, Network.DEFAULT.inputBiases, i);
            vBiases.intoArray(accumulator.whiteFeatures, i);
            vBiases.intoArray(accumulator.blackFeatures, i);
        }

        // Process any remaining elements
        for (; i < length; i++) {
            accumulator.whiteFeatures[i] = Network.DEFAULT.inputBiases[i];
            accumulator.blackFeatures[i] = Network.DEFAULT.inputBiases[i];
        }

        activateSide(board, board.getWhitePieces(), true);
        activateSide(board, board.getBlackPieces(), false);
    }

    private void activateSide(Board board, long pieces, boolean white) {
        while (pieces != 0) {
            int square = Bitwise.getNextBit(pieces);
            Piece piece = board.pieceAt(square);
            activate(piece, square, white);
            pieces = Bitwise.popBit(pieces);
        }
    }

    private static int featureIndex(Piece piece, int square, boolean white, boolean whiteIndex) {
        int pieceIndex = piece.getIndex();
        int colourIndex = colourIndex(white);
        if (whiteIndex) {
            return (colourIndex ^ 1) * COLOUR_STRIDE + pieceIndex * PIECE_STRIDE + square;
        } else {
            return colourIndex * COLOUR_STRIDE + pieceIndex * PIECE_STRIDE + square ^ 0x38;
        }
    }

    private void activate(Piece piece, int square, boolean white) {
        int whiteIndex = featureIndex(piece, square, white, true);
        int blackIndex = featureIndex(piece, square, white, false);
        addWeights(accumulator.whiteFeatures, Network.DEFAULT.inputWeights, whiteIndex * Network.HIDDEN_LAYER_SIZE);
        addWeights(accumulator.blackFeatures, Network.DEFAULT.inputWeights, blackIndex * Network.HIDDEN_LAYER_SIZE);
    }

    private void deactivate(Piece piece, int square, boolean white) {
        int whiteIndex = featureIndex(piece, square, white, true);
        int blackIndex = featureIndex(piece, square, white, false);
        subtractWeights(accumulator.whiteFeatures, Network.DEFAULT.inputWeights, whiteIndex * Network.HIDDEN_LAYER_SIZE);
        subtractWeights(accumulator.blackFeatures, Network.DEFAULT.inputWeights, blackIndex * Network.HIDDEN_LAYER_SIZE);
    }

    public void addWeights(short[] features, short[] weights, int offset) {
        int length = features.length;
        int i = 0;

        for (; i < SPECIES.loopBound(length); i += SPECIES.length()) {
            var vFeatures = ShortVector.fromArray(SPECIES, features, i);
            var vWeights = ShortVector.fromArray(SPECIES, weights, i + offset);
            var vResult = vFeatures.add(vWeights);
            vResult.intoArray(features, i);
        }

        for (; i < length; i++) {
            features[i] -= weights[i + offset];
        }
    }

    public void subtractWeights(short[] features, short[] weights, int offset) {
        int length = features.length;
        int i = 0;

        for (; i < SPECIES.loopBound(length); i += SPECIES.length()) {
            var vFeatures = ShortVector.fromArray(SPECIES, features, i);
            var vWeights = ShortVector.fromArray(SPECIES, weights, i + offset);
            var vResult = vFeatures.sub(vWeights);
            vResult.intoArray(features, i);
        }

        for (; i < length; i++) {
            features[i] -= weights[i + offset];
        }
    }

    private int forward(short[] us, short[] them, short[] weights) {
        return forwardCReLU(us, weights, 0) + forwardCReLU(them, weights, Network.HIDDEN_LAYER_SIZE);
    }

    private int forwardCReLU(short[] features, short[] weights, int weightOffset) {
        short floor = 0;
        short ceil = 255;
        int sum = 0;
        int length = features.length;
        int i = 0;

        for (; i < SPECIES.loopBound(length); i += SPECIES.length()) {
            var vFeatures = ShortVector.fromArray(SPECIES, features, i);
            var vWeights = ShortVector.fromArray(SPECIES, weights, i + weightOffset);

            var vClipped = vFeatures.min(ceil).max(floor);
            var vResult = vClipped.mul(vWeights);

            sum += vResult.reduceLanes(VectorOperators.ADD);
        }

        for (; i < length; i++) {
            short clipped = (short) Math.max(Math.min(features[i], ceil), floor);
            sum += clipped * weights[i + weightOffset];
        }

        return sum;
    }

    public static int colourIndex(boolean white) {
        return white ? 1 : 0;
    }

    @Override
    public void clearHistory() {
        this.accumulator = new Accumulator(Network.HIDDEN_LAYER_SIZE);
        this.accumulatorHistory = new ArrayDeque<>();
    }


}
