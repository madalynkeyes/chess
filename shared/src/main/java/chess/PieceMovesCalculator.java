package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Parent class for all the different pieces and their legal moves.
 * Contains the main methods that are implemented differently by each subclass.
 */
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

    /**
     * Getter for movesList (list of possible moves)
     * @return movesList
     */
    public List<ChessMove> getMovesList() {
        return movesList;
    }

    /**
     * Gives us a list of all possible moves for a specific piece.
     * @return movesList
     */
    public Collection<ChessMove>getPossibleMoves(){
        return movesList;
    }

    /**
     * If possible to move in specified direction, add that move to movesList.
     */
    public void addMoveIfPossible(String direction){
        ChessPosition endPosition = getDesiredPosition(position,direction);
        if (endPosition!=null){
            if(canCaptureOrMove(endPosition)){
                movesList.add(new ChessMove(position,endPosition,null));
            }
        }
    }

    /**
     * Check if enemy piece in desired position and whether specified piece can capture enemy or move.
     * @param endPosition desired position of piece
     * @return boolean of whether piece can move to end position.
     */
    public boolean canCaptureOrMove(ChessPosition endPosition) {
        if (board.getPiece(endPosition)!=null){
            return board.getPiece(endPosition).getTeamColor() != piece.getTeamColor();
        } return true;
    }

    /**
     * Get the position of the square in specified direction from current location of piece.
     * @param position current location/square of the piece
     * @param direction specified direction the piece wants to travel
     * @return desired position
     */
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