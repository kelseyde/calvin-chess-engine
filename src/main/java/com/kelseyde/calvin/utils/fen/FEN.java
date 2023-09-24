package com.kelseyde.calvin.utils.fen;

import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.ZobristKey;
import com.kelseyde.calvin.utils.BoardUtils;
import com.kelseyde.calvin.utils.NotationUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FEN {

    public static final String STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public static Board fromFEN(String fen) {

        try {

            String[] parts = fen.split(" ");

            String[] files = parts[0].split("/");

            long whitePawns = 0L;
            long whiteKnights = 0L;
            long whiteBishops = 0L;
            long whiteRooks = 0L;
            long whiteQueens = 0L;
            long whiteKing = 0L;
            long blackPawns = 0L;
            long blackKnights = 0L;
            long blackBishops = 0L;
            long blackRooks = 0L;
            long blackQueens = 0L;
            long blackKing = 0L;

            List<List<String>> rankFileHash = Arrays.stream(files)
                    .map(file -> Arrays.stream(file.split(""))
                            .flatMap(FEN::parseSquare)
                            .toList())
                    .collect(Collectors.toList());
            Collections.reverse(rankFileHash);

            for (int rankIndex = 0; rankIndex < rankFileHash.size(); rankIndex++) {
                List<String> rank = rankFileHash.get(rankIndex);
                for (int fileIndex = 0; fileIndex < rank.size(); fileIndex++) {
                    int squareIndex = BoardUtils.squareIndex(rankIndex, fileIndex);
                    String squareValue = rank.get(fileIndex);
                    long squareBB = 1L << squareIndex;
                    switch (squareValue) {
                        case "P" -> whitePawns |= squareBB;
                        case "N" -> whiteKnights |= squareBB;
                        case "B" -> whiteBishops |= squareBB;
                        case "R" -> whiteRooks |= squareBB;
                        case "Q" -> whiteQueens |= squareBB;
                        case "K" -> whiteKing |= squareBB;
                        case "p" -> blackPawns |= squareBB;
                        case "n" -> blackKnights |= squareBB;
                        case "b" -> blackBishops |= squareBB;
                        case "r" -> blackRooks |= squareBB;
                        case "q" -> blackQueens |= squareBB;
                        case "k" -> blackKing |= squareBB;
                    };
                }
            }

            boolean isWhiteToMove = parseSideToMove(parts[1]);
            int castlingRights = parseCastlingRights(parts[2]);
            int enPassantFile = parseEnPassantFile(parts[3]);
            int fiftyMoveCounter = parts.length > 4 ? parseFiftyMoveCounter(parts[4]) : 0;
            // This implementation does not require the full move counter (parts[5]).

            Board board = new Board();
            board.setWhitePawns(whitePawns);
            board.setWhiteKnights(whiteKnights);
            board.setWhiteBishops(whiteBishops);
            board.setWhiteRooks(whiteRooks);
            board.setWhiteQueens(whiteQueens);
            board.setWhiteKing(whiteKing);
            board.setBlackPawns(blackPawns);
            board.setBlackKnights(blackKnights);
            board.setBlackBishops(blackBishops);
            board.setBlackRooks(blackRooks);
            board.setBlackQueens(blackQueens);
            board.setBlackKing(blackKing);
            board.recalculatePieces();
            board.setWhiteToMove(isWhiteToMove);
            board.getCurrentGameState().setCastlingRights(castlingRights);
            board.getCurrentGameState().setEnPassantFile(enPassantFile);
            board.getCurrentGameState().setFiftyMoveCounter(fiftyMoveCounter);
            board.getCurrentGameState().setZobristKey(ZobristKey.generateKey(board));

            return board;

        } catch (Exception e) {
            throw new InvalidFENException(fen, e);
        }


    }

    public static String toFEN(Board board) {
        // TODO implement fen parsing
        return null;
    }

    private static boolean parseSideToMove(String sideToMove) {
        return switch (sideToMove) {
            case "w" -> true;
            case "b" -> false;
            default -> throw new IllegalArgumentException("Invalid side to move! " + sideToMove);
        };
    }

    private static int parseCastlingRights(String castlingRights) {
        int castlingRightsMask = 0b0000;
        if (castlingRights.contains("K")) {
            castlingRightsMask |= 0b0001;
        }
        if (castlingRights.contains("Q")) {
            castlingRightsMask |= 0b0010;
        }
        if (castlingRights.contains("k")) {
            castlingRightsMask |= 0b0100;
        }
        if (castlingRights.contains("q")) {
            castlingRightsMask |= 0b1000;
        }
        return castlingRightsMask;
    }

    private static int parseEnPassantFile(String enPassantFile) {
        if (enPassantFile.equals("-")) {
            return -1;
        }
        int square = NotationUtils.fromNotation(enPassantFile);
        return BoardUtils.getFile(square);
    }

    private static int parseFiftyMoveCounter(String fiftyMoveCounter) {
        return Character.isDigit(fiftyMoveCounter.charAt(0)) ? Integer.parseInt(fiftyMoveCounter) : 0;
    }

    private static Stream<String> parseSquare(String square) {
        if (square.length() != 1) {
            throw new IllegalArgumentException("Illegal square char! " + square);
        }
        boolean isLetter = Character.isLetter(square.charAt(0));
        return isLetter ? Stream.of(square) : IntStream.range(0, Integer.parseInt(square)).mapToObj(i -> "x");
    }


}
