package com.kelseyde.calvin.evaluation.nnue;

import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.evaluation.Evaluation;
import com.kelseyde.calvin.utils.BoardUtils;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.*;

public class NNUE implements Evaluation {

    private static final int COLOUR_STRIDE = 64 * 6;
    private static final int PIECE_STRIDE = 64;
    private static final int SCALE = 410;
    private static final int Q = 32767;
    private static final VectorSpecies<Short> SHORT_SPECIES = ShortVector.SPECIES_PREFERRED;
    private static final VectorSpecies<Integer> INT_SPECIES = IntVector.SPECIES_PREFERRED;

    List<Integer> activeFeatures = new ArrayList<>();

    public Accumulator accumulator;
    private Deque<Accumulator> accumulatorHistory = new ArrayDeque<>();

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
        short[] features = white ? accumulator.whiteFeatures : accumulator.blackFeatures;
        short[] weights = Network.DEFAULT.outputWeights;
        int output = clippedReLU(features, weights) + Network.DEFAULT.outputBias;
        double wdl = (double) output / Q;
        return (int) (Math.round(SCALE * (Math.log(wdl / (1.0 - wdl)))));
    }

    @Override
    public void makeMove(Board board, Move move) {
        accumulatorHistory.push(accumulator.copy());
        boolean white = board.isWhiteToMove();
        int startSquare = move.getStartSquare();
        int endSquare = move.getEndSquare();
        Piece piece = board.pieceAt(startSquare);
        Piece newPiece = move.isPromotion() ? move.getPromotionPiece() : piece;
        Piece capturedPiece = move.isEnPassant() ? Piece.PAWN : board.pieceAt(endSquare);

        int oldPieceWix = featureIndex(piece, startSquare, white, true);
        int oldPieceBix = featureIndex(piece, startSquare, white, false);

        int newPieceWix = featureIndex(newPiece, endSquare, white, true);
        int newPieceBix = featureIndex(newPiece, endSquare, white, false);

        if (move.isCastling()) {
            boolean isKingside = BoardUtils.getFile(endSquare) == 6;
            if (isKingside) {
                int rookStart = white ? 7 : 63;
                int rookEnd = white ? 5 : 61;
                int rookStartWix = featureIndex(Piece.ROOK, rookStart, white, true);
                int rookStartBix = featureIndex(Piece.ROOK, rookStart, white, false);
                int rookEndWix = featureIndex(Piece.ROOK, rookEnd, white, true);
                int rookEndBix = featureIndex(Piece.ROOK, rookEnd, white, false);
                accumulator.addAddSubSub(newPieceWix, newPieceBix, rookEndWix, rookEndBix, oldPieceWix, oldPieceBix, rookStartWix, rookStartBix);
            } else {
                int rookStart = white ? 0 : 56;
                int rookEnd = white ? 3 : 59;
                int rookStartWix = featureIndex(Piece.ROOK, rookStart, white, true);
                int rookStartBix = featureIndex(Piece.ROOK, rookStart, white, false);
                int rookEndWix = featureIndex(Piece.ROOK, rookEnd, white, true);
                int rookEndBix = featureIndex(Piece.ROOK, rookEnd, white, false);
                accumulator.addAddSubSub(newPieceWix, newPieceBix, rookEndWix, rookEndBix, oldPieceWix, oldPieceBix, rookStartWix, rookStartBix);
            }
        }
        else if (capturedPiece != null) {
            int captureSquare = move.getEndSquare();
            if (move.isEnPassant()) captureSquare = white ? endSquare - 8 : endSquare + 8;
            int capturedPieceWix = featureIndex(capturedPiece, captureSquare, !white, true);
            int capturedPieceBix = featureIndex(capturedPiece, captureSquare, !white, false);
            accumulator.addSubSub(newPieceWix, newPieceBix, oldPieceWix, oldPieceBix, capturedPieceWix, capturedPieceBix);
        } else {
            accumulator.addSub(newPieceWix, newPieceBix, oldPieceWix, oldPieceBix);
        }
    }

    @Override
    public void unmakeMove() {
        this.accumulator = accumulatorHistory.pop();
    }

    public void activateAll(Board board) {

        for (int i = 0; i < Network.HIDDEN_LAYER_SIZE; i++) {
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
        int squareIndex = white ? square : square ^ 56;
        int pieceIndex = piece.getIndex();
        int pieceOffset = pieceIndex * PIECE_STRIDE;
        boolean isSideToMoveFeature = white == whiteIndex;
        int colourOffset = isSideToMoveFeature ? 0 : COLOUR_STRIDE;
        return colourOffset + pieceOffset + squareIndex;
    }

    private void activate(Piece piece, int square, boolean white) {
        int whiteIndex = featureIndex(piece, square, white, true);
        int blackIndex = featureIndex(piece, square, white, false);
        accumulator.add(whiteIndex, blackIndex);
    }

    private void deactivate(Piece piece, int square, boolean white) {
        int whiteIndex = featureIndex(piece, square, white, true);
        int blackIndex = featureIndex(piece, square, white, false);
        accumulator.sub(whiteIndex, blackIndex);
    }

    private int clippedReLU(short[] features, short[] weights) {
        short floor = 0;
        int sum = 0;
        int length = features.length;

        for (int i = 0; i < length; i++) {
            short relu = (short) Math.max(features[i], floor);
            short weight = weights[i];
            sum += relu * weight / Q;
        }
        return sum;
    }


    @Override
    public void clearHistory() {
        this.accumulator = new Accumulator(Network.HIDDEN_LAYER_SIZE);
        this.accumulatorHistory = new ArrayDeque<>();
    }

}
