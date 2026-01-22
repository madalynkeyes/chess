package chess;

import java.util.Collection;
import java.util.List;

/**
 * Calculates the moves a BISHOP can take at a given position based on chess rules.
 */
public class BishopMovesCalculator extends PieceMovesCalculator {
    private final List<ChessMove> possibleMovesList;
    private final ChessPiece piece;
    private boolean hasEncounteredPiece;

    public BishopMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position);
        possibleMovesList = getPossibleMovesList();
        piece = getPiece();
    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        ChessPosition startPosition = getPosition();
        ChessPosition nextPosition = startPosition;
        while (!hasEncounteredPiece && nextPosition.getRow() != 8 && nextPosition.getColumn() != 8) {
            addMultipleMovesIfPossible("northeast", startPosition, nextPosition);
            nextPosition = new ChessPosition(nextPosition.getRow() + 1, nextPosition.getColumn() + 1);
        }
        hasEncounteredPiece = false;
        nextPosition = startPosition;
        while (!hasEncounteredPiece && nextPosition.getRow() != 8 && nextPosition.getColumn() != 1) {
            addMultipleMovesIfPossible("northwest", startPosition, nextPosition);
            nextPosition = new ChessPosition(nextPosition.getRow() + 1, nextPosition.getColumn() - 1);
        }
        hasEncounteredPiece = false;
        nextPosition = startPosition;
        while (!hasEncounteredPiece && nextPosition.getRow() != 1 && nextPosition.getColumn() != 8) {
            addMultipleMovesIfPossible("southeast", startPosition, nextPosition);
            nextPosition = new ChessPosition(nextPosition.getRow() - 1, nextPosition.getColumn() + 1);
        }
        hasEncounteredPiece = false;
        nextPosition = startPosition;
        while (!hasEncounteredPiece && nextPosition.getRow() != 1 && nextPosition.getColumn() != 1) {
            addMultipleMovesIfPossible("southwest", startPosition, nextPosition);
            nextPosition = new ChessPosition(nextPosition.getRow() - 1, nextPosition.getColumn() - 1);
        }
        return possibleMovesList;
    }


    @Override
    public boolean ifCanCaptureOrMove(ChessPosition position) {
        if (getBoard().getPiece(position) != null) {
            hasEncounteredPiece = true;
            return getBoard().getPiece(position).getTeamColor() != piece.getTeamColor();
        } else {
            return true;
        }
    }
}
