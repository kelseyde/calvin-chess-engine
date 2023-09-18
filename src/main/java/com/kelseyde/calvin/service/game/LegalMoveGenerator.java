package com.kelseyde.calvin.service.game;

import com.kelseyde.calvin.model.BitBoard;
import com.kelseyde.calvin.model.BitBoards;
import com.kelseyde.calvin.model.Board;
import com.kelseyde.calvin.model.Colour;
import com.kelseyde.calvin.model.move.Move;
import com.kelseyde.calvin.service.game.generator.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Evaluates the effect of a {@link Move} on a game. First checks if the move is legal. Then, checks if executing the
 * move results in the game ending, either due to checkmate or one of the draw conditions.
 */
@Slf4j
public class LegalMoveGenerator {

    private static final Set<PseudoLegalMoveGenerator> PSEUDO_LEGAL_MOVE_GENERATORS = Set.of(
        new PawnMoveGenerator(),
        new KnightMoveGenerator(),
        new BishopMoveGenerator(),
        new RookMoveGenerator(),
        new QueenMoveGenerator(),
        new KingMoveGenerator()
    );

    public Set<Move> generateLegalMoves(Board board) {
        return generatePseudoLegalMoves(board).stream()
                .filter(pseudoLegalMove -> isFullyLegal(board, pseudoLegalMove))
                .collect(Collectors.toSet());
    }

    private Set<Move> generatePseudoLegalMoves(Board board) {
        Set<Move> pseudoLegalMoves = PSEUDO_LEGAL_MOVE_GENERATORS.stream()
                .flatMap(generator -> generator.generatePseudoLegalMoves(board).stream())
                .collect(Collectors.toSet());
        if (board.getTurn().isWhite()) {

        }
        return pseudoLegalMoves;
    }

    private boolean isFullyLegal(Board board, Move move) {
        Colour colour = board.getTurn();

        Board boardCopy = board.copy();
        boardCopy.applyMove(move);

        long kingMask = switch (move.getMoveType()) {
            default -> colour.isWhite() ? boardCopy.getWhiteKing() : boardCopy.getBlackKing();
            case KINGSIDE_CASTLE -> colour.isWhite() ? BitBoards.WHITE_KINGSIDE_CASTLE_SAFE_MASK : BitBoards.BLACK_KINGSIDE_CASTLE_SAFE_MASK;
            case QUEENSIDE_CASTLE -> colour.isWhite() ? BitBoards.WHITE_QUEENSIDE_CASTLE_SAFE_MASK : BitBoards.BLACK_QUEENSIDE_CASTLE_SAFE_MASK;
        };

        // TODO perhaps can be done without a stream
        long attackedSquares = 0L;
        for (Move opponentMove : generatePseudoLegalMoves(boardCopy)) {
            attackedSquares |= (1L << opponentMove.getEndSquare());
        }
        boolean isLegalMove = (kingMask & attackedSquares) == 0;
        if (isLegalMove && isCheck(boardCopy)) {
            move.setCheck(true);
        }
        return isLegalMove;

    }

    private boolean isCheck(Board board) {
        // Additionally calculate whether the move is a check, store this info in Move.
        board.setTurn(board.getTurn().oppositeColour());
        int opponentKingSquare = board.getTurn().isWhite() ? BitBoard.scanForward(board.getBlackKing()) : BitBoard.scanForward(board.getWhiteKing());
        Set<Integer> attackingSquares = generatePseudoLegalMoves(board).stream()
                .map(Move::getEndSquare)
                .collect(Collectors.toSet());
        return attackingSquares.contains(opponentKingSquare);
    }

}
