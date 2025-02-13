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
            .file("calvin1024_4b.nnue")
            .inputSize(768)
            .hiddenSize(1024)
            .activation(Activation.SCReLU)
            .horizontalMirror(true)
            .inputBuckets(new int[] {
                    0, 0, 1, 1, 1, 1, 0, 0,
                    2, 2, 2, 2, 2, 2, 2, 2,
                    3, 3, 3, 3, 3, 3, 3, 3,
                    3, 3, 3, 3, 3, 3, 3, 3,
                    3, 3, 3, 3, 3, 3, 3, 3,
                    3, 3, 3, 3, 3, 3, 3, 3,
                    3, 3, 3, 3, 3, 3, 3, 3,
                    3, 3, 3, 3, 3, 3, 3, 3,
            })
            .quantisations(new int[]{255, 64})
            .scale(400)
            .build();


    private Accumulator[] accumulatorStack;
    private InputBucketCache bucketCache;
    private int current;
    private Board board;

    public NNUE() {
        this.current = 0;
        this.accumulatorStack = new Accumulator[Search.MAX_DEPTH];
        this.accumulatorStack[current] = new Accumulator(NETWORK.hiddenSize());
        this.bucketCache = new InputBucketCache(NETWORK.inputBucketCount());
    }

    public NNUE(Board board) {
        this.board = board;
        this.current = 0;
        this.accumulatorStack = new Accumulator[Search.MAX_DEPTH];
        this.accumulatorStack[current] = new Accumulator(NETWORK.hiddenSize());
        this.bucketCache = new InputBucketCache(NETWORK.inputBucketCount());
        fullRefresh(board);
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

    private void fullRefresh(Board board) {

        // Fully refresh the accumulator from both perspectives with the features of all pieces on the board.
        final Accumulator acc = accumulatorStack[current];
        final boolean whiteMirror = shouldMirror(board.kingSquare(true));
        final boolean blackMirror = shouldMirror(board.kingSquare(false));
        int whiteKingBucket = kingBucket(board.kingSquare(true), true);
        int blackKingBucket = kingBucket(board.kingSquare(false), false);
        fullRefresh(board, acc, true, whiteMirror, whiteKingBucket);
        fullRefresh(board, acc, false, blackMirror, blackKingBucket);

    }

    private void fullRefresh(Board board, Accumulator acc, boolean whitePerspective, boolean mirror, int bucket) {


        BucketCacheEntry cacheEntry = bucketCache.get(whitePerspective, mirror, bucket);
        short[] cachedFeatures = cacheEntry.features;

        if (cachedFeatures == null) {
            // If there is no cached accumulator for this bucket, then we will need to
            // reset every feature in the accumulator to the initial bias value.
            cachedFeatures = Arrays.copyOf(NETWORK.inputBiases(), NETWORK.inputBiases().length);
        }
        acc.copyFrom(cachedFeatures, whitePerspective);

        final short[] weights = NETWORK.inputWeights()[bucket];

        // Loop over each colour and piece type
        for (int colourIndex = 0; colourIndex < 2; colourIndex++) {
            final boolean white = colourIndex == 0;
            for (int pieceIndex = 0; pieceIndex < Piece.COUNT; pieceIndex++) {

                final Piece piece = Piece.values()[pieceIndex];
                final long pieces = board.getPieces(piece, white);
                final long cachedPieces = cacheEntry.bitboards[pieceIndex] & cacheEntry.bitboards[Piece.COUNT + colourIndex];

                // Calculate which pieces need to be added and removed from the accumulator.
                long added = pieces & ~cachedPieces;
                while (added != 0) {
                    final int square = Bits.next(added);
                    Feature feature = new Feature(piece, square, white);
                    acc.add(weights, feature, whitePerspective, mirror);
                    added = Bits.pop(added);
                }

                long removed = cachedPieces & ~pieces;
                while (removed != 0) {
                    final int square = Bits.next(removed);
                    Feature feature = new Feature(piece, square, white);
                    acc.sub(weights, feature, whitePerspective, mirror);
                    removed = Bits.pop(removed);
                }

            }
        }

        acc.needsRefresh[Colour.index(whitePerspective)] = false;
        acc.computed[Colour.index(whitePerspective)] = true;

        // Finally, update the cache entry with the new board state and accumulated features.
        cacheEntry.bitboards = Arrays.copyOf(board.getBitboards(), Piece.COUNT + 2);
        cacheEntry.features = Arrays.copyOf(whitePerspective ? acc.whiteFeatures : acc.blackFeatures, NETWORK.hiddenSize());

    }


    public void makeMove(Board board, Move move) {

        // Efficiently update only the relevant features of the network after a move has been made.
        final Accumulator acc = accumulatorStack[++current] = accumulatorStack[current - 1].copy();
        acc.computed[Colour.WHITE] = acc.computed[Colour.BLACK] = false;
        final boolean white = board.isWhite();

        final Piece piece = board.pieceAt(move.from());

        final int whiteKingSquare = board.kingSquare(true);
        final int blackKingSquare = board.kingSquare(false);

        final int whiteKingBucket = calculateNewKingBucket(board, whiteKingSquare, move, piece, true);
        final int blackKingBucket = calculateNewKingBucket(board, blackKingSquare, move, piece, false);

        final boolean whiteMirror = calculateNewMirror(board, whiteKingSquare, move, piece, true);
        final boolean blackMirror = calculateNewMirror(board, blackKingSquare, move, piece, false);

        // We must do a full accumulator refresh if either a) the network is horizontally mirrored, and the king has just
        // crossed the central axis, or b) the network has input buckets, and the king has just moved to a different bucket.
        final boolean mirrorChanged = mirrorChanged(board, move, piece);
        final boolean bucketChanged = bucketChanged(board, move, piece, white);
        if (mirrorChanged || bucketChanged) {
            acc.needsRefresh[Colour.index(white)] = true;
        }

        // Determine which features need to be updated based on the move type (standard, capture, or castle).
        // Queue the update to be applied lazily next time evaluate() is called.
        AccumulatorUpdate update = switch (moveType(board, move)) {
            case STANDARD -> handleStandardMove(board, move, white);
            case CASTLE -> handleCastleMove(move, white);
            case CAPTURE -> handleCapture(board, move, white);
        };

        update.kingBucket[Colour.WHITE] = whiteKingBucket;
        update.kingBucket[Colour.BLACK] = blackKingBucket;
        update.mirrored[Colour.WHITE] = whiteMirror;
        update.mirrored[Colour.BLACK] = blackMirror;

        acc.update = update;

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

    private void applyLazyUpdates() {

        final Accumulator acc = accumulatorStack[current];

        for (int whitePerspective = 0; whitePerspective < 2; whitePerspective++) {

            final boolean white = whitePerspective == Colour.WHITE;

            // If the current state if already correct for our perspective, no action is required.
            if (acc.computed[whitePerspective])
                continue;

            if (acc.needsRefresh[whitePerspective]) {
                final boolean mirror = acc.update.mirrored[whitePerspective];
                final int bucket = acc.update.kingBucket[whitePerspective];
                fullRefresh(board, acc, white, mirror, bucket);
                continue;
            }

            int index = current - 1;
            Accumulator curr = accumulatorStack[index];

            while (index > 0
                    && !curr.computed[whitePerspective]
                    && !curr.needsRefresh[whitePerspective])
                index--;

            if (curr.needsRefresh[whitePerspective]) {
                //  The most recent accumulator would need to be refreshed,
                //  so don't bother and refresh the current one instead
                final boolean mirror = acc.update.mirrored[whitePerspective];
                final int bucket = acc.update.kingBucket[whitePerspective];
                fullRefresh(board, acc, white, mirror, bucket);
            }
            else {

                // Apply the updates from the previous state to the current state
                while (index < current) {
                    Accumulator prev = curr;
                    curr = accumulatorStack[++index];

                    if (prev.update == null) {
                        System.out.println("index: " + index);
                    }

//                    if (prev.update.kingBucket[whitePerspective] != curr.update.kingBucket[whitePerspective]) {
//                        System.out.println("King bucket mismatch");
//                    }
//
//                    if (prev.update.mirrored[whitePerspective] != curr.update.mirrored[whitePerspective]) {
//                        System.out.println("Mirror mismatch");
//                    }

                    int bucket = curr.update.kingBucket[whitePerspective];
                    final short[] weights = NETWORK.inputWeights()[bucket];
                    final short[] features = white ? prev.whiteFeatures : prev.blackFeatures;

                    curr.copyFrom(features, white);
                    curr.apply(curr.update, weights, white);
                    curr.computed[whitePerspective] = true;
                }
            }


        }

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

    private boolean mirrorChanged(Board board, Move move, Piece piece) {
        if (!NETWORK.horizontalMirror() || piece != Piece.KING) {
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

    private boolean bucketChanged(Board board, Move move, Piece piece, boolean white) {
        if (piece != Piece.KING) {
            return false;
        }
        int prevKingSquare = move.from();
        int currKingSquare = move.to();
        if (move.isCastling() && UCI.Options.chess960) {
            final boolean kingside = Castling.isKingside(move.from(), move.to());
            currKingSquare = Castling.kingTo(kingside, board.isWhite());
        }
        return kingBucket(prevKingSquare, white) != kingBucket(currKingSquare, white);
    }

    private boolean calculateNewMirror(Board board, int kingSquare, Move move, Piece piece, boolean white) {
        if (board.isWhite() != white || move == null || piece != Piece.KING) {
            return shouldMirror(kingSquare);
        }
        int to = move.to();
        if (move.isCastling()) {
            final boolean kingside = Castling.isKingside(move.from(), move.to());
            to = UCI.Options.chess960 ? Castling.kingTo(kingside, board.isWhite()) : move.to();
        }
        return shouldMirror(to);
    }

    private boolean shouldMirror(int kingSquare) {
        return NETWORK.horizontalMirror() && File.of(kingSquare) > 3;
    }

    private int calculateNewKingBucket(Board board, int kingSquare, Move move, Piece piece, boolean white) {
        if (board.isWhite() != white || move == null || piece != Piece.KING) {
            return kingBucket(kingSquare, white);
        }
        int to = move.to();
        if (move.isCastling()) {
            final boolean kingside = Castling.isKingside(move.from(), move.to());
            to = UCI.Options.chess960 ? Castling.kingTo(kingside, board.isWhite()) : move.to();
        }
        return kingBucket(to, white);
    }

    private int kingBucket(int kingSquare, boolean white) {
        if (!white) {
            kingSquare = Square.flipRank(kingSquare);
        }
        return NETWORK.inputBuckets()[kingSquare];
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
        this.bucketCache = new InputBucketCache(NETWORK.inputBucketCount());
    }

    private enum MoveType {
        STANDARD,
        CAPTURE,
        CASTLE
    }

}
