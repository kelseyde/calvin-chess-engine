package com.kelseyde.calvin.tables.history;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Colour;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

/**
 * The {@link CounterMoveTable} is similar to the {@link KillerTable} in that is stores moves that caused a cut-off.
 * The difference is that, whereas the killer table indexes moves based on the ply at which they were played, the
 * counter move table indexes moves based on the previous move played.
 * </b>
 * The idea is that if the move was a good response to the previous move in one position, it is likely to be a good
 * response in other positions too. Therefore, we give that move a higher priority in the move ordering.
 *
 * @see <a href="https://www.chessprogramming.org/Countermove_Heuristic">Chess Programming Wiki</a>
 */
public class CounterMoveTable {

    private Move[][][] table = new Move[2][Piece.COUNT][Square.COUNT];
    
    public boolean isCounterMove(Piece prevPiece, Move prevMove, boolean white, Move move) {
        if (prevPiece == null || prevMove == null) return false;
        int colourIndex = Colour.index(white);
        int pieceIndex = prevPiece.index();
        Move counterMove = table[colourIndex][pieceIndex][prevMove.to()];
        return counterMove != null && counterMove.equals(move);
    }

    public void add(Piece prevPiece, Move prevMove, boolean white, Move move) {
        if (prevPiece == null || prevMove == null) return;
        int pieceIndex = prevPiece.index();
        int colourIndex = Colour.index(white);
        table[colourIndex][pieceIndex][prevMove.to()] = move;
    }

    public Move get(Piece piece, int to, boolean white) {
        int pieceIndex = piece.index() + (white ? 0 : Piece.COUNT);
        int colourIndex = Colour.index(white);
        return table[colourIndex][pieceIndex][to];
    }

    public void clear() {
        table = new Move[2][Piece.COUNT][Square.COUNT];
    }

}
