package chess;

import java.util.Collection;
import java.util.List;

/**
 * Calculates the moves a KING can take at a given position based on chess rules.
 */
public class KingMovesCalculator extends PieceMovesCalculator {
    private final List<ChessMove> possibleMovesList;

    public KingMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position);
        possibleMovesList = getPossibleMovesList();
    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        addMoveIfPossible("northeast");
        addMoveIfPossible("north");
        addMoveIfPossible("northwest");
        addMoveIfPossible("west");
        addMoveIfPossible("southwest");
        addMoveIfPossible("south");
        addMoveIfPossible("southeast");
        addMoveIfPossible("east");
        return possibleMovesList;
    }


}
