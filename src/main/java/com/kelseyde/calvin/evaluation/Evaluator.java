package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.evaluation.score.*;
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
public class Evaluator implements Evaluation {

    final EngineConfig config;

    Score score;

    public Evaluator(EngineConfig config) {
        this.config = config;
    }

    @Override
    public int evaluate(Board board) {

        score = new Score();
        boolean isWhite = board.isWhiteToMove();

        long whitePieces = board.getWhitePieces();
        long blackPieces = board.getBlackPieces();

        long whitePawns = board.getWhitePawns();
        long whiteKnights = board.getWhiteKnights();
        long whiteBishops = board.getWhiteBishops();
        long whiteRooks = board.getWhiteRooks();
        long whiteQueens = board.getWhiteQueens();
        long whiteKing = board.getWhiteKing();
        long blackPawns = board.getBlackPawns();
        long blackKnights = board.getBlackKnights();
        long blackBishops = board.getBlackBishops();
        long blackRooks = board.getBlackRooks();
        long blackQueens = board.getBlackQueens();
        long blackKing = board.getBlackKing();

        Material whiteMaterial = Material.fromBoard(board, true);
        Material blackMaterial = Material.fromBoard(board, false);

        float phase = Phase.fromMaterial(whiteMaterial, blackMaterial);
        score.setPhase(phase);

        int whiteMaterialMiddlegameScore = whiteMaterial.sum(config.getPieceValues()[0], config.getBishopPairBonus());
        int whiteMaterialEndgameScore = whiteMaterial.sum(config.getPieceValues()[1], config.getBishopPairBonus());
        score.addMaterialScore(whiteMaterialMiddlegameScore, whiteMaterialEndgameScore, true);

        int blackMaterialMiddlegameScore = blackMaterial.sum(config.getPieceValues()[0], config.getBishopPairBonus());
        int blackMaterialEndgameScore = blackMaterial.sum(config.getPieceValues()[1], config.getBishopPairBonus());;
        score.addMaterialScore(blackMaterialMiddlegameScore, blackMaterialEndgameScore, false);

        // Blockers used during mobility calculations
        long friendlyWhiteBlockers = whiteKing | whitePawns;
        long friendlyBlackBlockers = blackKing | blackPawns;

        // Pawn attacks used during mobility calculations
        long whitePawnAttacks = Attacks.pawnAttacks(whitePawns, true);
        long blackPawnAttacks = Attacks.pawnAttacks(blackPawns, false);

        scorePawns(whitePawns, blackPawns, true);
        scorePawns(blackPawns, whitePawns, false);

        scoreKnights(whiteKnights, friendlyWhiteBlockers, blackPawnAttacks, true);
        scoreKnights(blackKnights, friendlyBlackBlockers, whitePawnAttacks, false);

        scoreBishops(whiteBishops, friendlyWhiteBlockers, blackPieces, blackPawnAttacks, true);
        scoreBishops(blackBishops, friendlyBlackBlockers, whitePieces, whitePawnAttacks, false);

        scoreRooks(whiteRooks, whitePawns, blackPawns, friendlyWhiteBlockers, blackPieces, blackPawnAttacks, true);
        scoreRooks(blackRooks, blackPawns, whitePawns, friendlyBlackBlockers, whitePieces, whitePawnAttacks, false);

        scoreQueens(whiteQueens, friendlyWhiteBlockers, blackPieces, blackPawnAttacks, true);
        scoreQueens(blackQueens, friendlyBlackBlockers, whitePieces, whitePawnAttacks, false);

        scoreKing(whiteKing, blackKing, whitePawns, blackPawns, whiteMaterial, blackMaterial, board, phase, true);
        scoreKing(blackKing, whiteKing, blackPawns, whitePawns, blackMaterial, whiteMaterial, board, phase, false);

        int whiteTempoBonus = board.isWhiteToMove() ? config.getTempoBonus() : 0;
        int blackTempoBonus = board.isWhiteToMove() ? 0 : config.getTempoBonus();
        score.setWhiteTempoBonus(whiteTempoBonus);
        score.setBlackTempoBonus(blackTempoBonus);

        return score.sum(isWhite);

    }

