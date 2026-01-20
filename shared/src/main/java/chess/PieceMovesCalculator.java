package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

//DONT FORGET TO WRITE DOCUMENTATION!
public class PieceMovesCalculator {
    List<ChessMove> possibleMoves = new ArrayList<>();

    public PieceMovesCalculator(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType()== ChessPiece.PieceType.KING){
            KingMovesCalculator kingCalculator = new KingMovesCalculator(board,piece,position);
        }
        //else if (piece.getPieceType()==ChessPiece.PieceType.ROOK) {

        //}
    }

    public Collection<ChessMove> getPossibleMoves(){
        return possibleMoves;
    }

    public void moveIfPossible(ChessBoard board,ChessPosition position, String direction, ChessGame.TeamColor color){
        ChessPosition potentialPosition = getDesiredPosition(position,direction);
        if (potentialPosition!=null){
            ChessPiece potentialCapture = board.getPiece(potentialPosition);
            if (potentialCapture!=null){
                if(potentialCapture.getTeamColor()!=color){
                    possibleMoves.add(new ChessMove(position,potentialPosition,null));
                }
            } else{
                possibleMoves.add(new ChessMove(position,potentialPosition,null));
            }
        }
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
                moveIfPossible(board,startPosition,direction,piece.getTeamColor());
            }
        }
    }
}

