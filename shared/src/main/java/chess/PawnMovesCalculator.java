package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PawnMovesCalculator extends PieceMovesCalculator2{
    private List<ChessMove> possibleMovesList;
    private ChessPiece piece;

    public PawnMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position);
        possibleMovesList = getPossibleMovesList();
        piece = getPiece();
    }

    @Override
    public Collection<ChessMove> getPossibleMoves(){
        boolean hasMoved = false;
        ChessBoard board = getBoard();
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

//    @Override
//    public boolean ifCanCaptureOrMove(ChessPosition position){
//        if (getBoard().getPiece(position)!=null){
//            return board.getPiece(position).getTeamColor() != piece.getTeamColor();
//        } else {
//            return true;
//        }
//    }



//    /**
//     * Checks if PAWN is eligible to be promoted. If so, add to possibleMoves.
//     * If PAWN reaches opposite edge, it can be promoted to a BISHOP, QUEEN, ROOK or KNIGHT
//     */
//    private void promotePawn(ChessPosition startPosition, ChessPosition potentialPosition) {
//        if (potentialPosition.getRow() == 8 || potentialPosition.getRow() == 1) {
//            possibleMoves.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.BISHOP));
//            possibleMoves.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.QUEEN));
//            possibleMoves.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.ROOK));
//            possibleMoves.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.KNIGHT));
//        } else {
//            possibleMoves.add(new ChessMove(startPosition, potentialPosition, null));
//        }
//    }
}
