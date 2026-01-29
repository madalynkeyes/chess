package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class RookBishopQueenCalculator extends PieceMovesCalculator{
    private final List<ChessMove> movesList;
    private final ChessPiece.PieceType type;
    private boolean hasEncounteredPiece;
    private final ChessPiece piece;
    public RookBishopQueenCalculator(ChessBoard board, ChessPosition position) {
        super(board, position);
        movesList = getMovesList();
        piece = board.getPiece(position);
        type = board.getPiece(position).getPieceType();
    }

    @Override
    public Collection<ChessMove> getPossibleMoves(){
        if (type == ChessPiece.PieceType.ROOK){
            addMoveIfPossible("north");
            addMoveIfPossible("south");
            addMoveIfPossible("west");
            addMoveIfPossible("east");
        } else if (type == ChessPiece.PieceType.BISHOP) {
            addMoveIfPossible("northwest");
            addMoveIfPossible("northeast");
            addMoveIfPossible("southwest");
            addMoveIfPossible("southeast");
        } else {
            addMoveIfPossible("north");
            addMoveIfPossible("south");
            addMoveIfPossible("west");
            addMoveIfPossible("east");
            addMoveIfPossible("northwest");
            addMoveIfPossible("northeast");
            addMoveIfPossible("southwest");
            addMoveIfPossible("southeast");
        }
        return movesList;
    }

    @Override
    public void addMoveIfPossible(String direction){
        hasEncounteredPiece=false;
        ChessPosition endPosition = position;
        while (!hasEncounteredPiece) {
            endPosition = getDesiredPosition(endPosition,direction);
            if (endPosition != null) {
                if (canCaptureOrMove(endPosition)) {
                    movesList.add(new ChessMove(position, endPosition, null));
                }
            }
        }
    }

    @Override
    public boolean canCaptureOrMove(ChessPosition endPosition) {
        if (board.getPiece(endPosition)!=null){
            hasEncounteredPiece=true;
            return board.getPiece(endPosition).getTeamColor() != piece.getTeamColor();
        }
        return true;
    }

    @Override
    public ChessPosition getDesiredPosition(ChessPosition position, String direction) {
        if (Objects.equals(direction, "north") && position.getRow()==8){
            hasEncounteredPiece=true;
        } else if (Objects.equals(direction,"south") &&position.getRow()==1) {
            hasEncounteredPiece=true;
        } else if (Objects.equals(direction,"east") && position.getColumn()==8) {
            hasEncounteredPiece=true;
        } else if (Objects.equals(direction,"west") && position.getColumn()==1) {
            hasEncounteredPiece=true;
        } else if (Objects.equals(direction,"northwest") && (position.getRow()==8 || position.getColumn()==1)) {
            hasEncounteredPiece=true;
        } else if (Objects.equals(direction,"northeast") && (position.getRow()==8 || position.getColumn()==8)) {
            hasEncounteredPiece=true;
        } else if (Objects.equals(direction,"southwest") && (position.getRow()==1 || position.getColumn()==1)) {
            hasEncounteredPiece=true;
        } else if (Objects.equals(direction,"southeast") && (position.getRow()==1 || position.getColumn()==8)) {
            hasEncounteredPiece=true;
        } return super.getDesiredPosition(position,direction);
    }

}