    /**
     * Pawn evaluation consists of piece-placement eval + pawn structure considerations (bonuses for passed pawns,
     * penalties for isolated/doubled pawns).
     */
    private void scorePawns(long friendlyPawns,
                            long opponentPawns,
                            boolean isWhite) {
        int piecePlacementMgScore = 0;
        int piecePlacementEgScore = 0;
        int pawnStructureMgScore = 0;
        int pawnStructureEgScore = 0;

        int isolatedPawnCount = 0;
        int doubledPawnCount = 0;

        int[] mgPieceSquareTable = config.getMiddlegameTables()[Piece.PAWN.getIndex()];
        int[] egPieceSquareTable = config.getEndgameTables()[Piece.PAWN.getIndex()];

        long pawnsIterator = friendlyPawns;
        while (pawnsIterator > 0) {
            int pawn = Bitwise.getNextBit(pawnsIterator);
            int file = BoardUtils.getFile(pawn);

            int squareIndex = isWhite ? pawn ^ 56 : pawn;
            piecePlacementMgScore += mgPieceSquareTable[squareIndex];
            piecePlacementEgScore += egPieceSquareTable[squareIndex];

            // Bonuses for a passed pawn, indexed by the number of squares away that pawn is from promotion.
            // Bonus for a passed pawn that is additionally protected by another pawn (multiplied by number of defending pawns).
            if (isPassedPawn(pawn, opponentPawns, isWhite)) {
                pawnStructureMgScore += calculatePassedPawnBonus(pawn, isWhite, config.getPassedPawnBonus()[0]);
                pawnStructureMgScore += calculateProtectedPawnBonus(config, pawn, friendlyPawns, isWhite);
                pawnStructureEgScore += calculatePassedPawnBonus(pawn, isWhite, config.getPassedPawnBonus()[1]);
                pawnStructureEgScore += calculateProtectedPawnBonus(config, pawn, friendlyPawns, isWhite);
            }
            // Passed pawns are not penalised for being isolated
            else if (isIsolatedPawn(file, friendlyPawns)) {
                isolatedPawnCount++;
            }
            if (isDoubledPawn(file, friendlyPawns)) {
                doubledPawnCount++;
            }

            pawnsIterator = Bitwise.popBit(pawnsIterator);
        }

        // Penalties for isolated pawns, indexed by the number of isolated pawns.
        pawnStructureMgScore += config.getIsolatedPawnPenalty()[0][isolatedPawnCount];
        pawnStructureEgScore += config.getIsolatedPawnPenalty()[1][isolatedPawnCount];

        // Penalties for doubled pawns, indexed by the number of doubled pawns
        pawnStructureMgScore += config.getDoubledPawnPenalty()[0][doubledPawnCount];
        pawnStructureEgScore += config.getDoubledPawnPenalty()[1][doubledPawnCount];

        score.addPiecePlacementScore(piecePlacementMgScore, piecePlacementEgScore, isWhite);
        score.addPawnStructureScore(pawnStructureMgScore, pawnStructureEgScore, isWhite);
    }

