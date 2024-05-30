package com.kelseyde.calvin.nnue;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.notation.FEN;

import java.util.ArrayList;
import java.util.List;

public class NNUE implements Evaluation {

    // 768

    /**
     * How much to shift the feature index by based on the colour index
     */
    private static final int COLOUR_STRIDE = 64 * 6;

    /**
     * How much to shift the feature index by based on the piece index
     */
    private static final int PIECE_STRIDE = 64;

    private static final int SCALE = 400;

    private static final int Q = 255 * 64;

    public short[] whiteAccumulator;
    public short[] blackAccumulator;

    public int score;

    public NNUE() {
        this.whiteAccumulator = new short[Network.HIDDEN_LAYER_SIZE];
        this.blackAccumulator = new short[Network.HIDDEN_LAYER_SIZE];
    }

    @Override
    public int evaluate(Board board) {
        boolean white = board.isWhiteToMove();
        int output = white ?
                forward(whiteAccumulator, blackAccumulator, Network.DEFAULT.outputWeights) :
                forward(blackAccumulator, whiteAccumulator, Network.DEFAULT.outputWeights);
        return (output + Network.DEFAULT.outputBias) * SCALE / Q;
    }

    public void updateFeatures(Board board, Move move) {

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
            deactivate(Piece.PAWN, captureSquare, !white);
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

    public void activateAll(Board board) {
        System.arraycopy(Network.DEFAULT.inputBiases, 0, whiteAccumulator, 0, Network.HIDDEN_LAYER_SIZE);
        System.arraycopy(Network.DEFAULT.inputBiases, 0, blackAccumulator, 0, Network.HIDDEN_LAYER_SIZE);

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

    private void activate(Piece piece, int square, boolean white) {
        int whiteIndex = featureIndex(piece, square, white);
        int blackIndex = featureIndex(piece, square, !white);
        addWeights(whiteAccumulator, Network.DEFAULT.inputWeights, whiteIndex * Network.HIDDEN_LAYER_SIZE);
        addWeights(blackAccumulator, Network.DEFAULT.inputWeights, blackIndex * Network.HIDDEN_LAYER_SIZE);
    }

    private static int featureIndex(Piece piece, int square, boolean white) {
        int pieceIndex = piece.getIndex();
        int colourIndex = colourIndex(white);
        return colourIndex * COLOUR_STRIDE + pieceIndex * PIECE_STRIDE + square;
    }

    private void addWeights(short[] accumulator, short[] weights, int offset) {
        for (int i = 0; i < accumulator.length; i++) {
            int weightIndex = i + offset;
            accumulator[i] += weights[weightIndex];
        }
    }

    private void deactivate(Piece piece, int square, boolean white) {
        int whiteIndex = featureIndex(piece, square, white);
        int blackIndex = featureIndex(piece, square, !white);
        subtractWeights(whiteAccumulator, Network.DEFAULT.inputWeights, whiteIndex * Network.HIDDEN_LAYER_SIZE);
        subtractWeights(blackAccumulator, Network.DEFAULT.inputWeights, blackIndex * Network.HIDDEN_LAYER_SIZE);
    }

    private void subtractWeights(short[] accumulator, short[] weights, int offset) {
        for (int i = 0; i < accumulator.length; i++) {
            int weightIndex = i + offset;
            accumulator[i] -= weights[weightIndex];
        }
    }

    private int forward(short[] us, short[] them, short[] weights) {
        return forwardCReLU(us, weights, 0)
                + forwardCReLU(them, weights, Network.HIDDEN_LAYER_SIZE);
    }

    private int forwardCReLU(short[] accumulator, short[] weights, int weightOffset) {
        short floor = 0;
        short ceil = 255;
        int sum = 0;
        for (int i = 0; i < accumulator.length; i++) {
            // Here the input is activated using CReLU
            short input = (short) Math.max(Math.min(accumulator[i], ceil), floor);
            short weight = weights[i + weightOffset];
            sum += input * weight;
        }
        return sum;
    }

    private static int colourIndex(boolean white) {
        return white ? 0 : 1;
    }

    @Override
    public void clearHistory() {
        this.whiteAccumulator = new short[Network.HIDDEN_LAYER_SIZE];
        this.blackAccumulator = new short[Network.HIDDEN_LAYER_SIZE];
    }

    public static void main(String[] args) {

        NNUE nnue = new NNUE();
        Board board = FEN.toBoard("rnbqkbnr/ppp2ppp/3p4/4P3/5p2/2N2N2/PPPP2PP/R1BQKB1R w KQkq - 0 6");
        nnue.activateAll(board);
        System.out.println(nnue.evaluate(board));

        nnue.activateAll(new Board());
        System.out.println(nnue.evaluate(new Board()));
    }

}
