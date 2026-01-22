package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Calculates the moves a PAWN can take at a given position based on chess rules.
 */
public class PawnMovesCalculator extends PieceMovesCalculator {
    private final List<ChessMove> possibleMovesList;
    private final ChessPiece piece;
    private boolean hasMoved;

    public PawnMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position);
        possibleMovesList = getPossibleMovesList();
        piece = getPiece();
        hasMoved = false;
    }

    @Override
    public Collection<ChessMove> getPossibleMoves() {
        hasMoved = false;
        ChessPosition startPosition = getPosition();
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (getPosition().getRow() != 2) {
                hasMoved = true;
            }
            addMoveIfPossible("north");
            addMoveIfPossible("northwest");
            addMoveIfPossible("northeast");

        } else {
            if (startPosition.getRow() != 7) {
                hasMoved = true;
            }
            addMoveIfPossible("south");
            addMoveIfPossible("southwest");
            addMoveIfPossible("southeast");
        }
        return possibleMovesList;
    }
    //

    /**
     * Adds move to possibleMovesList if move in direction is legal.
     * Note if PAWN is in starting position, it can move up to 2 spaces forward if no pieces in its path.
     *
     * @param direction intended direction of pawn (forward or diagonally)
     */
    @Override
    public void addMoveIfPossible(String direction) {
        ChessPosition potentialPosition = getDesiredPosition(direction, getPosition());
        if (potentialPosition != null) {
            if (ifCanCaptureOrMove(potentialPosition, direction)) {
                checkIfPromotePawn(getPosition(), potentialPosition);
                if ((Objects.equals(direction, "north") && !hasMoved) || (Objects.equals(direction, "south") && !hasMoved)) {
                    potentialPosition = getDesiredPosition(direction, potentialPosition);
                    if (ifCanCaptureOrMove(potentialPosition, direction)) {
                        checkIfPromotePawn(getPosition(), potentialPosition);
                    }
                }
            }
        }
    }

    /**
     * Tells if piece can move in intended direction or capture piece and move.
     * If the piece is going forward (north or south) and there is a piece in front of the PAWN, the PAWN can't move forward.
     * If the piece wants to move diagonally, there needs to be an enemy piece diagonally from PAWN.
     *
     * @return boolean of whether PAWN can move in specific direction
     */
    public boolean ifCanCaptureOrMove(ChessPosition position, String direction) {
        boolean b = Objects.equals(direction, "north") || Objects.equals(direction, "south");
        if (getBoard().getPiece(position) != null) {
            if (b) {
                return false;
            } else {
                return getBoard().getPiece(position).getTeamColor() != piece.getTeamColor();
            }
        } else {
            return b;
        }
    }


    /**
     * Checks if PAWN is eligible to be promoted. If so, add to possibleMoves.
     * If PAWN reaches opposite edge, it can be promoted to a BISHOP, QUEEN, ROOK or KNIGHT
     */
    private void checkIfPromotePawn(ChessPosition startPosition, ChessPosition potentialPosition) {
        if (potentialPosition.getRow() == 8 || potentialPosition.getRow() == 1) {
            possibleMovesList.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.BISHOP));
            possibleMovesList.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.QUEEN));
            possibleMovesList.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.ROOK));
            possibleMovesList.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.KNIGHT));
        } else {
            possibleMovesList.add(new ChessMove(startPosition, potentialPosition, null));
        }
    }
}
