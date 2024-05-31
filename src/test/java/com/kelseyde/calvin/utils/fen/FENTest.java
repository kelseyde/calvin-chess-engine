package com.kelseyde.calvin.utils.fen;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.utils.notation.FEN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Disabled
public class FENTest {

    @Test
    public void testStartingPosition() {

        Board fenBoard = FEN.toBoard(FEN.STARTING_POSITION);
        Board newBoard = new Board();
        Assertions.assertEquals(newBoard.getWhitePawns(), fenBoard.getWhitePawns());
        Assertions.assertEquals(newBoard.getWhiteKnights(), fenBoard.getWhiteKnights());
        Assertions.assertEquals(newBoard.getWhiteBishops(), fenBoard.getWhiteBishops());
        Assertions.assertEquals(newBoard.getWhiteRooks(), fenBoard.getWhiteRooks());
        Assertions.assertEquals(newBoard.getWhiteQueens(), fenBoard.getWhiteQueens());
        Assertions.assertEquals(newBoard.getWhiteKing(), fenBoard.getWhiteKing());
        Assertions.assertEquals(newBoard.getBlackPawns(), fenBoard.getBlackPawns());
        Assertions.assertEquals(newBoard.getBlackKnights(), fenBoard.getBlackKnights());
        Assertions.assertEquals(newBoard.getBlackBishops(), fenBoard.getBlackBishops());
        Assertions.assertEquals(newBoard.getBlackRooks(), fenBoard.getBlackRooks());
        Assertions.assertEquals(newBoard.getBlackQueens(), fenBoard.getBlackQueens());
        Assertions.assertEquals(newBoard.getBlackKing(), fenBoard.getBlackKing());

        Assertions.assertEquals(newBoard.getWhitePieces(), fenBoard.getWhitePieces());
        Assertions.assertEquals(newBoard.getBlackPieces(), fenBoard.getBlackPieces());
        Assertions.assertEquals(newBoard.getOccupied(), fenBoard.getOccupied());

        Assertions.assertEquals(newBoard.isWhite(), fenBoard.isWhite());
        Assertions.assertEquals(newBoard.getGameState(), fenBoard.getGameState());
        Assertions.assertEquals(new ArrayList<>(newBoard.getGameStateHistory()), new ArrayList<>(fenBoard.getGameStateHistory()));
        Assertions.assertEquals(new ArrayList<>(newBoard.getMoveHistory()), new ArrayList<>(fenBoard.getMoveHistory()));

    }

    @Test
    public void testAllFens() throws IOException {

        List<String> fens = loadFens();
        for (String originalFen : fens) {
            String actualFen = originalFen.split("\"")[0];
            Board board = FEN.toBoard(actualFen);
            String newFen = FEN.toFEN(board);
            Assertions.assertEquals(actualFen, newFen);
        }

    }

    private List<String> loadFens() throws IOException {
        String fileName = "src/test/resources/texel/quiet_positions.epd";
        Path path = Paths.get(fileName);
        return Files.readAllLines(path);
    }

}