package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//DONT FORGET TO WRITE DOCUMENTATION!
public class PieceMovesCalculator {
    static int row;
    static int col;
    int right = 1;
    int left = -1;
    int up = 2;
    int down = -2;
    int rightUp = 3;
    int rightDown = -3;
    int leftUp = 4;
    int leftDown = -4;
    List<ChessMove> possibleMoves = new ArrayList<>();

    public PieceMovesCalculator(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        row = position.getRow();
        col = position.getColumn();
        if (piece.getPieceType()== ChessPiece.PieceType.KING){
            KingMovesCalculator kingCalculator = new KingMovesCalculator(board,piece,position);
        }
    }

    public Collection<ChessMove> getPossibleMoves(){
        return possibleMoves;
    }

    public ChessPosition moveIfPossible(ChessBoard board,ChessPiece piece,int row, int col){
        ChessPosition potentialPosition = new ChessPosition(row,col);
        ChessPiece potentialCapture = board.getPiece(potentialPosition);
        if (potentialCapture != null){
            if (potentialCapture.getTeamColor() != piece.getTeamColor()) {
                return new ChessPosition(row,col);
            }
        } else{
            return new ChessPosition(row,col);
        }
        return null;
    }

    public ChessPosition movePieceUpOrDown(ChessBoard board, ChessPiece piece, int direction){
        int newRow;
        if (direction == 2){ //up
            if (row != 8){
                newRow = row+1;
            } else{return null;}
        } else{
            if (row != 1){
                newRow = row-1;
            } else{return null;}
        }
        return moveIfPossible(board,piece,newRow,col);
    }

    public ChessPosition movePieceSideways(ChessBoard board, ChessPiece piece, int direction){
        int newCol;
        if (direction == 1){ //move right
            if (col !=8){  //won't go out of bounds
                newCol = col+1;
            } else{return null;}
        } else{ //move left
            if (col !=1){
            newCol = col-1;
            } else{return null;}
        }
        return moveIfPossible(board,piece,row,newCol);
    }

    public ChessPosition movePieceDiagonal(ChessBoard board, ChessPiece piece, int direction){
        int newRow;
        int newCol;
        if (direction ==3){ //rightUp
            if (row !=8 && col !=8){
                newRow = row+1;
                newCol = col+1;
            } else{return null;}
        } else if (direction == -3) { //rightDown
            if (row !=8 && col !=1){
                newRow = row-1;
                newCol = col+1;
            } else{return null;}
        } else if (direction == 4) { //leftUp
            if (row !=1 && col !=8){
                newRow = row+1;
                newCol = col-1;
            } else{return null;}
        } else { //leftDown
            if (row!=1 && col !=1){
                newRow = row-1;
                newCol = col-1;
            } else{return null;}
        }
        return moveIfPossible(board,piece,newRow,newCol);
    }

    public class KingMovesCalculator {

        public KingMovesCalculator(ChessBoard board,ChessPiece piece,ChessPosition startPosition){
            ChessPosition moveUp = movePieceUpOrDown(board, piece,up);
            ChessPosition moveDown = movePieceUpOrDown(board, piece,down);
            ChessPosition moveRight = movePieceSideways(board,piece,right);
            ChessPosition moveLeft = movePieceSideways(board,piece,left);
            ChessPosition moveRightUp = movePieceDiagonal(board,piece,rightUp);
            ChessPosition moveRightDown = movePieceDiagonal(board,piece,rightDown);
            ChessPosition moveLeftUp = movePieceDiagonal(board,piece,leftUp);
            ChessPosition moveLeftDown = movePieceDiagonal(board,piece,leftDown);
            checkIfNull(startPosition, moveUp, moveDown, moveRight, moveLeft);
            checkIfNull(startPosition, moveRightUp, moveRightDown, moveLeftUp, moveLeftDown);
        }

        private void checkIfNull(ChessPosition startPosition, ChessPosition moveUp, ChessPosition moveDown, ChessPosition moveRight, ChessPosition moveLeft) {
            if (moveUp != null) {
                possibleMoves.add(new ChessMove(startPosition, moveUp, null));
            }
            if(moveDown != null){
                possibleMoves.add(new ChessMove(startPosition, moveDown, null));
            }
            if (moveRight!= null){
                possibleMoves.add(new ChessMove(startPosition,moveRight,null));
            }
            if (moveLeft != null){
                possibleMoves.add(new ChessMove(startPosition,moveLeft,null));
            }
        }
    }
}