    /**
     * Knight evaluation consists of simple piece-placement and mobility bonuses.
     */
    private void scoreKnights(long knights,
                              long friendlyBlockers,
                              long opponentPawnAttacks,
                              boolean isWhite) {
        int piecePlacementMgScore = 0;
        int piecePlacementEgScore = 0;
        int mobilityMgScore = 0;
        int mobilityEgScore = 0;

        int[] mgPieceSquareTable = config.getMiddlegameTables()[Piece.KNIGHT.getIndex()];
        int[] egPieceSquareTable = config.getEndgameTables()[Piece.KNIGHT.getIndex()];
        int[] mgMobilityBonuses = config.getMiddlegameMobilityBonus()[Piece.KNIGHT.getIndex()];
        int[] egMobilityBonuses = config.getEndgameMobilityBonus()[Piece.KNIGHT.getIndex()];

        while (knights != 0) {
            int knight = Bitwise.getNextBit(knights);

            int squareIndex = isWhite ? knight ^ 56 : knight;
            piecePlacementMgScore += mgPieceSquareTable[squareIndex];
            piecePlacementEgScore += egPieceSquareTable[squareIndex];

            long attacks = Attacks.knightAttacks(knight);
            long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            mobilityMgScore += mgMobilityBonuses[moveCount];
            mobilityEgScore += egMobilityBonuses[moveCount];

            knights = Bitwise.popBit(knights);
        }

        score.addPiecePlacementScore(piecePlacementMgScore, piecePlacementEgScore, isWhite);
        score.addMobilityScore(mobilityMgScore, mobilityEgScore, isWhite);

    }

    /**
     * Bishop evaluation consists of simple piece-placement and mobility bonuses.
     */
    private void scoreBishops(long bishops,
                              long friendlyBlockers,
                              long opponentBlockers,
                              long opponentPawnAttacks,
                              boolean isWhite) {
        int piecePlacementMgScore = 0;
        int piecePlacementEgScore = 0;
        int mobilityMgScore = 0;
        int mobilityEgScore = 0;

        int[] mgPieceSquareTable = config.getMiddlegameTables()[Piece.BISHOP.getIndex()];
        int[] egPieceSquareTable = config.getEndgameTables()[Piece.BISHOP.getIndex()];
        int[] mgMobilityBonuses = config.getMiddlegameMobilityBonus()[Piece.BISHOP.getIndex()];
        int[] egMobilityBonuses = config.getEndgameMobilityBonus()[Piece.BISHOP.getIndex()];

        long blockers = friendlyBlockers | opponentBlockers;
        while (bishops != 0) {
            int bishop = Bitwise.getNextBit(bishops);

            int squareIndex = isWhite ? bishop ^ 56 : bishop;
            piecePlacementMgScore += mgPieceSquareTable[squareIndex];
            piecePlacementEgScore += egPieceSquareTable[squareIndex];

            long attacks = Attacks.bishopAttacks(bishop, blockers);
            long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            mobilityMgScore += mgMobilityBonuses[moveCount];
            mobilityEgScore += egMobilityBonuses[moveCount];

            bishops = Bitwise.popBit(bishops);
        }

        score.addPiecePlacementScore(piecePlacementMgScore, piecePlacementEgScore, isWhite);
        score.addMobilityScore(mobilityMgScore, mobilityEgScore, isWhite);

    }

    /**
     * Rook evaluation consists of piece-placement and mobility bonuses, as well as bonuses for being on an open- or semi-open file.
     */
    private void scoreRooks(long rooks,
                            long friendlyPawns,
                            long opponentPawns,
                            long friendlyBlockers,
                            long opponentBlockers,
                            long opponentPawnAttacks,
                            boolean isWhite) {
        int piecePlacementMgScore = 0;
        int piecePlacementEgScore = 0;
        int mobilityMgScore = 0;
        int mobilityEgScore = 0;
        int rookMgScore = 0;
        int rookEgScore = 0;

        int[] mgPieceSquareTable = config.getMiddlegameTables()[Piece.ROOK.getIndex()];
        int[] egPieceSquareTable = config.getEndgameTables()[Piece.ROOK.getIndex()];
        int[] mgMobilityBonuses = config.getMiddlegameMobilityBonus()[Piece.ROOK.getIndex()];
        int[] egMobilityBonuses = config.getEndgameMobilityBonus()[Piece.ROOK.getIndex()];

        long blockers = friendlyBlockers | opponentBlockers;
        while (rooks != 0) {
            int rook = Bitwise.getNextBit(rooks);
            int file = BoardUtils.getFile(rook);
            long fileMask = Bits.FILE_MASKS[file];

            int squareIndex = isWhite ? rook ^ 56 : rook;
            piecePlacementMgScore += mgPieceSquareTable[squareIndex];
            piecePlacementEgScore += egPieceSquareTable[squareIndex];

            long attacks = Attacks.rookAttacks(rook, blockers);
            long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            mobilityMgScore += mgMobilityBonuses[moveCount];
            mobilityEgScore += egMobilityBonuses[moveCount];

            boolean hasFriendlyPawn = (fileMask & friendlyPawns) != 0;
            boolean hasOpponentPawn = (fileMask & opponentPawns) != 0;
            boolean hasOpenFile = !hasFriendlyPawn && !hasOpponentPawn;
            boolean hasSemiOpenFile = !hasFriendlyPawn && hasOpponentPawn;

            if (hasOpenFile) {
                rookMgScore += config.getRookOpenFileBonus()[0];
                rookEgScore += config.getRookOpenFileBonus()[1];
            }
            else if (hasSemiOpenFile) {
                rookMgScore += config.getRookSemiOpenFileBonus()[0];
                rookEgScore += config.getRookSemiOpenFileBonus()[1];
            }

            rooks = Bitwise.popBit(rooks);
        }

        score.addPiecePlacementScore(piecePlacementMgScore, piecePlacementEgScore, isWhite);
        score.addMobilityScore(mobilityMgScore, mobilityEgScore, isWhite);
        score.addRookScore(rookMgScore, rookEgScore, isWhite);
    }

