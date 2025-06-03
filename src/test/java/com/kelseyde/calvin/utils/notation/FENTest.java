package com.kelseyde.calvin.utils.notation;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.utils.notation.FEN.InvalidFenException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FENTest {

    @Test
    public void testNull() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse(null));
    }

    @Test
    public void testEmpty() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse(""));
    }

    @Test
    public void testRandomString() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("random string"));
    }

    @Test
    public void testTooFewParts() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w"));
    }

    @Test
    public void testTooManyParts() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 extra"));
    }

    @Test
    public void testBoardHasTooFewRanks() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP w KQkq - 0 1"));
    }

    @Test
    public void testBoardHasTooManyRanks() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/rnbqkbnr w KQkq - 0 1"));
    }

    @Test
    public void testBoardHasNoKings() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqbbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQNBNR w KQkq - 0 1"));
    }

    @Test
    public void testBoardHasOneKing() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbkr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    public void testBoardHasTooManyKings() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/ppppkppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    public void testBoardHasTwoWhiteKings() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqKbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    public void testBoardHasTwoBlackKings() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQkBNR w KQkq - 0 1"));
    }

    @Test
    public void testBoardWithInvalidCharacter() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPP*PPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    public void testRankDoesNotAddUpToEight() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/ppp2ppp/8/8/4p/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    public void testConsecutiveNumbersInRank() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/44/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    public void testNumberExceedsEight() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/9/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    public void testEmptyRank() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp//8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    public void testRankWithTooManyPieces() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnrr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    public void testRankWithTooFewPieces() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbn/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    public void testInvalidTurn() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR white KQkq - 0 1"));
    }

    @Test
    public void testInvalidCastlingRights() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkqX - 0 1"));
    }

    @Test
    public void testInvalidEnPassantRandomString() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq randomString 0 1"));
    }

    @Test
    public void testInvalidEnPassantIllegalSquare() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq i9 0 1"));
    }

    @Test
    public void testInvalidEnPassantIllegalEnPassantSquare() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e4 0 1"));
    }

    @Test
    public void testInvalidHalfMove() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - randomString 1"));
    }

    @Test
    public void testNegativeHalfMove() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - -1 1"));
    }

    @Test
    public void testInvalidFullMove() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 randomString"));
    }

    @Test
    public void testNegativeFullMove() {
        assertThrows(InvalidFenException.class,
                () -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 -1"));
    }

    @Test
    public void testValid() {
        assertDoesNotThrow(() -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    @Test
    public void testValidShredderFen() {
        assertDoesNotThrow(() -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w HAha - 0 1"));
    }

    @Test
    public void testValidWithEnPassant() {
        assertDoesNotThrow(() -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e3 0 1"));
    }

    @Test
    public void testValidMissingFullMove() {
        assertDoesNotThrow(() -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0"));
    }

    @Test
    public void testValidMissingHalfMove() {
        assertDoesNotThrow(() -> FEN.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0"));
    }

    @Test
    public void testFullSuite() throws IOException {

        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/perft_suite.epd"));
        lines.forEach(line -> {
            String[] parts = line.split(";");
            String fen1 = parts[0].trim();
            Board board = FEN.parse(fen1).toBoard();
            assertNotNull(board);
            String fen2 = FEN.fromBoard(board).toString();
            assertEquals(fen1, fen2, "FEN strings do not match!");
        });

    }

}