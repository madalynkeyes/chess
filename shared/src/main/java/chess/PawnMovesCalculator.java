package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Calculates the moves a PAWN can take at a given position based on chess rules.
 */
public class PawnMovesCalculator extends PieceMovesCalculator {
    private final List<ChessMove> movesList;
    private final ChessPiece piece;
    private boolean hasMoved;
    public PawnMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position);
        movesList = getMovesList();
        piece = board.getPiece(position);
    }

    @Override
    public Collection<ChessMove> getPossibleMoves(){
        if (piece.getTeamColor()== ChessGame.TeamColor.WHITE){
            if (position.getRow()!=2){
                hasMoved=true;
            }
            addMoveIfPossible("north");
            addMoveIfPossible("northwest");
            addMoveIfPossible("northeast");
        } else {
            if(position.getRow()!=7){
                hasMoved=true;
            }
            addMoveIfPossible("south") ;
            addMoveIfPossible("southwest");
            addMoveIfPossible("southeast");
        }
        return movesList;
    }

    @Override
    public void addMoveIfPossible(String direction){
        ChessPosition endPosition = getDesiredPosition(position,direction);
        if (endPosition!=null){
            if(canCaptureOrMove(endPosition,direction)){
                checkIfPromote(endPosition);
                if (!hasMoved){
                    endPosition = getDesiredPosition(endPosition,direction);
                    if (endPosition!=null){
                        if(canCaptureOrMove(endPosition,direction)){
                            checkIfPromote(endPosition);
                        }
                    }
                }
            }
        }
    }

    public boolean canCaptureOrMove(ChessPosition endPosition, String direction) {
        if (!Objects.equals(direction, "north") && !Objects.equals(direction, "south")){
            if (board.getPiece(endPosition)!=null){
                return board.getPiece(endPosition).getTeamColor() != piece.getTeamColor();
            } return false;
        } else{
            return board.getPiece(endPosition) == null;
        }
    }

    private void checkIfPromote(ChessPosition endPosition) {
        if(endPosition.getRow()==8 || endPosition.getRow()==1){
            movesList.add(new ChessMove(position,endPosition, ChessPiece.PieceType.QUEEN));
            movesList.add(new ChessMove(position,endPosition, ChessPiece.PieceType.KNIGHT));
            movesList.add(new ChessMove(position,endPosition, ChessPiece.PieceType.BISHOP));
            movesList.add(new ChessMove(position,endPosition, ChessPiece.PieceType.ROOK));
        } else{
            movesList.add(new ChessMove(position,endPosition,null));
        }
    }

}