    /**
     * Queen evaluation consists of simple piece-placement and mobility bonuses.
     */
    private void scoreQueens(long queens,
                             long friendlyBlockers,
                             long opponentBlockers,
                             long opponentPawnAttacks,
                             boolean isWhite) {
        int piecePlacementMgScore = 0;
        int piecePlacementEgScore = 0;
        int mobilityMgScore = 0;
        int mobilityEgScore = 0;

        int[] mgPieceSquareTable = config.getMiddlegameTables()[Piece.QUEEN.getIndex()];
        int[] egPieceSquareTable = config.getEndgameTables()[Piece.QUEEN.getIndex()];
        int[] mgMobilityBonuses = config.getMiddlegameMobilityBonus()[Piece.QUEEN.getIndex()];
        int[] egMobilityBonuses = config.getEndgameMobilityBonus()[Piece.QUEEN.getIndex()];

        long blockers = friendlyBlockers | opponentBlockers;
        while (queens != 0) {
            int queen = Bitwise.getNextBit(queens);

            int squareIndex = isWhite ? queen ^ 56 : queen;
            piecePlacementMgScore += mgPieceSquareTable[squareIndex];
            piecePlacementEgScore += egPieceSquareTable[squareIndex];

            long attacks = Attacks.bishopAttacks(queen, blockers) | Attacks.rookAttacks(queen, blockers);
            long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            mobilityMgScore += mgMobilityBonuses[moveCount];
            mobilityEgScore += egMobilityBonuses[moveCount];

            queens = Bitwise.popBit(queens);
        }

        score.addPiecePlacementScore(piecePlacementMgScore, piecePlacementEgScore, isWhite);
        score.addMobilityScore(mobilityMgScore, mobilityEgScore, isWhite);
    }

