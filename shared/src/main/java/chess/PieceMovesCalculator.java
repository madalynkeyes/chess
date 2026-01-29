package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PieceMovesCalculator {
    public final ChessBoard board;
    public final ChessPosition position;
    private final List<ChessMove> movesList = new ArrayList<>();
    private final ChessPiece piece;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        piece = board.getPiece(position);
    }

    public List<ChessMove> getMovesList() {
        return movesList;
    }

    public Collection<ChessMove>getPossibleMoves(){
        return movesList;
    }

    public void addMoveIfPossible(String direction){
        ChessPosition endPosition = getDesiredPosition(position,direction);
        if (endPosition!=null){
            if(canCaptureOrMove(endPosition)){
                movesList.add(new ChessMove(position,endPosition,null));
            }
        }
    }

    public boolean canCaptureOrMove(ChessPosition endPosition) {
        if (board.getPiece(endPosition)!=null){
            return board.getPiece(endPosition).getTeamColor() != piece.getTeamColor();
        } return true;
    }

    public ChessPosition getDesiredPosition(ChessPosition position, String direction) {
        if (Objects.equals(direction, "north") && position.getRow()!=8){
            return new ChessPosition(position.getRow()+1, position.getColumn());
        } else if (Objects.equals(direction,"south") &&position.getRow()!=1) {
            return new ChessPosition(position.getRow()-1, position.getColumn());
        } else if (Objects.equals(direction,"east") && position.getColumn()!=8) {
            return new ChessPosition(position.getRow(), position.getColumn()+1);
        } else if (Objects.equals(direction,"west") && position.getColumn()!=1) {
            return new ChessPosition(position.getRow(), position.getColumn()-1);
        } else if (Objects.equals(direction,"northwest") && position.getRow()!=8 && position.getColumn()!=1) {
            return new ChessPosition(position.getRow()+1, position.getColumn()-1);
        } else if (Objects.equals(direction,"northeast") && position.getRow()!=8 && position.getColumn()!=8) {
            return new ChessPosition(position.getRow()+1, position.getColumn()+1);
        } else if (Objects.equals(direction,"southwest") && position.getRow()!=1 && position.getColumn()!=1) {
            return new ChessPosition(position.getRow()-1, position.getColumn()-1);
        } else if (Objects.equals(direction,"southeast") && position.getRow()!=1 && position.getColumn()!=8) {
            return new ChessPosition(position.getRow()-1, position.getColumn()+1);
        } return null;
    }
}