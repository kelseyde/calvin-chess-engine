package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.*;
import com.kelseyde.calvin.board.Bits.File;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.evaluation.Accumulator.AccumulatorUpdate;
import com.kelseyde.calvin.evaluation.InputBucketCache.BucketCacheEntry;
import com.kelseyde.calvin.evaluation.activation.Activation;
import com.kelseyde.calvin.search.Search;
import com.kelseyde.calvin.uci.UCI;

import java.util.Arrays;

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
            .file("calvin1024_8b.nnue")
            .inputSize(768)
            .hiddenSize(1024)
            .activation(Activation.SCReLU)
            .horizontalMirror(true)
            .inputBuckets(new int[] {
                    0, 1, 2, 3, 3, 2, 1, 0,
                    4, 4, 5, 5, 5, 5, 4, 4,
                    6, 6, 6, 6, 6, 6, 6, 6,
                    6, 6, 6, 6, 6, 6, 6, 6,
                    6, 6, 6, 6, 6, 6, 6, 6,
                    7, 7, 7, 7, 7, 7, 7, 7,
                    7, 7, 7, 7, 7, 7, 7, 7,
                    7, 7, 7, 7, 7, 7, 7, 7,
            })
            .quantisations(new int[]{255, 64})
            .scale(400)
            .build();

    private static final int STACK_SIZE = Search.MAX_DEPTH + 1;

    private Accumulator[] accumulatorStack;
    private InputBucketCache bucketCache;
    private int current;
    private Board board;

    public NNUE() {
        this.current = 0;
        this.accumulatorStack = new Accumulator[STACK_SIZE];
        for (int i = 0; i < STACK_SIZE; i++) {
            this.accumulatorStack[i] = new Accumulator(NETWORK.hiddenSize());
        }
        this.bucketCache = new InputBucketCache(NETWORK.inputBucketCount());
    }

    public NNUE(Board board) {
        this.board = board;
        this.current = 0;
        this.accumulatorStack = new Accumulator[STACK_SIZE];
        for (int i = 0; i < STACK_SIZE; i++) {
            this.accumulatorStack[i] = new Accumulator(NETWORK.hiddenSize());
        }
        this.bucketCache = new InputBucketCache(NETWORK.inputBucketCount());
        fullRefresh(board);
    }

    // Evaluates the current position. Gets the 'us-perspective' and 'them-perspective' feature sets, based on
    // the side to move. Then, passes the features through the network to get the evaluation. Finally, scales
    // the evaluation based on the material and proximity to 50-move rule draw.
    public int evaluate() {

        boolean white = board.isWhite();
        Accumulator acc = accumulatorStack[current];

        short[] us = white ? acc.whiteFeatures : acc.blackFeatures;
        short[] them = white ? acc.blackFeatures : acc.whiteFeatures;

        int eval = NETWORK.activation().forward(us, them);
        eval = scaleEvaluation(board, eval);
        return eval;

    }

    // Fully refreshes the accumulator for both perspectives based on the current board state.
    private void fullRefresh(Board board) {

        Accumulator acc = accumulatorStack[current];
        boolean whiteMirror = shouldMirror(board.kingSquare(true));
        boolean blackMirror = shouldMirror(board.kingSquare(false));
        int whiteKingBucket = kingBucket(board.kingSquare(true), true);
        int blackKingBucket = kingBucket(board.kingSquare(false), false);
        fullRefresh(board, acc, true, whiteMirror, whiteKingBucket);
        fullRefresh(board, acc, false, blackMirror, blackKingBucket);

    }

    // Fully refreshes the accumulator for one perspective, using the features of all pieces on the board.
    // Uses the cached features for the current bucket if available; otherwise initializes the accumulator
    // with the initial bias values and updates it from scratch.
    private void fullRefresh(Board board, Accumulator acc, boolean whitePerspective, boolean mirror, int bucket) {

        acc.mirrored[Colour.index(whitePerspective)] = mirror;

        BucketCacheEntry cacheEntry = bucketCache.get(whitePerspective, mirror, bucket);
        short[] cachedFeatures = cacheEntry.features;

        if (cachedFeatures == null) {
            // If there is no cached accumulator for this bucket, then we will need to
            // reset every feature in the accumulator to the initial bias value.
            cachedFeatures = Arrays.copyOf(NETWORK.inputBiases(), NETWORK.inputBiases().length);
        }
        acc.copyFrom(cachedFeatures, whitePerspective);

        short[] weights = NETWORK.inputWeights()[bucket];

        int addIndex = 0, subIndex = 0;
        Feature[] adds = new Feature[32];
        Feature[] subs = new Feature[32];

        // Loop over each colour and piece type
        for (int colourIndex = 0; colourIndex < 2; colourIndex++) {
            boolean white = colourIndex == 0;
            for (int pieceIndex = 0; pieceIndex < Piece.COUNT; pieceIndex++) {

                Piece piece = Piece.values()[pieceIndex];
                long pieces = board.getPieces(piece, white);
                long cachedPieces = cacheEntry.bitboards[pieceIndex] & cacheEntry.bitboards[Piece.COUNT + colourIndex];

                // Calculate which pieces need to be added and removed from the accumulator.
                long added = pieces & ~cachedPieces;
                while (added != 0) {
                    int square = Bits.next(added);
                    adds[addIndex++] = new Feature(piece, square, white);
                    added = Bits.pop(added);
                }

                long removed = cachedPieces & ~pieces;
                while (removed != 0) {
                    int square = Bits.next(removed);
                    subs[subIndex++] = new Feature(piece, square, white);
                    removed = Bits.pop(removed);
                }

            }
        }

        // Fuse together updates to the accumulator for efficiency.
        while (addIndex >= 4)
            acc.addAddAddAdd(weights, adds[--addIndex], adds[--addIndex], adds[--addIndex], adds[--addIndex], whitePerspective);
        while (addIndex > 0)
            acc.add(weights, adds[--addIndex], whitePerspective);

        while (subIndex >= 4)
            acc.subSubSubSub(weights, subs[--subIndex], subs[--subIndex], subs[--subIndex], subs[--subIndex], whitePerspective);
        while (subIndex > 0)
            acc.sub(weights, subs[--subIndex], whitePerspective);


        // Finally, update the cache entry with the new board state and accumulated features.
        cacheEntry.bitboards = Arrays.copyOf(board.getBitboards(), Piece.COUNT + 2);
        if (cacheEntry.features == null)
            cacheEntry.features = new short[NETWORK.hiddenSize()];
        Accumulator.vectorCopy(whitePerspective ? acc.whiteFeatures : acc.blackFeatures, cacheEntry.features, NETWORK.hiddenSize());

    }


    // Efficiently update the accumulator after a move has been made, based on the type of move (standard, capture,
    // or castle). Only updates the features of the pieces that have changed, rather than recalculating the entire
    // board state. If the move causes the king to cross into a new bucket - or cross the horizontal axis - then
    // a full refresh of the accumulator is required.
    public void makeMove(Board board, Move move) {

        Accumulator prev = accumulatorStack[current];
        Accumulator curr = accumulatorStack[++current];
        curr.copyFrom(prev);

        boolean white = board.isWhite();

        Piece piece = board.pieceAt(move.from());
        int whiteKingSquare = board.kingSquare(true);
        int blackKingSquare = board.kingSquare(false);

        int whiteKingBucket = board.isWhite() ?
                calculateNewKingBucket(whiteKingSquare, move, piece, true) :
                kingBucket(whiteKingSquare, true);

        int blackKingBucket = board.isWhite() ?
                kingBucket(blackKingSquare, false) :
                calculateNewKingBucket(blackKingSquare, move, piece, false);

        short[] whiteWeights = NETWORK.inputWeights()[whiteKingBucket];
        short[] blackWeights = NETWORK.inputWeights()[blackKingBucket];

        // Determine which features need to be updated based on the move type (standard, capture, or castle).
        AccumulatorUpdate update = switch (moveType(board, move)) {
            case STANDARD -> handleStandardMove(board, move, white);
            case CASTLE -> handleCastleMove(move, white);
            case CAPTURE -> handleCapture(board, move, white);
        };

        // We must do a full accumulator refresh if either a) the network is horizontally mirrored, and the king has just
        // crossed the central axis, or b) the network has input buckets, and the king has just moved to a different bucket.
        boolean mirrorChanged = mirrorChanged(board, move, piece);
        boolean bucketChanged = bucketChanged(board, move, piece, white);
        boolean refreshRequired = mirrorChanged || bucketChanged;

        if (refreshRequired) {
            boolean mirror = shouldMirror(board.kingSquare(white));
            if (mirrorChanged) {
                mirror = !mirror;
            }
            int bucket = white ? whiteKingBucket : blackKingBucket;
            fullRefresh(board, curr, white, mirror, bucket);
            curr.apply(curr, update, whiteWeights, blackWeights);
        } else {
            curr.apply(prev, update, whiteWeights, blackWeights);
        }

    }

    // For standard moves we simply need to remove the piece from the 'from' square and add it to the 'to' square.
    private AccumulatorUpdate handleStandardMove(Board board, Move move, boolean white) {

        Piece piece = board.pieceAt(move.from());
        Piece newPiece = move.isPromotion() ? move.promoPiece() : piece;

        AccumulatorUpdate update = new AccumulatorUpdate();
        update.pushAdd(new Feature(newPiece, move.to(), white));
        update.pushSub(new Feature(piece, move.from(), white));
        return update;

    }

    // For castling moves we need to move both the king and the rook, with some special handling for Chess960.
    private AccumulatorUpdate handleCastleMove(Move move, boolean white) {

        AccumulatorUpdate update = new AccumulatorUpdate();
        boolean kingside = Castling.isKingside(move.from(), move.to());

        // In Chess960, castling is encoded as 'king captures rook'.
        int kingFrom = move.from();
        int kingTo = UCI.Options.chess960 ? Castling.kingTo(kingside, white) : move.to();
        int rookFrom = UCI.Options.chess960 ? move.to() : Castling.rookFrom(kingside, white);
        int rookTo = Castling.rookTo(kingside, white);

        update.pushSub(new Feature(Piece.KING, kingFrom, white));
        update.pushSub(new Feature(Piece.ROOK, rookFrom, white));
        update.pushAdd(new Feature(Piece.KING, kingTo, white));
        update.pushAdd(new Feature(Piece.ROOK, rookTo, white));

        return update;

    }

    // For captures, we need to remove the captured piece as well as updating the capturing piece.
    private AccumulatorUpdate handleCapture(Board board, Move move, boolean white) {

        Piece piece = board.pieceAt(move.from());
        Piece newPiece = move.isPromotion() ? move.promoPiece() : piece;
        Piece captured = move.isEnPassant() ? Piece.PAWN : board.pieceAt(move.to());

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

    // 'Unmake' the last move by decrementing the current accumulator index, resetting the head to the accumulator
    // from before the move was played.
    public void unmakeMove() {

        current--;

    }

    // Set the position of the board and clear the history. This is used to reset the evaluation when a new
    // position command is received from UCI.
    public void setPosition(Board board) {

        clearHistory();
        this.board = board;
        fullRefresh(board);

    }

    // Scale the evaluation based on the material left on the board and the proximity to the 50-move rule draw.
    // Scaling by material creates an incentive to keep pieces on the board when we have winning chances, and
    // trade them off when we're under pressure. Scaling by the 50-move rule draw gives the engine an understanding
    // of when no progress is being made in the position.
    private int scaleEvaluation(Board board, int eval) {

        eval = eval * (22400 + materialPhase(board)) / 32768;
        eval = eval * (200 - board.getState().getHalfMoveClock()) / 200;
        return eval;

    }

    // Calculate the current material phase, which is a weighted sum of the pieces on the board.
    private int materialPhase(Board board) {

        int knights = Bits.count(board.getKnights());
        int bishops = Bits.count(board.getBishops());
        int rooks = Bits.count(board.getRooks());
        int queens = Bits.count(board.getQueens());
        return 3 * knights + 3 * bishops + 5 * rooks + 10 * queens;

    }

    // Check if the horizontal mirror has changed based on the move made. If the king has crossed the central
    // axis, then we need to fully refresh the accumulator.
    private boolean mirrorChanged(Board board, Move move, Piece piece) {

        if (!NETWORK.horizontalMirror() || piece != Piece.KING) {
            return false;
        }
        int prevKingSquare = move.from();
        int currKingSquare = move.to();
        if (move.isCastling() && UCI.Options.chess960) {
            boolean kingside = Castling.isKingside(move.from(), move.to());
            currKingSquare = Castling.kingTo(kingside, board.isWhite());
        }
        return shouldMirror(prevKingSquare) != shouldMirror(currKingSquare);

    }

    // Check if the king has moved to a different input bucket based on the move made. If it has, we need to
    // fully refresh the accumulator with the weights from the new bucket.
    private boolean bucketChanged(Board board, Move move, Piece piece, boolean white) {

        if (piece != Piece.KING) {
            return false;
        }
        int prevKingSquare = move.from();
        int currKingSquare = move.to();
        if (move.isCastling() && UCI.Options.chess960) {
            boolean kingside = Castling.isKingside(move.from(), move.to());
            currKingSquare = Castling.kingTo(kingside, board.isWhite());
        }
        return kingBucket(prevKingSquare, white) != kingBucket(currKingSquare, white);

    }

    // Check if the king is on the horizontally mirrored side of the board. If it is, then all features should
    // be flipped across the central axis.
    private boolean shouldMirror(int kingSquare) {

        return NETWORK.horizontalMirror() && File.of(kingSquare) > 3;

    }

    // Calculate the new king bucket based on the move made. If the piece moved is not the king, the bucket for the
    // original king square is returned. This also takes into account castling, where special rules apply for Chess960.
    private int calculateNewKingBucket(int kingSquare, Move move, Piece piece, boolean white) {

        if (move == null) return kingBucket(kingSquare, white);
        if (piece != Piece.KING) return kingBucket(kingSquare, white);
        int to = move.to();
        if (move.isCastling()) {
            boolean kingside = Castling.isKingside(move.from(), move.to());
            to = UCI.Options.chess960 ? Castling.kingTo(kingside, board.isWhite()) : move.to();
        }
        return kingBucket(to, white);

    }

    // Get the input bucket for the current king square.
    private int kingBucket(int kingSquare, boolean white) {

        kingSquare = white ? kingSquare : Square.flipRank(kingSquare);
        return NETWORK.inputBuckets()[kingSquare];

    }

    // Determine the type of move being made based on whether it is a castling, capture, or standard move.
    private MoveType moveType(Board board, Move move) {

        if (move.isCastling())
            return MoveType.CASTLE;
        else if (board.isCapture(move))
            return MoveType.CAPTURE;
        else
            return MoveType.STANDARD;

    }

    // Clear the history of the accumulator stack and reset the current index to 0.
    public void clearHistory() {

        this.current = 0;
        this.accumulatorStack = new Accumulator[STACK_SIZE];
        for (int i = 0; i < STACK_SIZE; i++) {
            this.accumulatorStack[i] = new Accumulator(NETWORK.hiddenSize());
        }
        this.bucketCache = new InputBucketCache(NETWORK.inputBucketCount());

    }

    private enum MoveType {
        STANDARD,
        CAPTURE,
        CASTLE
    }


}
