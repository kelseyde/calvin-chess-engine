package com.kelseyde.calvin.utils.notation;

import com.kelseyde.calvin.board.*;
import com.kelseyde.calvin.board.Bits.File;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.uci.UCI;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FEN {

    public static final String STARTPOS = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public static Board toBoard(String fen) {

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
                    int square = Square.of(rankIndex, fileIndex);
                    String squareValue = rank.get(fileIndex);
                    long squareBB = Bits.of(square);
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
                    }
                }
            }

            long pawns = whitePawns | blackPawns;
            long knight = whiteKnights | blackKnights;
            long bishops = whiteBishops | blackBishops;
            long rooks = whiteRooks | blackRooks;
            long queens = whiteQueens | blackQueens;
            long king = whiteKing | blackKing;
            long whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
            long blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;

            boolean whiteToMove = parseSideToMove(parts[1]);
            int castlingRights = parseCastlingRights(parts[2], whiteRooks, blackRooks, Bits.next(whiteKing), Bits.next(blackKing));
            int enPassantFile = parseEnPassantFile(parts[3]);
            int fiftyMoveCounter = parts.length > 4 ? parseFiftyMoveCounter(parts[4]) : 0;
            // This implementation does not require the full move counter (parts[5]).

            Board board = new Board();
            board.setBitboards(new long[Piece.COUNT + 2]);
            board.setPawns(pawns);
            board.setKnights(knight);
            board.setBishops(bishops);
            board.setRooks(rooks);
            board.setQueens(queens);
            board.setKings(king);
            board.setWhitePieces(whitePieces);
            board.setBlackPieces(blackPieces);
            board.setPieces(calculatePieceList(board));
            board.setWhite(whiteToMove);
            board.getState().setRights(castlingRights);
            board.getState().setEnPassantFile(enPassantFile);
            board.getState().setHalfMoveClock(fiftyMoveCounter);
            board.getState().setKey(Key.generateKey(board));
            board.getState().setPawnKey(Key.generatePawnKey(board));
            board.getState().setNonPawnKeys(Key.generateNonPawnKeys(board));
            board.getState().setMajorKey(Key.generateMajorKey(board));
            board.getState().setMinorKey(Key.generateMinorKey(board));

            return board;

    }

    public static String toFEN(Board board) {
        try {
            StringBuilder sb = new StringBuilder();

            for (int rank = 7; rank >= 0; rank--) {
                int emptySquares = 0;
                for (int file = 0; file < 8; file++) {
                    int square = Square.of(rank, file);
                    Piece piece = board.pieceAt(square);
                    if (piece != null) {
                        if (emptySquares != 0) {
                            sb.append(emptySquares);
                            emptySquares = 0;
                        }
                        long squareBB = Bits.of(square);
                        boolean white = (board.getWhitePieces() & squareBB) != 0;
                        String pieceCode = piece.code();
                        if (white) pieceCode = pieceCode.toUpperCase();
                        sb.append(pieceCode);
                    } else {
                        emptySquares++;
                    }
                }
                if (emptySquares != 0) {
                    sb.append(emptySquares);
                }
                if (rank > 0) {
                    sb.append('/');
                }
            }

            String whiteToMove = toSideToMove(board.isWhite());
            sb.append(" ").append(whiteToMove);

            String castlingRights = toCastlingRights(board.getState().getRights());
            sb.append(" ").append(castlingRights);

            String enPassantSquare = toEnPassantSquare(board.getState().getEnPassantFile(), board.isWhite());
            sb.append(" ").append(enPassantSquare);

            String fiftyMoveCounter = toFiftyMoveCounter(board.getState().getHalfMoveClock());
            sb.append(" ").append(fiftyMoveCounter);

            String fullMoveNumber = toFullMoveCounter(board.getPly());
            sb.append(" ").append(fullMoveNumber);

            return sb.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException(board.toString(), e);
        }
    }

    private static boolean parseSideToMove(String sideToMove) {
        return switch (sideToMove) {
            case "w" -> true;
            case "b" -> false;
            default -> throw new IllegalArgumentException("Invalid side to move! " + sideToMove);
        };
    }

    private static String toSideToMove(boolean sideToMove) {
        return sideToMove ? "w" : "b";
    }

    private static int parseCastlingRights(String castlingRights, long whiteRooks, long blackRooks, int whiteKing, int blackKing) {
        if (castlingRights.length() > 4) {
            throw new IllegalArgumentException("Invalid castling rights! " + castlingRights);
        }
        int rights = Castling.empty();
        for (int i = 0; i < castlingRights.length(); i++) {
            char right = castlingRights.charAt(i);
            switch (right) {
                case 'K' -> rights = Castling.setRook(rights, true, true, findRook(whiteRooks, true, true));
                case 'Q' -> rights = Castling.setRook(rights, false, true, findRook(whiteRooks, true, false));
                case 'k' -> rights = Castling.setRook(rights, true, false, findRook(blackRooks, false, true));
                case 'q' -> rights = Castling.setRook(rights, false, false, findRook(blackRooks, false, false));
                case 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' -> {
                    // Shredder FEN: White rooks on specified files
                    int file = File.fromNotation(right);
                    int kingFile = File.of(whiteKing);
                    if (file < kingFile) {
                        rights = Castling.setRook(rights, false, true, Square.of(0, file)); // Queenside
                    } else {
                        rights = Castling.setRook(rights, true, true, Square.of(0, file));  // Kingside
                    }
                }
                case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' -> {
                    // Shredder FEN: Black rooks on specified files
                    int file = File.fromNotation(Character.toUpperCase(right));
                    int kingFile = File.of(blackKing);
                    if (file < kingFile) {
                        rights = Castling.setRook(rights, false, false, Square.of(7, file)); // Queenside
                    } else {
                        rights = Castling.setRook(rights, true, false, Square.of(7, file));  // Kingside
                    }
                }
                case '-' -> {
                    // No castling rights, so return empty rights directly
                    return Castling.empty();
                }
                default -> throw new IllegalArgumentException("Invalid castling right! " + right);
            }
        }
        return rights;
    }

    private static String toCastlingRights(int rights) {
        if (rights == Castling.empty()) {
            return "-";
        }
        String rightsString = "";
        int wk = Castling.getRook(rights, true, true);
        if (wk != Castling.NO_ROOK) {
            rightsString += UCI.Options.chess960 ? File.toNotation(wk).toUpperCase() : "K";
        }
        int wq = Castling.getRook(rights, false, true);
        if (wq != Castling.NO_ROOK) {
            rightsString += UCI.Options.chess960 ? File.toNotation(wq).toUpperCase() : "Q";
        }
        int bk = Castling.getRook(rights, true, false);
        if (bk != Castling.NO_ROOK) {
            rightsString += UCI.Options.chess960 ? File.toNotation(bk) : "k";
        }
        int bq = Castling.getRook(rights, false, false);
        if (bq != Castling.NO_ROOK) {
            rightsString += UCI.Options.chess960 ? File.toNotation(bq) : "q";
        }
        return rightsString;
    }

    private static int parseEnPassantFile(String enPassantSquare) {
        if (enPassantSquare.equals("-")) {
            return -1;
        }
        int square = Square.fromNotation(enPassantSquare);
        return File.of(square);
    }

    private static String toEnPassantSquare(int enPassantFile, boolean white) {
        int rank = white ? 2 : 5;
        if (enPassantFile == -1) {
            return "-";
        }
        return Square.toNotation(Square.of(rank, enPassantFile));
    }

    private static int parseFiftyMoveCounter(String fiftyMoveCounter) {
        return Character.isDigit(fiftyMoveCounter.charAt(0)) ? Integer.parseInt(fiftyMoveCounter) : 0;
    }

    private static String toFiftyMoveCounter(int fiftyMoveCounter) {
        return Integer.toString(fiftyMoveCounter);
    }

    private static String toFullMoveCounter(int ply) {
        return Integer.toString(1 + (ply / 2));
    }

    private static Stream<String> parseSquare(String square) {
        if (square.length() != 1) {
            throw new IllegalArgumentException("Illegal square char! " + square);
        }
        boolean isLetter = Character.isLetter(square.charAt(0));
        return isLetter ? Stream.of(square) : IntStream.range(0, Integer.parseInt(square)).mapToObj(i -> "x");
    }

    public static Piece[] calculatePieceList(Board board) {

        Piece[] pieceList = new Piece[Square.COUNT];
        for (int square = 0; square < Square.COUNT; square++) {
            long squareMask = Bits.of(square);
            if ((squareMask & board.getPawns()) != 0)           pieceList[square] = Piece.PAWN;
            else if ((squareMask & board.getKnights()) != 0)    pieceList[square] = Piece.KNIGHT;
            else if ((squareMask & board.getBishops()) != 0)    pieceList[square] = Piece.BISHOP;
            else if ((squareMask & board.getRooks()) != 0)      pieceList[square] = Piece.ROOK;
            else if ((squareMask & board.getQueens()) != 0)     pieceList[square] = Piece.QUEEN;
            else if ((squareMask & board.getKings()) != 0)      pieceList[square] = Piece.KING;
        }
        return pieceList;

    }

    private static int findRook(long rooks, boolean white, boolean kingside) {

        long firstRank = white ? Bits.Rank.FIRST : Bits.Rank.EIGHTH;
        long firstRankRooks = rooks & firstRank;

        if (Bits.count(firstRankRooks) == 0) {
            throw new IllegalArgumentException("Illegal FEN: castling rights with no rooks on the first rank!");
        }

        if (Bits.count(firstRankRooks) == 1) {
            return Bits.next(firstRankRooks);
        }

        List<Integer> squares = Arrays.stream(Bits.collect(firstRankRooks))
                .boxed()
                .sorted(Comparator.comparing(File::of))
                .toList();

        return kingside ? squares.get(squares.size() - 1) : squares.get(0);

    }


}
