package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

//DONT FORGET TO WRITE DOCUMENTATION!
public class PieceMovesCalculator {
    List<ChessMove> possibleMoves = new ArrayList<>();
    boolean hasCapturedPiece = false;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType()== ChessPiece.PieceType.KING){
            KingMovesCalculator kingCalculator = new KingMovesCalculator(board,piece,position);
        }
        else if (piece.getPieceType()==ChessPiece.PieceType.ROOK) {
            RookMovesCalculator rookCalculator = new RookMovesCalculator(board,piece,position);
        }
    }

    public Collection<ChessMove> getPossibleMoves(){
        return possibleMoves;
    }

    public boolean moveIfPossible(ChessBoard board, ChessPosition startPosition, ChessPosition nextPosition,String direction, ChessGame.TeamColor color){
        hasCapturedPiece = false;
        ChessPosition potentialPosition = getDesiredPosition(nextPosition,direction);
        if (potentialPosition!=null){
            ChessPiece potentialCapture = board.getPiece(potentialPosition);
            if (potentialCapture!=null){
                if(potentialCapture.getTeamColor()!=color){
                    hasCapturedPiece = true;
                    possibleMoves.add(new ChessMove(startPosition,potentialPosition,null));
                    return true;
                }
            } else{
                possibleMoves.add(new ChessMove(startPosition,potentialPosition,null));
                return true;
            }
        }
        return false;
    }

    public ChessPosition getDesiredPosition (ChessPosition startPosition,String direction){
        int startCol = startPosition.getColumn();
        int startRow = startPosition.getRow();
        if (Objects.equals(direction, "north")){
            if (startRow!=8){
                return new ChessPosition(startRow+1,startCol);
            }
        } else if (Objects.equals(direction, "south")) {
            if (startRow!=1){
                return new ChessPosition(startRow-1,startCol);
            }
        } else if (Objects.equals(direction, "west")) {
            if (startCol!=1){
                return new ChessPosition(startRow,startCol-1);
            }
        } else if (Objects.equals(direction, "east")) {
            if (startCol!=8) {
                return new ChessPosition(startRow, startCol + 1);
            }
        } else if (Objects.equals(direction, "northwest")) {
            if (startRow!=8 && startCol!=1){
                return new ChessPosition(startRow+1,startCol-1);
            }
        } else if (Objects.equals(direction, "northeast")) {
            if (startRow!=8 && startCol!=8){
                return new ChessPosition(startRow+1,startCol+1);
            }
        } else if (Objects.equals(direction, "southwest")) {
            if (startRow!=1 && startCol!=1){
                return new ChessPosition(startRow-1,startCol-1);
            }
        } else if (Objects.equals(direction, "southeast")) {
            if (startRow!=1 && startCol!=8){
                return new ChessPosition(startRow-1, startCol+1);
            }
        }
        return null;
    }


    public class KingMovesCalculator {

        public KingMovesCalculator(ChessBoard board,ChessPiece piece,ChessPosition startPosition){
            String[] directions = {"northwest","north","northeast","east","southeast","south","southwest","west"};
            for (String direction: directions){
                moveIfPossible(board,startPosition,startPosition,direction,piece.getTeamColor());
            }
        }
    }

    public class RookMovesCalculator{

        public RookMovesCalculator(ChessBoard board,ChessPiece piece,ChessPosition startPosition) {
            ChessPosition nextPosition = startPosition;
            int row = nextPosition.getRow();
            int col = nextPosition.getColumn();

            while (moveIfPossible(board,startPosition,nextPosition,"north",piece.getTeamColor()) && nextPosition.getRow()!=8 && !hasCapturedPiece){
                nextPosition=new ChessPosition(nextPosition.getRow()+1,col);
            }
            nextPosition = startPosition;
            while (moveIfPossible(board,startPosition,nextPosition,"south",piece.getTeamColor()) && nextPosition.getRow()!=1 && !hasCapturedPiece){
                nextPosition=new ChessPosition(nextPosition.getRow()-1,col);
            }
            nextPosition = startPosition;
            while (moveIfPossible(board,startPosition,nextPosition,"east",piece.getTeamColor()) && nextPosition.getColumn()!=8 && !hasCapturedPiece){
                nextPosition=new ChessPosition(row,nextPosition.getColumn()+1);
            }
            nextPosition = startPosition;
            while (moveIfPossible(board,startPosition,nextPosition,"west",piece.getTeamColor()) && nextPosition.getColumn()!=1 && !hasCapturedPiece){
                nextPosition=new ChessPosition(row,nextPosition.getColumn()-1);
            }

        }
    }
}

