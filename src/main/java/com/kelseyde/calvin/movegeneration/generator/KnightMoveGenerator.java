package com.kelseyde.calvin.movegeneration.generator;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.bitboard.BitBoardUtils;
import com.kelseyde.calvin.board.move.Move;
import com.kelseyde.calvin.board.piece.PieceType;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class KnightMoveGenerator implements PseudoLegalMoveGenerator {

    @Getter
    private final PieceType pieceType = PieceType.KNIGHT;

    public static final long[] KNIGHT_ATTACKS = new long[] {
            0x0000000000020400L, 0x0000000000050800L, 0x00000000000a1100L, 0x0000000000142200L,
            0x0000000000284400L, 0x0000000000508800L, 0x0000000000a01000L, 0x0000000000402000L,
            0x0000000002040004L, 0x0000000005080008L, 0x000000000a110011L, 0x0000000014220022L,
            0x0000000028440044L, 0x0000000050880088L, 0x00000000a0100010L, 0x0000000040200020L,
            0x0000000204000402L, 0x0000000508000805L, 0x0000000a1100110aL, 0x0000001422002214L,
            0x0000002844004428L, 0x0000005088008850L, 0x000000a0100010a0L, 0x0000004020002040L,
            0x0000020400040200L, 0x0000050800080500L, 0x00000a1100110a00L, 0x0000142200221400L,
            0x0000284400442800L, 0x0000508800885000L, 0x0000a0100010a000L, 0x0000402000204000L,
            0x0002040004020000L, 0x0005080008050000L, 0x000a1100110a0000L, 0x0014220022140000L,
            0x0028440044280000L, 0x0050880088500000L, 0x00a0100010a00000L, 0x0040200020400000L,
            0x0204000402000000L, 0x0508000805000000L, 0x0a1100110a000000L, 0x1422002214000000L,
            0x2844004428000000L, 0x5088008850000000L, 0xa0100010a0000000L, 0x4020002040000000L,
            0x0400040200000000L, 0x0800080500000000L, 0x1100110a00000000L, 0x2200221400000000L,
            0x4400442800000000L, 0x8800885000000000L, 0x100010a000000000L, 0x2000204000000000L,
            0x0004020000000000L, 0x0008050000000000L, 0x00110a0000000000L, 0x0022140000000000L,
            0x0044280000000000L, 0x0088500000000000L, 0x0010a00000000000L, 0x0020400000000000L
    };

    public Set<Move> generatePseudoLegalMoves(Board board) {

        Set<Move> moves = new HashSet<>();

        long knights = board.isWhiteToMove() ? board.getWhiteKnights() : board.getBlackKnights();
        long friendlyPieces = board.isWhiteToMove() ? board.getWhitePieces() : board.getBlackPieces();

        while (knights != 0) {
            int startSquare = BitBoardUtils.scanForward(knights);
            long possibleMoves = KNIGHT_ATTACKS[startSquare] &~ friendlyPieces;
            moves.addAll(addKnightMoves(startSquare, possibleMoves));
            knights = BitBoardUtils.popLSB(knights);
        }
        return moves;

    }

    private Set<Move> addKnightMoves(int startSquare, long possibleMoves) {
        Set<Move> moves = new HashSet<>();
        while (possibleMoves != 0) {
            int endSquare = BitBoardUtils.scanForward(possibleMoves);
            moves.add(Move.builder()
                    .pieceType(PieceType.KNIGHT)
                    .startSquare(startSquare)
                    .endSquare(endSquare)
                    .build());
            possibleMoves = BitBoardUtils.popLSB(possibleMoves);
        }
        return moves;
    }

}
