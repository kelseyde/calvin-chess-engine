package com.kelseyde.calvin.evaluation;

import com.kelseyde.calvin.board.Bits.Square;
import com.kelseyde.calvin.board.Piece;

public record Feature(Piece piece, boolean white, int square) {

    public static class FeatureUpdate {

        public Feature[] adds = new Feature[2];
        public int addCount = 0;

        public Feature[] subs = new Feature[2];
        public int subCount = 0;

        public void pushAdd(Feature update) {
            adds[addCount++] = update;
        }

        public void pushSub(Feature update) {
            subs[subCount++] = update;
        }

        public void pushAddSub(Feature add, Feature sub) {
            pushAdd(add);
            pushSub(sub);
        }

        public void pushAddSubSub(Feature add, Feature sub1, Feature sub2) {
            pushAdd(add);
            pushSub(sub1);
            pushSub(sub2);
        }

        public void pushAddAddSubSub(Feature add1, Feature add2, Feature sub1, Feature sub2) {
            pushAdd(add1);
            pushAdd(add2);
            pushSub(sub1);
            pushSub(sub2);
        }

    }


    /**
     * Compute the index of the feature vector for a given piece, colour and square.
     */
    public static int index(Piece piece, int square, boolean mirror, boolean whitePiece, boolean whitePerspective) {

        // If we are looking at the board from black's perspective, then we flip the rank so that the position is
        // evaluated 'as if' we were white - that way the same network can be used for both colours.
        if (!whitePerspective) {
            square = Square.flipRank(square);
        }

        // If the network is horizontally mirrored, and the king is on the 'mirrored' side, then we flip the file so
        // that it were as if the king were on the non-mirrored side of the board.
        if (mirror) {
            square = Square.flipFile(square);
        }

        final int pieceIndex = piece.index();
        final int pieceOffset = pieceIndex * Square.COUNT;

        final boolean ourPiece = whitePiece == whitePerspective;
        final int colourOffset = ourPiece ? 0 : (Square.COUNT * Piece.COUNT);

        return colourOffset + pieceOffset + square;

    }

}
