package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Bitwise;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.engine.EngineConfig;
import com.kelseyde.calvin.generation.Attacks;
import com.kelseyde.calvin.transposition.pawn.PawnHashEntry;
import com.kelseyde.calvin.transposition.pawn.PawnHashEntry.PawnScore;
import com.kelseyde.calvin.transposition.pawn.PawnHashTable;
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
    final PawnHashTable pawnHash;

    final int[] pawnMgTable;
    final int[] pawnEgTable;
    final int[] knightMgTable;
    final int[] knightEgTable;
    final int[] bishopMgTable;
    final int[] bishopEgTable;
    final int[] rookMgTable;
    final int[] rookEgTable;
    final int[] queenMgTable;
    final int[] queenEgTable;
    final int[] kingMgTable;
    final int[] kingEgTable;

    final int[] knightMgMobility;
    final int[] knightEgMobility;
    final int[] bishopMgMobility;
    final int[] bishopEgMobility;
    final int[] rookMgMobility;
    final int[] rookEgMobility;
    final int[] queenMgMobility;
    final int[] queenEgMobility;

    int whiteMaterialMgScore;
    int blackMaterialMgScore;

    int whiteMaterialEgScore;
    int blackMaterialEgScore;

    Score score;

    public Evaluator(EngineConfig config) {
        this.config = config;
        this.pawnHash = new PawnHashTable();

        pawnMgTable = config.getMiddlegameTables()[Piece.PAWN.getIndex()];
        pawnEgTable = config.getEndgameTables()[Piece.PAWN.getIndex()];
        knightMgTable = config.getMiddlegameTables()[Piece.KNIGHT.getIndex()];
        knightEgTable = config.getEndgameTables()[Piece.KNIGHT.getIndex()];
        bishopMgTable = config.getMiddlegameTables()[Piece.BISHOP.getIndex()];
        bishopEgTable = config.getEndgameTables()[Piece.BISHOP.getIndex()];
        rookMgTable = config.getMiddlegameTables()[Piece.ROOK.getIndex()];
        rookEgTable = config.getEndgameTables()[Piece.ROOK.getIndex()];
        queenMgTable = config.getMiddlegameTables()[Piece.QUEEN.getIndex()];
        queenEgTable = config.getEndgameTables()[Piece.QUEEN.getIndex()];
        kingMgTable = config.getMiddlegameTables()[Piece.KING.getIndex()];
        kingEgTable = config.getEndgameTables()[Piece.KING.getIndex()];

        knightMgMobility = config.getMiddlegameMobilityBonus()[Piece.KNIGHT.getIndex()];
        knightEgMobility = config.getEndgameMobilityBonus()[Piece.KNIGHT.getIndex()];
        bishopMgMobility = config.getMiddlegameMobilityBonus()[Piece.BISHOP.getIndex()];
        bishopEgMobility = config.getEndgameMobilityBonus()[Piece.BISHOP.getIndex()];
        rookMgMobility = config.getMiddlegameMobilityBonus()[Piece.ROOK.getIndex()];
        rookEgMobility = config.getEndgameMobilityBonus()[Piece.ROOK.getIndex()];
        queenMgMobility = config.getMiddlegameMobilityBonus()[Piece.QUEEN.getIndex()];
        queenEgMobility = config.getEndgameMobilityBonus()[Piece.QUEEN.getIndex()];
    }

    @Override
    public int evaluate(Board board) {

        score = new Score();
        boolean white = board.isWhiteToMove();

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

        float phase = Phase.fromMaterial(whiteMaterial, blackMaterial, config);
        score.setPhase(phase);

        whiteMaterialMgScore = whiteMaterial.sum(config.getPieceValues()[0], config.getBishopPairBonus());
        whiteMaterialEgScore = whiteMaterial.sum(config.getPieceValues()[1], config.getBishopPairBonus());
        score.addScore(whiteMaterialMgScore, whiteMaterialEgScore, true);

        blackMaterialMgScore = blackMaterial.sum(config.getPieceValues()[0], config.getBishopPairBonus());
        blackMaterialEgScore = blackMaterial.sum(config.getPieceValues()[1], config.getBishopPairBonus());;
        score.addScore(blackMaterialMgScore, blackMaterialEgScore, false);

        // Blockers used during mobility calculations
        long friendlyWhiteBlockers = whiteKing | whitePawns;
        long friendlyBlackBlockers = blackKing | blackPawns;

        // Pawn attacks used during mobility calculations
        long whitePawnAttacks = Attacks.pawnAttacks(whitePawns, true);
        long blackPawnAttacks = Attacks.pawnAttacks(blackPawns, false);

        scorePawnsWithHash(board, whitePawns, blackPawns);

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

        score.setTempoBonus(config.getTempoBonus(), board.isWhiteToMove());

        return score.sum(white);

    }

    private void scorePawnsWithHash(Board board, long whitePawns, long blackPawns) {

        long pawnKey = board.getGameState().getPawnKey();
        PawnHashEntry hashEntry = pawnHash.get(pawnKey);
        PawnScore whiteScore;
        PawnScore blackScore;
        if (hashEntry != null) {
            whiteScore = hashEntry.whiteScore();
            blackScore = hashEntry.blackScore();
        } else {
            whiteScore = scorePawns(whitePawns, blackPawns, true);
            blackScore = scorePawns(blackPawns, whitePawns, false);
            hashEntry = new PawnHashEntry(pawnKey, whiteScore, blackScore);
            pawnHash.put(pawnKey, hashEntry);
        }
        score.addScore(whiteScore.mgScore(), whiteScore.egScore(), true);
        score.addScore(blackScore.mgScore(), blackScore.egScore(), false);

    }

    /**
     * Pawn evaluation consists of piece-placement eval + pawn structure considerations (bonuses for passed pawns,
     * penalties for isolated/doubled pawns).
     */
    private PawnScore scorePawns(long friendlyPawns,
                                 long opponentPawns,
                                 boolean white) {
        
        if (friendlyPawns == 0) return new PawnScore(0, 0);
        int mgScore = 0;
        int egScore = 0;

        int isolatedPawnCount = 0;
        int doubledPawnCount = 0;

        long pawnsIterator = friendlyPawns;
        while (pawnsIterator > 0) {
            int pawn = Bitwise.getNextBit(pawnsIterator);
            int file = BoardUtils.getFile(pawn);

            int square = white ? pawn ^ 56 : pawn;
            mgScore += pawnMgTable[square];
            egScore += pawnEgTable[square];

            // Bonuses for a passed pawn, indexed by the number of squares away that pawn is from promotion.
            // Bonus for a passed pawn that is additionally protected by another pawn (multiplied by number of defending pawns).
            if (Bitwise.isPassedPawn(pawn, opponentPawns, white)) {
                int rank = BoardUtils.getRank(pawn);
                int squaresFromPromotion = white ? 7 - rank : rank;
                mgScore += config.getPassedPawnBonus()[0][squaresFromPromotion];
                egScore += config.getPassedPawnBonus()[1][squaresFromPromotion];

                int numberOfProtectors = Bitwise.countPawnProtectors(pawn, friendlyPawns, white);
                int protectedPawnBonus = numberOfProtectors * config.getProtectedPassedPawnBonus();
                mgScore += protectedPawnBonus;
                egScore += protectedPawnBonus;
            }
            // Passed pawns are not penalised for being isolated
            else if (Bitwise.isIsolatedPawn(file, friendlyPawns)) {
                isolatedPawnCount++;
            }
            if (Bitwise.isDoubledPawn(file, friendlyPawns)) {
                doubledPawnCount++;
            }

            pawnsIterator = Bitwise.popBit(pawnsIterator);
        }

        // Penalties for isolated pawns, indexed by the number of isolated pawns.
        mgScore += config.getIsolatedPawnPenalty()[0][isolatedPawnCount];
        egScore += config.getIsolatedPawnPenalty()[1][isolatedPawnCount];

        // Penalties for doubled pawns, indexed by the number of doubled pawns
        mgScore += config.getDoubledPawnPenalty()[0][doubledPawnCount];
        egScore += config.getDoubledPawnPenalty()[1][doubledPawnCount];

        return new PawnScore(mgScore, egScore);
    }

    /**
     * Knight evaluation consists of simple piece-placement and mobility bonuses.
     */
    private void scoreKnights(long knights,
                              long friendlyBlockers,
                              long opponentPawnAttacks,
                              boolean white) {

        if (knights == 0) return;
        int mgScore = 0;
        int egScore = 0;

        while (knights != 0) {
            int knight = Bitwise.getNextBit(knights);

            int square = white ? knight ^ 56 : knight;
            mgScore += knightMgTable[square];
            egScore += knightEgTable[square];

            long attacks = Attacks.knightAttacks(knight);
            long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            mgScore += knightMgMobility[moveCount];
            egScore += knightEgMobility[moveCount];

            knights = Bitwise.popBit(knights);
        }

        score.addScore(mgScore, egScore, white);

    }

    /**
     * Bishop evaluation consists of simple piece-placement and mobility bonuses.
     */
    private void scoreBishops(long bishops,
                              long friendlyBlockers,
                              long opponentBlockers,
                              long opponentPawnAttacks,
                              boolean white) {

        if (bishops == 0) return;
        int mgScore = 0;
        int egScore = 0;

        long blockers = friendlyBlockers | opponentBlockers;
        while (bishops != 0) {
            int bishop = Bitwise.getNextBit(bishops);

            int square = white ? bishop ^ 56 : bishop;
            mgScore += bishopMgTable[square];
            egScore += bishopEgTable[square];

            long attacks = Attacks.bishopAttacks(bishop, blockers);
            long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            mgScore += bishopMgMobility[moveCount];
            egScore += bishopEgMobility[moveCount];

            bishops = Bitwise.popBit(bishops);
        }

        score.addScore(mgScore, egScore, white);

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
                            boolean white) {

        if (rooks == 0) return;
        int mgScore = 0;
        int egScore = 0;

        long blockers = friendlyBlockers | opponentBlockers;
        while (rooks != 0) {
            int rook = Bitwise.getNextBit(rooks);
            int file = BoardUtils.getFile(rook);
            long fileMask = Bits.FILE_MASKS[file];

            int square = white ? rook ^ 56 : rook;
            mgScore += rookMgTable[square];
            egScore += rookEgTable[square];

            long attacks = Attacks.rookAttacks(rook, blockers);
            long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            mgScore += rookMgMobility[moveCount];
            egScore += rookEgMobility[moveCount];

            boolean hasFriendlyPawn = (fileMask & friendlyPawns) != 0;
            boolean hasOpponentPawn = (fileMask & opponentPawns) != 0;
            boolean hasOpenFile = !hasFriendlyPawn && !hasOpponentPawn;
            boolean hasSemiOpenFile = !hasFriendlyPawn && hasOpponentPawn;

            if (hasOpenFile) {
                mgScore += config.getRookOpenFileBonus()[0];
                egScore += config.getRookOpenFileBonus()[1];
            }
            else if (hasSemiOpenFile) {
                mgScore += config.getRookSemiOpenFileBonus()[0];
                egScore += config.getRookSemiOpenFileBonus()[1];
            }

            rooks = Bitwise.popBit(rooks);
        }

        score.addScore(mgScore, egScore, white);
    }

    /**
     * Queen evaluation consists of simple piece-placement and mobility bonuses.
     */
    private void scoreQueens(long queens,
                             long friendlyBlockers,
                             long opponentBlockers,
                             long opponentPawnAttacks,
                             boolean white) {

        if (queens == 0) return;
        int mgScore = 0;
        int egScore = 0;

        long blockers = friendlyBlockers | opponentBlockers;
        while (queens != 0) {
            int queen = Bitwise.getNextBit(queens);

            int square = white ? queen ^ 56 : queen;
            mgScore += queenMgTable[square];
            egScore += queenEgTable[square];

            long attacks = Attacks.bishopAttacks(queen, blockers) | Attacks.rookAttacks(queen, blockers);
            long moves = attacks &~ friendlyBlockers &~ opponentPawnAttacks;
            int moveCount = Bitwise.countBits(moves);
            mgScore += queenMgMobility[moveCount];
            egScore += queenEgMobility[moveCount];

            queens = Bitwise.popBit(queens);
        }

        score.addScore(mgScore, egScore, white);
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
                           boolean white) {
        int mgScore = 0;
        int egScore = 0;

        int king = Bitwise.getNextBit(friendlyKing);
        int square = white ? king ^ 56 : king;
        mgScore += kingMgTable[square];
        egScore += kingEgTable[square];
        score.addScore(mgScore, egScore, white);

        scoreKingSafety(king, friendlyPawns, opponentPawns, opponentMaterial, board, phase, white);
        scoreMopUp(king, opponentKing, friendlyMaterial, white);

    }

    /**
     * The king is penalised for being uncastled; for being castled but with the pawns infront of the king (the 'pawn shield')
     * pushed too far up the board; or for having open or semi-open files either infront of or adjacent to the king. This
     * evaluation matters less in the endgame, when there are fewer pieces on the board, and the king becomes a more active piece.
     * </p>
     * @see <a href="https://www.chessprogramming.org/King_Safety">Chess Programming Wiki</a>
     */
    private void scoreKingSafety(int kingSquare,
                                   long friendlyPawns,
                                   long opponentPawns,
                                   Material opponentMaterial,
                                   Board board,
                                   float phase,
                                   boolean white) {
        // King safety evaluation
        int kingFile = BoardUtils.getFile(kingSquare);
        int pawnShieldPenalty = calculatePawnShieldPenalty(kingSquare, kingFile, friendlyPawns);
        int openKingFilePenalty = calculateOpenKingFilePenalty(kingFile, friendlyPawns, opponentPawns, opponentMaterial);
        int lostCastlingRightsPenalty = calculateLostCastlingRightsPenalty(config, board, white, kingFile);
        if (opponentMaterial.queens() == 0) {
            phase *= 0.4f;
        }
        float kingSafetyScore = (int) -((pawnShieldPenalty + openKingFilePenalty + lostCastlingRightsPenalty) * phase);
        int mgScore = (int) ((kingSafetyScore / 100) * config.getKingSafetyScaleFactor()[0]);
        int egScore = (int) ((kingSafetyScore / 100) * config.getKingSafetyScaleFactor()[1]);
        score.addScore(mgScore, egScore, white);
    }

    /**
     * When the side-to-move is up a decisive amount of material, give a small bonus for escorting the opponent king to
     * the sides or corners of the board. This assists the engine in finding forced mate.
     * </p>
     * @see <a href="https://www.chessprogramming.org/Mop-up_Evaluation">Chess Programming Wiki</a>
     */
    private void scoreMopUp(int friendlyKingSquare,
                              long opponentKing,
                              Material opponentMaterial,
                              boolean white) {
        int mopUpScore = 0;
        int friendlyMaterialScore = white ? whiteMaterialMgScore : blackMaterialMgScore;
        int opponentMaterialScore = white ? blackMaterialMgScore: whiteMaterialMgScore;
        boolean twoPawnAdvantage = friendlyMaterialScore > (opponentMaterialScore + 2 * Piece.PAWN.getValue());
        if (!twoPawnAdvantage) return;
        int opponentKingSquare = Bitwise.getNextBit(opponentKing);

        // Bonus for moving king closer to opponent king
        mopUpScore += (14 - Distance.manhattan(friendlyKingSquare, opponentKingSquare)) * config.getKingManhattanDistanceMultiplier();
        mopUpScore += (7 - Distance.chebyshev(friendlyKingSquare, opponentKingSquare)) * config.getKingChebyshevDistanceMultiplier();

        // Bonus for pushing opponent king to the edges of the board
        mopUpScore += Distance.centerManhattan(opponentKingSquare) * config.getKingCenterManhattanDistanceMultiplier();

        float opponentPhase = Phase.fromMaterial(opponentMaterial, config);
        float phasedScore = mopUpScore * (1 - opponentPhase);

        int mgScore = (int) ((phasedScore / 100) * config.getMopUpScaleFactor()[0]);
        int egScore = (int) ((phasedScore / 100) * config.getMopUpScaleFactor()[1]);
        score.addScore(mgScore, egScore, white);

    }

    private int calculatePawnShieldPenalty(int kingSquare, int kingFile, long pawns) {
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

    private int calculateOpenKingFilePenalty(int kingFile, long friendlyPawns, long opponentPawns, Material opponentMaterial) {
        if (opponentMaterial.rooks() == 0 && opponentMaterial.queens() == 0) {
            return 0;
        }
        return scoreFile(Bits.FILE_MASKS[kingFile], friendlyPawns, opponentPawns, true)
            + scoreFile(Bits.WEST_FILE_MASK[kingFile], friendlyPawns, opponentPawns, false)
            + scoreFile(Bits.EAST_FILE_MASK[kingFile], friendlyPawns, opponentPawns, false);
    }

    private int scoreFile(long fileMask, long friendlyPawns, long opponentPawns, boolean isKingFile) {
        if (fileMask == 0) {
            return 0;
        }
        int penalty = 0;
        boolean isFriendlyPawnMissing = (friendlyPawns & fileMask) == 0;
        boolean isOpponentPawnMissing = (opponentPawns & fileMask) == 0;
        if (isFriendlyPawnMissing || isOpponentPawnMissing) {
            // Add penalty for semi-open file around the king
            penalty += isKingFile ? config.getKingSemiOpenFilePenalty() : config.getKingSemiOpenAdjacentFilePenalty();
        }
        if (isFriendlyPawnMissing && isOpponentPawnMissing) {
            // Add penalty for fully open file around king
            penalty += isKingFile ? config.getKingOpenFilePenalty() : config.getKingOpenAdjacentFilePenalty();
        }
        return penalty;
    }

    private int calculateLostCastlingRightsPenalty(EngineConfig config, Board board, boolean white, int kingFile) {
        boolean isCastled = kingFile <= 2 || kingFile >= 5;
        if (isCastled) return 0;
        boolean hasCastlingRights = board.getGameState().hasCastlingRights(white);
        boolean opponentHasCastlingRights = board.getGameState().hasCastlingRights(!white);
        return !hasCastlingRights && opponentHasCastlingRights ? config.getKingLostCastlingRightsPenalty() : 0;
    }

    @Override
    public Score getScore() {
        return score;
    }

}
