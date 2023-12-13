package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.generation.Attacks;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.Distance;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Evaluates the current board position. Uses various heuristics to calculate a numeric value, in centipawns, estimating
 * how good the current position is for the side to move. This means that a positive score indicates the position is better
 * for the side to move, regardless of whether they are white or black.
 * <p>
 * Also includes logic for incrementally updating the evaluation during make/unmake move, which saves some time during the
 * search procedure.
 * @see <a href="https://www.chessprogramming.org/Evaluation">Chess Programming Wiki</a>
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimpleEvaluator implements Evaluation {

    final EngineConfig config;

    Board board;
    float phase;

    int mgMaterialScore;
    int egMaterialScore;
    int mgPiecePlacementScore;
    int egPiecePlacementScore;
    int mgMobilityScore;
    int egMobilityScore;
    int mgPawnStructureScore;
    int egPawnStructureScore;
    int mgRookScore;
    int egRookScore;
    int kingSafetyScore;
    int mopUpScore;
    int tempoBonus;

    long whiteKing;
    long blackKing;
    long whitePawns;
    long blackPawns;
    long whitePawnAttacks;
    long blackPawnAttacks;
    long friendlyWhiteBlockers;
    long friendlyBlackBlockers;
    long blockers;
    int isolatedPawnsCount;
    int doubledPawnsCount;

    public SimpleEvaluator(EngineConfig config) {
        this.config = config;
    }

    @Override
    public int evaluate(Board board) {

        resetScore();
        this.board = board;
        boolean isWhite = board.isWhiteToMove();
        int[] mgPieceValues = config.getPieceValues()[0];
        int[] egPieceValues = config.getPieceValues()[1];
        int[][] mgPieceSquareTables = config.getMiddlegameTables();
        int[][] egPieceSquareTables = config.getEndgameTables();

        phase = Phase.fromBoard(board);
        whiteKing = board.getWhiteKing();
        blackKing = board.getBlackKing();
        whitePawns = board.getWhitePawns();
        blackPawns = board.getBlackPawns();
        whitePawnAttacks = Attacks.pawnAttacks(whitePawns, true);
        blackPawnAttacks = Attacks.pawnAttacks(blackPawns, false);
        friendlyWhiteBlockers = whiteKing | whitePawns;
        friendlyBlackBlockers = blackKing | blackPawns;
        blockers = friendlyWhiteBlockers | friendlyBlackBlockers;

        for (int square = 0; square < 64; square++) {
            Piece piece = board.pieceAt(square);
            if (piece == null) continue;
            boolean isWhitePiece = isWhitePiece(board, square);
            int colourModifier = isWhitePiece ? 1 : -1;
            int squareIndex = isWhitePiece ? square ^ 56 : square;
            mgMaterialScore += (colourModifier * mgPieceValues[piece.getIndex()]);
            egMaterialScore += (colourModifier * egPieceValues[piece.getIndex()]);
            mgPiecePlacementScore += (colourModifier * mgPieceSquareTables[piece.getIndex()][squareIndex]);
            egPiecePlacementScore += (colourModifier * egPieceSquareTables[piece.getIndex()][squareIndex]);
            switch (piece) {
                case PAWN -> scorePawn(square, isWhitePiece);
                case KNIGHT -> scoreKnight(square, isWhitePiece);
                case BISHOP -> scoreBishop(square, isWhitePiece);
                case ROOK -> scoreRook(square, isWhitePiece);
                case QUEEN -> scoreQueen(square, isWhitePiece);
                case KING -> scoreKing(square, isWhitePiece);
            }
        }

        int materialScore = Phase.taperedEval(mgMaterialScore, egMaterialScore, phase);
        int piecePlacementScore = Phase.taperedEval(mgPiecePlacementScore, egPiecePlacementScore, phase);
        int mobilityScore = Phase.taperedEval(mgMobilityScore, egMobilityScore, phase);
        updatePawnStructureScore();
        int pawnStructureScore = Phase.taperedEval(mgPawnStructureScore, egPawnStructureScore, phase);
        int rookScore = Phase.taperedEval(mgRookScore, egRookScore, phase);
        tempoBonus = isWhite ? 10 : -10;

        int score = materialScore + piecePlacementScore + mobilityScore + pawnStructureScore +
                rookScore + kingSafetyScore + mopUpScore + tempoBonus;
        int modifier = isWhite ? 1 : -1;
        return modifier * score;

    }

    private void scorePawn(int square, boolean isWhite) {
        int file = BoardUtils.getFile(square);
        int colourModifier = isWhite ? 1 : -1;
        long friendlyPawns = isWhite ? whitePawns : blackPawns;
        long opponentPawns = isWhite ? blackPawns : whitePawns;
        if (Bitwise.isPassedPawn(square, opponentPawns, isWhite)) {
            int rank = BoardUtils.getRank(square);
            int squaresFromPromotion = isWhite ? 7 - rank : rank;
            mgPawnStructureScore += (colourModifier * config.getPassedPawnBonus()[0][squaresFromPromotion]);
            egPawnStructureScore += (colourModifier * config.getPassedPawnBonus()[1][squaresFromPromotion]);
        }
        // Passed pawns are not penalised for being isolated
        else if (Bitwise.isIsolatedPawn(file, friendlyPawns)) {
            isolatedPawnsCount += colourModifier;
        }
        if (Bitwise.isDoubledPawn(file, friendlyPawns)) {
            doubledPawnsCount += colourModifier;
        }
    }

    private void scoreKnight(int square, boolean isWhite) {
        int colourModifier = isWhite ? 1 : -1;
        long friendlyBlockers = isWhite ? friendlyWhiteBlockers : friendlyBlackBlockers;
        long opponentPawnAttacks = isWhite ? blackPawnAttacks : whitePawnAttacks;
        long attacks = Attacks.knightAttacks(square);
        long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
        int moveCount = Bitwise.countBits(moves);
        mgMobilityScore += (colourModifier * config.getMiddlegameMobilityBonus()[Piece.KNIGHT.getIndex()][moveCount]);
        egMobilityScore += (colourModifier * config.getEndgameMobilityBonus()[Piece.KNIGHT.getIndex()][moveCount]);
    }

    private void scoreBishop(int square, boolean isWhite) {
        int colourModifier = isWhite ? 1 : -1;
        long friendlyBlockers = isWhite ? friendlyWhiteBlockers : friendlyBlackBlockers;
        long opponentPawnAttacks = isWhite ? blackPawnAttacks : whitePawnAttacks;
        long attacks = Attacks.bishopAttacks(square, blockers);
        long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
        int moveCount = Bitwise.countBits(moves);
        mgMobilityScore += (colourModifier * config.getMiddlegameMobilityBonus()[Piece.BISHOP.getIndex()][moveCount]);
        egMobilityScore += (colourModifier * config.getEndgameMobilityBonus()[Piece.BISHOP.getIndex()][moveCount]);
    }

    private void scoreRook(int square, boolean isWhite) {
        int colourModifier = isWhite ? 1 : -1;
        int file = BoardUtils.getFile(square);
        long fileMask = Bits.FILE_MASKS[file];
        long friendlyPawns = isWhite ? whitePawns : blackPawns;
        long opponentPawns = isWhite ? blackPawns : whitePawns;
        long friendlyBlockers = isWhite ? friendlyWhiteBlockers : friendlyBlackBlockers;
        long opponentBlockers = isWhite ? friendlyBlackBlockers : friendlyWhiteBlockers;
        long opponentPawnAttacks = isWhite ? blackPawnAttacks : whitePawnAttacks;
        long blockers = friendlyBlockers | opponentBlockers;
        long attacks = Attacks.bishopAttacks(square, blockers);
        long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
        int moveCount = Bitwise.countBits(moves);
        mgMobilityScore += (colourModifier * config.getMiddlegameMobilityBonus()[Piece.ROOK.getIndex()][moveCount]);
        egMobilityScore += (colourModifier * config.getEndgameMobilityBonus()[Piece.ROOK.getIndex()][moveCount]);
        boolean hasFriendlyPawn = (fileMask & friendlyPawns) != 0;
        boolean hasOpponentPawn = (fileMask & opponentPawns) != 0;
        boolean hasOpenFile = !hasFriendlyPawn && !hasOpponentPawn;
        boolean hasSemiOpenFile = !hasFriendlyPawn && hasOpponentPawn;
        if (hasOpenFile) {
            mgRookScore += (colourModifier * config.getRookOpenFileBonus()[0]);
            egRookScore += (colourModifier * config.getRookOpenFileBonus()[1]);
        }
        else if (hasSemiOpenFile) {
            mgRookScore += (colourModifier * config.getRookSemiOpenFileBonus()[0]);
            egRookScore += (colourModifier * config.getRookSemiOpenFileBonus()[1]);
        }
    }

    private void scoreQueen(int square, boolean isWhite) {
        int colourModifier = isWhite ? 1 : -1;
        long friendlyBlockers = isWhite ? friendlyWhiteBlockers : friendlyBlackBlockers;
        long opponentPawnAttacks = isWhite ? blackPawnAttacks : whitePawnAttacks;
        long attacks = Attacks.bishopAttacks(square, blockers);
        long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
        int moveCount = Bitwise.countBits(moves);
        mgMobilityScore += (colourModifier * config.getMiddlegameMobilityBonus()[Piece.QUEEN.getIndex()][moveCount]);
        egMobilityScore += (colourModifier * config.getEndgameMobilityBonus()[Piece.QUEEN.getIndex()][moveCount]);
    }

    private void scoreKing(int square, boolean isWhite) {
        int colourModifier = isWhite ? 1 : -1;
        int file = BoardUtils.getFile(square);
        long friendlyPawns = isWhite ? whitePawns : blackPawns;
        long opponentPawns = isWhite ? blackPawns : whitePawns;
        long opponentKing = isWhite ? blackKing : whiteKing;
        int kingSafetyPenalty =
                -kingPawnShieldPenalty(square, file, friendlyPawns) +
                -kingLostCastlingRightsPenalty(isWhite, file) +
                -kingOpenFilePenalty(isWhite, file, friendlyPawns, opponentPawns);
        int mopUp = evaluateMopUp(square, opponentKing, isWhite);
        kingSafetyScore += (colourModifier * kingSafetyPenalty);
        mopUpScore += (colourModifier * mopUp);
    }

    private int kingPawnShieldPenalty(int kingSquare, int kingFile, long pawns) {
        boolean isCastled = kingFile <= 2 || kingFile >= 5;
        if (!isCastled) return 0;
        int pawnShieldPenalty = 0;
        long pawnShield = Bitwise.getPawnShield(kingFile, pawns);
        while (pawnShield != 0) {
            int distance = Distance.chebyshev(kingSquare, Bitwise.getNextBit(pawnShield));
            pawnShieldPenalty += config.getKingPawnShieldPenalty()[distance];
            pawnShield = Bitwise.popBit(pawnShield);
        }
        return pawnShieldPenalty;
    }

    private int kingOpenFilePenalty(boolean isWhite, int kingFile, long friendlyPawns, long opponentPawns) {
        long opponentRooks = isWhite ? board.getBlackRooks() : board.getWhiteRooks();
        long opponentQueens = isWhite ? board.getBlackQueens() : board.getWhiteQueens();
        if (opponentRooks == 0 && opponentQueens == 0) return 0;
        int penalty = 0;
        for (int file = kingFile - 1; file <= kingFile + 1; file++) {
            if (file < 0 || file > 7) {
                continue;
            }
            long fileMask = Bits.FILE_MASKS[file];
            boolean isKingFile = file == kingFile;
            boolean isFriendlyPawnMissing = (friendlyPawns & fileMask) == 0;
            boolean isOpponentPawnMissing = (opponentPawns & fileMask) == 0;
            if (isFriendlyPawnMissing || isOpponentPawnMissing) {
                // Add penalty for semi-open file around the king
                penalty += isKingFile ? config.getKingSemiOpenFilePenalty() : config.getKingSemiOpenAdjacentFilePenalty();
            }
            if (isFriendlyPawnMissing && isOpponentPawnMissing) {
                // Add penalty for fully open file around king
                penalty += isKingFile ? config.getKingOpenFilePenalty() : config.getKingSemiOpenFilePenalty();
            }
        }
        return penalty;
    }

    private int kingLostCastlingRightsPenalty(boolean isWhite, int kingFile) {
        boolean isCastled = kingFile <= 2 || kingFile >= 5;
        if (isCastled) return 0;
        boolean hasCastlingRights = board.getGameState().hasCastlingRights(isWhite);
        boolean opponentHasCastlingRights = board.getGameState().hasCastlingRights(!isWhite);
        return !hasCastlingRights && opponentHasCastlingRights ? config.getKingLostCastlingRightsPenalty() : 0;
    }

    private int evaluateMopUp(int friendlyKingSquare, long opponentKing, boolean isWhite) {
        int mopUpScore = 0;
        Material friendlyMaterial = Material.fromBoard(board, isWhite);
        Material opponentMaterial = Material.fromBoard(board, !isWhite);
        int friendlyMaterialScore = friendlyMaterial.sum(Score.SIMPLE_PIECE_VALUES, config.getBishopPairBonus());
        int opponentMaterialScore = opponentMaterial.sum(Score.SIMPLE_PIECE_VALUES, config.getBishopPairBonus());
        boolean twoPawnAdvantage = friendlyMaterialScore > (opponentMaterialScore + 2 * Piece.PAWN.getValue());
        if (!twoPawnAdvantage) return 0;
        int opponentKingSquare = Bitwise.getNextBit(opponentKing);

        // Bonus for moving king closer to opponent king
        mopUpScore += (14 - Distance.manhattan(friendlyKingSquare, opponentKingSquare)) * config.getKingManhattanDistanceMultiplier();
        mopUpScore += (7 - Distance.chebyshev(friendlyKingSquare, opponentKingSquare)) * config.getKingChebyshevDistanceMultiplier();

        // Bonus for pushing opponent king to the edges of the board
        mopUpScore += Distance.centerManhattan(opponentKingSquare) * config.getKingCenterManhattanDistanceMultiplier();

        return (int) (mopUpScore * (1 - Phase.fromMaterial(opponentMaterial)));
    }

    private boolean isWhitePiece(Board board, int i) {
        long squareMask = 1L << i;
        if ((board.getWhitePieces() & squareMask) != 0) {
            return true;
        } else if ((board.getBlackPieces() & squareMask) != 0) {
            return false;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void updatePawnStructureScore() {
        mgPawnStructureScore += config.getIsolatedPawnPenalty()[0][isolatedPawnsCount];
        egPawnStructureScore += config.getIsolatedPawnPenalty()[1][isolatedPawnsCount];
        mgPawnStructureScore += config.getDoubledPawnPenalty()[0][doubledPawnsCount];
        egPawnStructureScore += config.getDoubledPawnPenalty()[1][doubledPawnsCount];
        if (doubledPawnsCount < 0) {
            mgPawnStructureScore -= Math.abs(config.getIsolatedPawnPenalty()[0][isolatedPawnsCount]);
            egPawnStructureScore += config.getIsolatedPawnPenalty()[1][isolatedPawnsCount];
        }
    }

    private void resetScore() {
        mgMaterialScore = 0;
        egMaterialScore = 0;
        mgPiecePlacementScore = 0;
        egPiecePlacementScore = 0;
        mgMobilityScore = 0;
        egMobilityScore = 0;
        isolatedPawnsCount = 0;
        doubledPawnsCount = 0;
        mgPawnStructureScore = 0;
        egPawnStructureScore = 0;
        mgRookScore = 0;
        egRookScore = 0;
        kingSafetyScore = 0;
        mopUpScore = 0;
        tempoBonus = 0;
    }

    @Override
    public Score getScore() {
        return null;
    }

}
