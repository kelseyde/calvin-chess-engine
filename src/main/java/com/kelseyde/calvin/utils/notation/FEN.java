package com.kelseyde.calvin.utils.notation;

import com.kelseyde.calvin.board.*;
import com.kelseyde.calvin.board.Bits.File;
import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.uci.UCI;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * FEN (Forsyth-Edwards Notation) is the standard notation for describing the state of a chess position. It encodes into a
 * single string the position of the pieces, the side to move, castling rights, en passant target square, halfmove clock,
 * and fullmove number.
 * <p>
 * This class provides methods to parse a FEN string into a {@link Board} object and to convert a {@link Board} object
 * back into a FEN string.
 */
public record FEN(String value,
                  long[] bitboards,
                  Piece[] pieces,
                  int enPassantFile,
                  boolean whiteToMove,
                  int castleRights,
                  int halfMoveClock,
                  int fullMoveNumber) {

    public static final String STARTPOS = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public static FEN startpos() {
        return FEN.parse(FEN.STARTPOS);
    }

    /**
     * Parses a FEN string.
     * Checks that the string is a valid FEN string, and throws an {@link InvalidFenException} if it is not.
     */
    public static FEN parse(String fen) throws InvalidFenException {

        if (fen == null || fen.isEmpty())
            throw new InvalidFenException("FEN string is null or empty", fen);

        String[] parts = fen.split(" ");

        if (parts.length < 4 || parts.length > 6)
            throw new InvalidFenException("FEN string has an invalid number of parts", fen);

        String board = parts[0];

        int slashCount = (int) board.chars().filter(ch -> ch == '/').count();
        if (slashCount != 7)
            throw new InvalidFenException("Invalid number of ranks", fen);

        String[] ranks = board.split("/");
        for (String rank : ranks) {

            if (FenPatterns.RANK.matcher(rank).matches())
                throw new InvalidFenException("Consecutive numbers in rank are not allowed", fen);

            int squareCount = 0;
            for (int i = 0; i < rank.length(); i++) {
                char c = rank.charAt(i);
                if (Character.isDigit(c))
                    squareCount += Character.getNumericValue(c);
                 else if (Character.isLetter(c))
                    squareCount += 1;
            }

            if (squareCount != 8) {
                if (squareCount > 8)
                    throw new InvalidFenException("Rank has too many pieces", fen);
                else
                    throw new InvalidFenException("Rank has too few pieces", fen);
            }
        }

        List<String> kings = parseKings(board);
        if (kings.size() != 2)
            throw new InvalidFenException("Invalid number of kings", fen);

        if (!kings.contains("K"))
            throw new InvalidFenException("Missing white king", fen);

        if (!kings.contains("k"))
            throw new InvalidFenException("Missing black king", fen);

        if (!FenPatterns.BOARD.matcher(board).matches())
            throw new InvalidFenException("Invalid board representation", fen);

        String turn = parts[1];
        if (!FenPatterns.TURN.matcher(turn).matches())
            throw new InvalidFenException("Invalid turn: " + turn, fen);

        String castle = parts[2];
        if (!FenPatterns.CASTLE.matcher(castle).matches())
            throw new InvalidFenException("Invalid castling rights: " + castle, fen);

        String enPassant = parts[3];
        if (!FenPatterns.EN_PASSANT.matcher(enPassant).matches())
            throw new InvalidFenException("Invalid en passant square: " + enPassant, fen);

        String halfMove = parts.length > 4 ? parts[4] : "0";
        if (!FenPatterns.HALF_MOVE.matcher(halfMove).matches())
            throw new InvalidFenException("Invalid half move clock: " + halfMove, fen);

        String fullMove = parts.length > 5 ? parts[5] : "0";
        if (!FenPatterns.FULL_MOVE.matcher(fullMove).matches())
            throw new InvalidFenException("Invalid full move number: " + fullMove, fen);

        long[] bitboards = new long[Piece.COUNT + 2];
        Piece[] pieces = new Piece[Square.COUNT];
        calculatePiecePositions(board, bitboards, pieces);
        int enPassantFile = parseEnPassantFile(enPassant);
        boolean whiteToMove = turn.equals("w");
        int castleRights = parseCastlingRights(castle, bitboards);
        int halfMoveClock = Integer.parseInt(halfMove);
        int fullMoveNumber = Integer.parseInt(fullMove);

        return new FEN(fen, bitboards, pieces, enPassantFile, whiteToMove, castleRights, halfMoveClock, fullMoveNumber);
    }

    /**
     * Converts this {@link FEN} to a {@link Board}.
     */
    public Board toBoard() {
        Board board = new Board();
        board.setBitboards(bitboards);
        board.setPieces(pieces);
        board.setWhite(whiteToMove);
        board.getState().setRights(castleRights);
        board.getState().setHalfMoveClock(halfMoveClock);
        board.getState().setFullMoveNumber(fullMoveNumber);
        board.getState().setEnPassantFile(enPassantFile);
        board.getState().setKey(Key.generateKey(board));
        board.getState().setPawnKey(Key.generatePawnKey(board));
        board.getState().setNonPawnKeys(Key.generateNonPawnKeys(board));
        board.calculatePins();
        return board;
    }

    /**
     * Converts a {@link Board} to a {@link FEN}.
     */
    public static FEN fromBoard(Board board) {
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
                if (emptySquares != 0)
                    sb.append(emptySquares);
                if (rank > 0)
                    sb.append('/');
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

            return FEN.parse(sb.toString());
        } catch (Exception e) {
            throw new InvalidFenException(board.toString(), e);
        }
    }

    @Override
    public String toString() {
        return value;
    }

    private static void calculatePiecePositions(String boardString, long[] bitboards, Piece[] pieces) {

        String[] files = boardString.split("/");
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
                parsePiece(squareValue).ifPresent(piece -> {
                    boolean isWhite = Character.isUpperCase(squareValue.charAt(0));
                    updatePiecePosition(bitboards, pieces, square, piece, isWhite);
                });
            }
        }

    }

    private static int parseEnPassantFile(String epSquare) {
        return epSquare.equals("-") ? -1 : File.of(Square.fromNotation(epSquare));
    }

    private static List<String> parseKings(String boardString) {
        return boardString.chars()
                .mapToObj(i -> (char) i)
                .filter(i -> i == 'K' || i == 'k')
                .map(String::valueOf)
                .toList();
    }

    private static Stream<String> parseSquare(String square) {
        if (square.length() != 1)
            throw new InvalidFenException("Illegal square char! " + square);

        boolean isLetter = Character.isLetter(square.charAt(0));
        return isLetter ? Stream.of(square) : IntStream.range(0, Integer.parseInt(square)).mapToObj(i -> "x");
    }

    private static int parseCastlingRights(String castlingRights, long[] bitboards) {

        long whiteRooks = bitboards[Piece.ROOK.index()] & bitboards[Piece.WHITE_PIECES];
        long blackRooks = bitboards[Piece.ROOK.index()] & bitboards[Piece.BLACK_PIECES];
        int whiteKing = Bits.next(bitboards[Piece.KING.index()] & bitboards[Piece.WHITE_PIECES]);
        int blackKing = Bits.next(bitboards[Piece.KING.index()] & bitboards[Piece.BLACK_PIECES]);

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
                default -> throw new InvalidFenException("Invalid castling right! " + right);
            }
        }
        return rights;
    }

    private static int findRook(long rooks, boolean white, boolean kingside) {

        long firstRank = white ? Bits.Rank.FIRST : Bits.Rank.EIGHTH;
        long firstRankRooks = rooks & firstRank;

        if (Bits.count(firstRankRooks) == 0)
            throw new InvalidFenException("Castling rights with no rooks on the first rank!");

        if (Bits.count(firstRankRooks) == 1)
            return Bits.next(firstRankRooks);

        List<Integer> squares = Arrays.stream(Bits.collect(firstRankRooks))
                .boxed()
                .sorted(Comparator.comparing(File::of))
                .toList();

        return kingside ? squares.get(squares.size() - 1) : squares.get(0);

    }

    private static Optional<Piece> parsePiece(String code) {
        return Arrays.stream(Piece.values())
                .filter(piece -> piece.code().equalsIgnoreCase(code))
                .findAny();
    }

    private static void updatePiecePosition(long[] bbs, Piece[] pcs, int square, Piece piece, boolean white) {
        int pieceIndex = piece.index();
        int colourIndex = Piece.COUNT + Colour.index(white);
        bbs[pieceIndex] |= Bits.of(square);
        bbs[colourIndex] |= Bits.of(square);
        pcs[square] = piece;
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

    private static String toEnPassantSquare(int enPassantFile, boolean white) {
        int rank = white ? 2 : 5;
        if (enPassantFile == -1) {
            return "-";
        }
        return Square.toNotation(Square.of(rank, enPassantFile));
    }

    private static String toFiftyMoveCounter(int fiftyMoveCounter) {
        return Integer.toString(fiftyMoveCounter);
    }

    private static String toFullMoveCounter(int ply) {
        return Integer.toString(1 + (ply / 2));
    }

    private static String toSideToMove(boolean sideToMove) {
        return sideToMove ? "w" : "b";
    }

    private static class FenPatterns {
        public static final Pattern BOARD       = Pattern.compile("^([rnbqkpRNBQKP1-8]+/){7}[rnbqkpRNBQKP1-8]+$");
        public static final Pattern RANK        = Pattern.compile(".*\\d{2,}.*");
        public static final Pattern TURN        = Pattern.compile("^[wb]$");
        public static final Pattern CASTLE      = Pattern.compile("^(-|[A-HKQ]*[a-hkq]*)$");
        public static final Pattern EN_PASSANT  = Pattern.compile("^(-|[a-h][36])$");
        public static final Pattern HALF_MOVE   = Pattern.compile("^\\d+$");
        public static final Pattern FULL_MOVE   = Pattern.compile("^\\d+$");
    }

    public static class InvalidFenException extends RuntimeException {
        public InvalidFenException(String message, String fen) {
            super(String.format("Invalid FEN %s: %s", fen, message));
        }

        public InvalidFenException(String message) {
            super(String.format("Invalid FEN: %s", message));
        }

        public InvalidFenException(String fen, Throwable cause) {
            super(String.format("Invalid FEN %s: %s", fen, cause.getMessage()), cause);
        }
    }


}