    /**
     * King evaluation consists of a piece-placement bonus, king safety considerations and mop-up bonus if up material.
     */
    private void scoreKing(long friendlyKing,
                           long opponentKing,
                           long friendlyPawns,
                           long opponentPawns,
                           Material friendlyMaterial,
                           Material opponentMaterial,
                           Board board,
                           float phase,
                           boolean isWhite) {

        int piecePlacementMgScore = 0;
        int piecePlacementEgScore = 0;
        int kingSafetyScore = 0;
        int mopUpScore = 0;

        int[] mgPieceSquareTable = config.getMiddlegameTables()[Piece.KING.getIndex()];
        int[] egPieceSquareTable = config.getEndgameTables()[Piece.KING.getIndex()];

        int friendlyKingSquare = Bitwise.getNextBit(friendlyKing);
        int friendlyKingFile = BoardUtils.getFile(friendlyKingSquare);

        int squareIndex = isWhite ? friendlyKingSquare ^ 56 : friendlyKingSquare;
        piecePlacementMgScore += mgPieceSquareTable[squareIndex];
        piecePlacementEgScore += egPieceSquareTable[squareIndex];

        // King safety evaluation
        if (phase > 0.5) {
            int pawnShieldPenalty = KingSafety.calculatePawnShieldPenalty(config, friendlyKingSquare, friendlyKingFile, friendlyPawns);
            int openKingFilePenalty = KingSafety.calculateOpenKingFilePenalty(config, friendlyKingFile, friendlyPawns, opponentPawns, opponentMaterial, isWhite);
            int lostCastlingRightsPenalty = KingSafety.calculateLostCastlingRightsPenalty(config, board, isWhite, friendlyKingFile);
            if (opponentMaterial.queens() == 0) {
                phase *= 0.6f;
            }
            kingSafetyScore = (int) -((pawnShieldPenalty + openKingFilePenalty + lostCastlingRightsPenalty) * phase);
        }

        // Mop-up evaluation
        int friendlyMaterialScore = friendlyMaterial.sum(Score.SIMPLE_PIECE_VALUES, config.getBishopPairBonus());
        int opponentMaterialScore = opponentMaterial.sum(Score.SIMPLE_PIECE_VALUES, config.getBishopPairBonus());
        boolean twoPawnAdvantage = friendlyMaterialScore > (opponentMaterialScore + 2 * Piece.PAWN.getValue());
        if (twoPawnAdvantage) {
            int opponentKingSquare = Bitwise.getNextBit(opponentKing);

            // Bonus for moving king closer to opponent king
            mopUpScore += (14 - Distance.manhattan(friendlyKingSquare, opponentKingSquare)) * config.getKingManhattanDistanceMultiplier();
            mopUpScore += (7 - Distance.chebyshev(friendlyKingSquare, opponentKingSquare)) * config.getKingChebyshevDistanceMultiplier();

            // Bonus for pushing opponent king to the edges of the board
            mopUpScore += Distance.centerManhattan(opponentKingSquare) * config.getKingCenterManhattanDistanceMultiplier();

            mopUpScore = (int) (mopUpScore * (1 - Phase.fromMaterial(opponentMaterial)));
        }
        score.addPiecePlacementScore(piecePlacementMgScore, piecePlacementEgScore, isWhite);
        score.setKingSafetyScore(kingSafetyScore, isWhite);
        score.setMopUpScore(mopUpScore, isWhite);

    }

    private static boolean isPassedPawn(int pawn, long opponentPawns, boolean isWhite) {
        long passedPawnMask = isWhite ? Bits.WHITE_PASSED_PAWN_MASK[pawn] : Bits.BLACK_PASSED_PAWN_MASK[pawn];
        return (passedPawnMask & opponentPawns) == 0;
    }

    private static boolean isIsolatedPawn(int file, long friendlyPawns) {
        return (Bits.ADJACENT_FILE_MASK[file] & friendlyPawns) == 0;
    }

    private static boolean isDoubledPawn(int file, long friendlyPawns) {
        long fileMask = Bits.FILE_MASKS[file];
        return Bitwise.countBits(friendlyPawns & fileMask) > 1;
    }

    private static int calculatePassedPawnBonus(int pawn, boolean isWhite, int[] passedPawnBonuses) {
        int rank = BoardUtils.getRank(pawn);
        int squaresFromPromotion = isWhite ? 7 - rank : rank;
        return passedPawnBonuses[squaresFromPromotion];
    }

    private static int calculateProtectedPawnBonus(EngineConfig config, int pawn, long friendlyPawns, boolean isWhite) {
        long protectionMask = isWhite ? Bits.WHITE_PROTECTED_PAWN_MASK[pawn] : Bits.BLACK_PROTECTED_PAWN_MASK[pawn];
        return Bitwise.countBits(protectionMask & friendlyPawns) * config.getProtectedPassedPawnBonus();
    }

}
