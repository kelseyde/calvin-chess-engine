package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.generation.Attacks;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.Distance;
import com.kelseyde.calvin.utils.notation.Notation;
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

    int mgWhiteMaterialScore;
    int mgBlackMaterialScore;
    int egWhiteMaterialScore;
    int egBlackMaterialScore;

    int whitePiecePlacementScore;
    int blackPiecePlacementScore;

    int mgWhiteMobilityScore;
    int mgBlackMobilityScore;
    int egWhiteMobilityScore;
    int egBlackMobilityScore;

    int mgWhitePawnStructureScore;
    int mgBlackPawnStructureScore;
    int egWhitePawnStructureScore;
    int egBlackPawnStructureScore;

    int mgWhiteRookScore;
    int mgBlackRookScore;
    int egWhiteRookScore;
    int egBlackRookScore;
    int whiteKingSafetyScore;
    int blackKingSafetyScore;
    int whiteMopUpScore;
    int blackMopUpScore;
    int whiteTempoBonus;
    int blackTempoBonus;

    long whiteKing;
    long blackKing;
    long whitePawns;
    long blackPawns;
    long whitePawnAttacks;
    long blackPawnAttacks;
    long friendlyWhiteBlockers;
    long friendlyBlackBlockers;
    int whiteIsolatedPawnsCount;
    int blackIsolatedPawnsCount;
    int whiteDoubledPawnsCount;
    int blackDoubledPawnsCount;

    public SimpleEvaluator(EngineConfig config) {
        this.config = config;
    }

    @Override
    public int evaluate(Board board) {

        resetScore();
        this.board = board;
        int[] mgPieceValues = config.getPieceValues()[0];
        int[] egPieceValues = config.getPieceValues()[1];
        int[][] mgPieceSquareTables = config.getMiddlegameTables();
        int[][] egPieceSquareTables = config.getEndgameTables();
        initPieceData();

        for (int square = 0; square < 64; square++) {
            Piece piece = board.pieceAt(square);
            if (piece == null) continue;
            boolean isWhitePiece = isWhitePiece(board, square);
            int squareIndex = isWhitePiece ? square ^ 56 : square;
            int mgMaterialScore = mgPieceValues[piece.getIndex()];
            int egMaterialScore = egPieceValues[piece.getIndex()];
            addMaterialScore(mgMaterialScore, egMaterialScore, isWhitePiece);
            int mgPiecePlacementScore = mgPieceSquareTables[piece.getIndex()][squareIndex];
            int egPiecePlacementScore = egPieceSquareTables[piece.getIndex()][squareIndex];
            addPiecePlacementScore(mgPiecePlacementScore, egPiecePlacementScore, isWhitePiece);
            switch (piece) {
                case PAWN -> scorePawn(square, isWhitePiece);
                case KNIGHT -> scoreKnight(square, isWhitePiece);
                case BISHOP -> scoreBishop(square, isWhitePiece);
                case ROOK -> scoreRook(square, isWhitePiece);
                case QUEEN -> scoreQueen(square, isWhitePiece);
                case KING -> scoreKing(square, isWhitePiece);
            }
        }
        addBishopPairBonus();
        updatePawnStructureScore();
        addTempoBonus();

        return sum();

    }

    private void scorePawn(int square, boolean isWhite) {
        int file = BoardUtils.getFile(square);
        long friendlyPawns = isWhite ? whitePawns : blackPawns;
        long opponentPawns = isWhite ? blackPawns : whitePawns;
        if (Bitwise.isPassedPawn(square, opponentPawns, isWhite)) {
            int rank = BoardUtils.getRank(square);
            int squaresFromPromotion = isWhite ? 7 - rank : rank;
            addPawnStructureScore(config.getPassedPawnBonus()[0][squaresFromPromotion], config.getPassedPawnBonus()[1][squaresFromPromotion], isWhite);
        }
        // Passed pawns are not penalised for being isolated
        else if (Bitwise.isIsolatedPawn(file, friendlyPawns)) {
            if (isWhite) whiteIsolatedPawnsCount++;
            else blackIsolatedPawnsCount++;
        }
        if (Bitwise.isDoubledPawn(file, friendlyPawns)) {
            if (isWhite) whiteDoubledPawnsCount++;
            else blackDoubledPawnsCount++;
        }
    }

    private void scoreKnight(int square, boolean isWhite) {
        long friendlyBlockers = isWhite ? friendlyWhiteBlockers : friendlyBlackBlockers;
        long opponentPawnAttacks = isWhite ? blackPawnAttacks : whitePawnAttacks;
        long attacks = Attacks.knightAttacks(square);
        long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
        int moveCount = Bitwise.countBits(moves);
        //Sytem.out.printf("knight moves %s%n", moveCount);

        int mgScore = config.getMiddlegameMobilityBonus()[Piece.KNIGHT.getIndex()][moveCount];
        int egScore = config.getEndgameMobilityBonus()[Piece.KNIGHT.getIndex()][moveCount];
        //Sytem.out.printf("knight mob %s %s %n", mgScore, egScore);

        addMobilityScore(mgScore, egScore, isWhite);
    }

    private void scoreBishop(int square, boolean isWhite) {
        long friendlyBlockers = isWhite ? friendlyWhiteBlockers : friendlyBlackBlockers;
        long opponentBlockers = isWhite ? board.getBlackPieces() : board.getWhitePieces();
        long opponentPawnAttacks = isWhite ? blackPawnAttacks : whitePawnAttacks;
        long blockers = friendlyBlockers | opponentBlockers;
        long attacks = Attacks.bishopAttacks(square, blockers);
        long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
        int moveCount = Bitwise.countBits(moves);
        //Sytem.out.printf("bishop moves %s%n", moveCount);

        int mgScore = config.getMiddlegameMobilityBonus()[Piece.BISHOP.getIndex()][moveCount];
        int egScore = config.getEndgameMobilityBonus()[Piece.BISHOP.getIndex()][moveCount];
        //Sytem.out.printf("bishop mob %s %s %n", mgScore, egScore);

        addMobilityScore(mgScore, egScore, isWhite);
    }

    private void scoreRook(int square, boolean isWhite) {
        int file = BoardUtils.getFile(square);
        long fileMask = Bits.FILE_MASKS[file];

        long friendlyPawns = isWhite ? whitePawns : blackPawns;
        long opponentPawns = isWhite ? blackPawns : whitePawns;
        long friendlyBlockers = isWhite ? friendlyWhiteBlockers : friendlyBlackBlockers;
        long opponentBlockers = isWhite ? board.getBlackPieces() : board.getWhitePieces();
        long opponentPawnAttacks = isWhite ? blackPawnAttacks : whitePawnAttacks;
        long blockers = friendlyBlockers | opponentBlockers;
        long attacks = Attacks.rookAttacks(square, blockers);
        long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
        int moveCount = Bitwise.countBits(moves);
        //Sytem.out.printf("rook moves %s%n", moveCount);

        int mgScore = config.getMiddlegameMobilityBonus()[Piece.ROOK.getIndex()][moveCount];
        int egScore = config.getEndgameMobilityBonus()[Piece.ROOK.getIndex()][moveCount];
        //Sytem.out.printf("rook mob %s %s %n", mgScore, egScore);
        addMobilityScore(mgScore, egScore, isWhite);

        boolean hasFriendlyPawn = (fileMask & friendlyPawns) != 0;
        boolean hasOpponentPawn = (fileMask & opponentPawns) != 0;
        boolean hasOpenFile = !hasFriendlyPawn && !hasOpponentPawn;
        boolean hasSemiOpenFile = !hasFriendlyPawn && hasOpponentPawn;
        if (hasOpenFile) {
            addRookScore(config.getRookOpenFileBonus()[0], config.getRookOpenFileBonus()[1], isWhite);
        }
        else if (hasSemiOpenFile) {
            addRookScore(config.getRookSemiOpenFileBonus()[0], config.getRookSemiOpenFileBonus()[1], isWhite);
        }
    }

    private void scoreQueen(int square, boolean isWhite) {
        long friendlyBlockers = isWhite ? friendlyWhiteBlockers : friendlyBlackBlockers;
        long opponentBlockers = isWhite ? board.getBlackPieces() : board.getWhitePieces();
        long opponentPawnAttacks = isWhite ? blackPawnAttacks : whitePawnAttacks;
        long blockers = friendlyBlockers | opponentBlockers;
        long attacks = Attacks.bishopAttacks(square, blockers) | Attacks.rookAttacks(square, blockers);
        long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
        int moveCount = Bitwise.countBits(moves);

        int mgScore = config.getMiddlegameMobilityBonus()[Piece.QUEEN.getIndex()][moveCount];
        int egScore = config.getEndgameMobilityBonus()[Piece.QUEEN.getIndex()][moveCount];

        addMobilityScore(mgScore, egScore, isWhite);
    }

    private void scoreKing(int square, boolean isWhite) {
        int file = BoardUtils.getFile(square);
        long friendlyPawns = isWhite ? whitePawns : blackPawns;
        long opponentPawns = isWhite ? blackPawns : whitePawns;
        long opponentKing = isWhite ? blackKing : whiteKing;
        int kingSafetyPenalty =
                -kingPawnShieldPenalty(square, file, friendlyPawns) +
                -kingLostCastlingRightsPenalty(isWhite, file) +
                -kingOpenFilePenalty(isWhite, file, friendlyPawns, opponentPawns);
        int mopUpScore = evaluateMopUp(square, opponentKing, isWhite);
        setKingSafetyScore(kingSafetyPenalty, isWhite);
        setMopUpScore(mopUpScore, isWhite);
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
        int mgWhiteIsolatedPawnPenalty = config.getIsolatedPawnPenalty()[0][whiteIsolatedPawnsCount];
        int egWhiteIsolatedPawnPenalty = config.getIsolatedPawnPenalty()[1][whiteIsolatedPawnsCount];
        int mgWhiteDoubledPawnPenalty = config.getDoubledPawnPenalty()[0][whiteDoubledPawnsCount];
        int egWhiteDoubledPawnPenalty = config.getDoubledPawnPenalty()[1][whiteDoubledPawnsCount];
        addPawnStructureScore(mgWhiteIsolatedPawnPenalty, egWhiteIsolatedPawnPenalty, true);
        addPawnStructureScore(mgWhiteDoubledPawnPenalty, egWhiteDoubledPawnPenalty, true);

        int mgBlackIsolatedPawnPenalty = config.getIsolatedPawnPenalty()[0][blackIsolatedPawnsCount];
        int egBlackIsolatedPawnPenalty = config.getIsolatedPawnPenalty()[1][blackIsolatedPawnsCount];
        int mgBlackDoubledPawnPenalty = config.getDoubledPawnPenalty()[0][blackDoubledPawnsCount];
        int egBlackDoubledPawnPenalty = config.getDoubledPawnPenalty()[1][blackDoubledPawnsCount];
        addPawnStructureScore(mgBlackIsolatedPawnPenalty, egBlackIsolatedPawnPenalty, false);
        addPawnStructureScore(mgBlackDoubledPawnPenalty, egBlackDoubledPawnPenalty, false);
    }

    private void addTempoBonus() {
        if (board.isWhiteToMove()) whiteTempoBonus = 10; else blackTempoBonus = 10;
    }

    private void resetScore() {
        mgWhiteMaterialScore = 0;
        mgBlackMaterialScore = 0;
        egWhiteMaterialScore = 0;
        egBlackMaterialScore = 0;
        whitePiecePlacementScore = 0;
        blackPiecePlacementScore = 0;
        mgWhiteMobilityScore = 0;
        mgBlackMobilityScore = 0;
        egWhiteMobilityScore = 0;
        egBlackMobilityScore = 0;
        mgWhitePawnStructureScore = 0;
        mgBlackPawnStructureScore = 0;
        egWhitePawnStructureScore = 0;
        egBlackPawnStructureScore = 0;
        whiteDoubledPawnsCount = 0;
        blackDoubledPawnsCount = 0;
        whiteIsolatedPawnsCount = 0;
        blackIsolatedPawnsCount = 0;
        mgWhiteRookScore = 0;
        mgBlackRookScore = 0;
        egWhiteRookScore = 0;
        egBlackRookScore = 0;
        whiteKingSafetyScore = 0;
        blackKingSafetyScore = 0;
        whiteMopUpScore = 0;
        blackMopUpScore = 0;
        whiteTempoBonus = 0;
        blackTempoBonus = 0;
    }

    private void initPieceData() {
        phase = Phase.fromBoard(board);
        whiteKing = board.getWhiteKing();
        blackKing = board.getBlackKing();
        whitePawns = board.getWhitePawns();
        blackPawns = board.getBlackPawns();
        whitePawnAttacks = Attacks.pawnAttacks(whitePawns, true);
        blackPawnAttacks = Attacks.pawnAttacks(blackPawns, false);
        friendlyWhiteBlockers = whiteKing | whitePawns;
        friendlyBlackBlockers = blackKing | blackPawns;
    }

    private int sum() {
        int whiteMaterialScore = Phase.taperedEval(mgWhiteMaterialScore, egWhiteMaterialScore, phase);
        //Sytem.out.printf("wmat %s %s %s: %s%n", mgWhiteMaterialScore, egWhiteMaterialScore, phase, whiteMaterialScore);
        int blackMaterialScore = Phase.taperedEval(mgBlackMaterialScore, egBlackMaterialScore, phase);

        //Sytem.out.printf("new wmob %s %s %n", mgWhiteMobilityScore, egWhiteMobilityScore);
        int whiteMobilityScore = Phase.taperedEval(mgWhiteMobilityScore, egWhiteMobilityScore, phase);
        int blackMobilityScore = Phase.taperedEval(mgBlackMobilityScore, egBlackMobilityScore, phase);

        int whitePawnStructureScore = Phase.taperedEval(mgWhitePawnStructureScore, egWhitePawnStructureScore, phase);
        int blackPawnStructureScore = Phase.taperedEval(mgBlackPawnStructureScore, egBlackPawnStructureScore, phase);

        int whiteRookScore = Phase.taperedEval(mgWhiteRookScore, egWhiteRookScore, phase);
        int blackRookScore = Phase.taperedEval(mgBlackRookScore, egBlackRookScore, phase);

        int whiteScore = whiteMaterialScore + whitePiecePlacementScore + whiteMobilityScore + whitePawnStructureScore +
                whiteKingSafetyScore + whiteRookScore + whiteMopUpScore + whiteTempoBonus;

        int blackScore = blackMaterialScore + blackPiecePlacementScore + blackMobilityScore + blackPawnStructureScore +
                blackKingSafetyScore + blackRookScore + blackMopUpScore + blackTempoBonus;

        //Sytem.out.println(Notation.toNotation(board.getMoveHistory()));
//        System.out.printf("""
//                        [new]
//                        wm: %s
//                        bm: %s
//                        wpp: %s
//                        bpp: %s
//                        wmob: %s
//                        bmob: %s
//                        wpawn: %s
//                        bpawn: %s
//                        wrook: %s
//                        brook: %s
//                        wking: %s
//                        bking: %s
//                        wmop: %s
//                        bmop: %s
//                        wtemp: %s
//                        btemp: %s%n""",
//                whiteMaterialScore, blackMaterialScore, whitePiecePlacementScore, blackPiecePlacementScore, whiteMobilityScore,
//                blackMobilityScore, whitePawnStructureScore, blackPawnStructureScore, whiteRookScore, blackRookScore,
//                whiteKingSafetyScore, blackKingSafetyScore, whiteMopUpScore, blackMopUpScore, whiteTempoBonus, blackTempoBonus);
        int modifier = board.isWhiteToMove() ? 1 : -1;
        return modifier * (whiteScore - blackScore);
    }

    private void addMaterialScore(int middlegameScore, int endgameScore, boolean isWhite) {
        if (isWhite) {
            mgWhiteMaterialScore += middlegameScore;
            egWhiteMaterialScore += endgameScore;
        } else {
            mgBlackMaterialScore += middlegameScore;
            egBlackMaterialScore += endgameScore;
        }
    }

    private void addPiecePlacementScore(int middlegameScore, int endgameScore, boolean isWhite) {
        if (isWhite) {
            whitePiecePlacementScore += Phase.taperedEval(middlegameScore, endgameScore, phase);
        } else {
            blackPiecePlacementScore += Phase.taperedEval(middlegameScore, endgameScore, phase);
        }
    }

    private void addMobilityScore(int middlegameScore, int endgameScore, boolean isWhite) {
        if (isWhite) {
            mgWhiteMobilityScore += middlegameScore;
            egWhiteMobilityScore += endgameScore;
        } else {
            mgBlackMobilityScore += middlegameScore;
            egBlackMobilityScore += endgameScore;
        }
    }

    private void addPawnStructureScore(int middlegameScore, int endgameScore, boolean isWhite) {
        if (isWhite) {
            mgWhitePawnStructureScore += middlegameScore;
            egWhitePawnStructureScore += endgameScore;
        } else {
            mgBlackPawnStructureScore += middlegameScore;
            egBlackPawnStructureScore += endgameScore;
        }
    }

    private void addBishopPairBonus() {
        if (Bitwise.countBits(board.getWhiteBishops()) == 2) {
            addMaterialScore(config.getBishopPairBonus(), config.getBishopPairBonus(), true);
        }
        if (Bitwise.countBits(board.getBlackBishops()) == 2) {
            addMaterialScore(config.getBishopPairBonus(), config.getBishopPairBonus(), false);
        }
    }

    private void addRookScore(int middlegameScore, int endgameScore, boolean isWhite) {
        if (isWhite) {
            mgWhiteRookScore += middlegameScore;
            egWhiteRookScore += endgameScore;
        } else {
            mgBlackRookScore += middlegameScore;
            egBlackRookScore += endgameScore;
        }
    }

    private void setKingSafetyScore(int score, boolean isWhite) {
        if (isWhite) {
            whiteKingSafetyScore = score;
        } else {
            blackKingSafetyScore = score;
        }
    }

    private void setMopUpScore(int score, boolean isWhite) {
        if (isWhite) {
            whiteMopUpScore = score;
        } else {
            blackMopUpScore = score;
        }
    }

    @Override
    public Score getScore() {
        return null;
    }

}
