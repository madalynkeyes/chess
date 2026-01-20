package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

//DONT FORGET TO WRITE DOCUMENTATION!
public class PieceMovesCalculator {
    private final List<ChessMove> possibleMoves = new ArrayList<>();
    private boolean hasCapturedPiece = false;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType()== ChessPiece.PieceType.KING){
            new KingMovesCalculator(board,piece,position);
        } else if (piece.getPieceType()==ChessPiece.PieceType.ROOK) {
            new RookMovesCalculator(board,piece,position);
        } else if (piece.getPieceType()==ChessPiece.PieceType.BISHOP) {
            new BishopMovesCalculator(board,piece,position);
        } else if (piece.getPieceType()==ChessPiece.PieceType.QUEEN) {
            new RookMovesCalculator(board,piece,position);
            new BishopMovesCalculator(board,piece,position);
        } else if (piece.getPieceType()==ChessPiece.PieceType.KNIGHT) {
            new KnightMovesCalculator(board,piece,position);
        } else if (piece.getPieceType()==ChessPiece.PieceType.PAWN) {
            new PawnMovesCalculator(board,piece,position);
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

    public class BishopMovesCalculator {

        public BishopMovesCalculator(ChessBoard board, ChessPiece piece, ChessPosition startPosition) {
            ChessPosition nextPosition = startPosition;

            while (moveIfPossible(board,startPosition,nextPosition,"northeast",piece.getTeamColor()) && nextPosition.getRow()!=8 && nextPosition.getColumn()!=8 && !hasCapturedPiece){
                nextPosition=new ChessPosition(nextPosition.getRow()+1,nextPosition.getColumn()+1);
            }
            nextPosition = startPosition;
            while (moveIfPossible(board,startPosition,nextPosition,"northwest",piece.getTeamColor()) && nextPosition.getRow()!=8 && nextPosition.getColumn()!=1 && !hasCapturedPiece){
                nextPosition=new ChessPosition(nextPosition.getRow()+1, nextPosition.getColumn()-1);
            }
            nextPosition = startPosition;
            while (moveIfPossible(board,startPosition,nextPosition,"southeast",piece.getTeamColor()) && nextPosition.getRow()!=1 && nextPosition.getColumn()!=8 && !hasCapturedPiece){
                nextPosition=new ChessPosition(nextPosition.getRow()-1, nextPosition.getColumn()+1);
            }
            nextPosition = startPosition;
            while (moveIfPossible(board,startPosition,nextPosition,"southwest",piece.getTeamColor()) && nextPosition.getRow()!=1 && nextPosition.getColumn()!=1 && !hasCapturedPiece){
                nextPosition=new ChessPosition(nextPosition.getRow()-1,nextPosition.getColumn()-1);
            }
        }
    }

    public class KnightMovesCalculator {

        public KnightMovesCalculator(ChessBoard board, ChessPiece piece, ChessPosition startPosition) {
            /*"""
            | | | | | | | | |
            | | | |*| |*| | |
            | | |*| | | |*| |
            | | | | |N| | | |
            | | |*| | | |*| |
            | | | |*| |*| | |
            | | | | | | | | |
            | | | | | | | | |
                        """*/
            String[] directions = {"NWW","NNW","NNE","NEE","SEE","SSE","SSW","SWW"};
            for (String direction: directions){
                moveIfPossible(board,startPosition,direction,piece.getTeamColor());
            }
        }
        private void moveIfPossible(ChessBoard board, ChessPosition startPosition, String direction, ChessGame.TeamColor color){
            ChessPosition potentialPosition = getDesiredPosition(startPosition,direction);
            if (potentialPosition!=null){
                ChessPiece potentialCapture = board.getPiece(potentialPosition);
                if (potentialCapture!=null){
                    if(potentialCapture.getTeamColor()!=color){
                        possibleMoves.add(new ChessMove(startPosition,potentialPosition,null));
                    }
                } else{
                    possibleMoves.add(new ChessMove(startPosition,potentialPosition,null));
                }
            }
        }

        private ChessPosition getDesiredPosition (ChessPosition startPosition,String direction){
            int startCol = startPosition.getColumn();
            int startRow = startPosition.getRow();
            if (Objects.equals(direction, "NWW")){
                if (startRow!=8 && startCol>2){
                    return new ChessPosition(startRow+1,startCol-2);
                }
            } else if (Objects.equals(direction, "NNW")) {
                if (startRow<7 && startCol!=1){
                    return new ChessPosition(startRow+2,startCol-1);
                }
            } else if (Objects.equals(direction, "NNE")) {
                if (startRow<7 && startCol!=8){
                    return new ChessPosition(startRow+2,startCol+1);
                }
            } else if (Objects.equals(direction, "NEE")) {
                if (startRow!=8 && startCol<7) {
                    return new ChessPosition(startRow+1, startCol + 2);
                }
            } else if (Objects.equals(direction, "SEE")) {
                if (startRow!=1 && startCol<7){
                    return new ChessPosition(startRow-1,startCol+2);
                }
            } else if (Objects.equals(direction, "SSE")) {
                if (startRow>2 && startCol!=8){
                    return new ChessPosition(startRow-2,startCol+1);
                }
            } else if (Objects.equals(direction, "SSW")) {
                if (startRow>2 && startCol!=1){
                    return new ChessPosition(startRow-2,startCol-1);
                }
            } else if (Objects.equals(direction, "SWW")) {
                if (startRow!=1 && startCol>2){
                    return new ChessPosition(startRow-1, startCol-2);
                }
            }
            return null;
        }

    }

    public class PawnMovesCalculator {
        private boolean hasMoved;


        public PawnMovesCalculator(ChessBoard board, ChessPiece piece, ChessPosition startPosition) {
            hasMoved = false;
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (startPosition.getRow()!=2){hasMoved=true;}
                moveForwardIfPossible(board, startPosition, "north");
                moveDiagonalIfPossible(board,startPosition,"northwest",piece.getTeamColor());
                moveDiagonalIfPossible(board,startPosition,"northeast",piece.getTeamColor());

            } else {
                if (startPosition.getRow()!=7){hasMoved=true;}
                moveForwardIfPossible(board, startPosition, "south");
                moveDiagonalIfPossible(board,startPosition,"southwest",piece.getTeamColor());
                moveDiagonalIfPossible(board,startPosition,"southeast",piece.getTeamColor());
            }
        }

        private void moveForwardIfPossible(ChessBoard board, ChessPosition startPosition, String direction) {
            ChessPosition potentialPosition = getDesiredPosition(startPosition, direction);
            if (potentialPosition != null) {
                ChessPiece potentialCapture = board.getPiece(potentialPosition);
                if (potentialCapture == null) {
                    promotePawn(startPosition, potentialPosition);
                    if (Objects.equals(direction, "north") || Objects.equals(direction, "south")) {
                        if (!hasMoved) {  //can move up to two spots
                            potentialPosition = getDesiredPosition(potentialPosition, direction);
                            if (potentialPosition != null) {
                                potentialCapture = board.getPiece(potentialPosition);
                                if (potentialCapture == null) {
                                    possibleMoves.add(new ChessMove(startPosition, potentialPosition, null));
                                }
                            }
                        }
                    }
                    hasMoved=true;
                }
            }
        }

        private void moveDiagonalIfPossible(ChessBoard board, ChessPosition startPosition, String direction, ChessGame.TeamColor color) {
            ChessPosition potentialPosition = getDesiredPosition(startPosition, direction);
            if (potentialPosition != null) {
                ChessPiece potentialCapture = board.getPiece(potentialPosition);
                if (potentialCapture != null) {
                    if (potentialCapture.getTeamColor() != color) {
                        promotePawn(startPosition, potentialPosition);
                    }
                }
            }
        }

        private void promotePawn(ChessPosition startPosition, ChessPosition potentialPosition) {
            if (potentialPosition.getRow()==8 || potentialPosition.getRow()==1){
                possibleMoves.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.BISHOP));
                possibleMoves.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.QUEEN));
                possibleMoves.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.ROOK));
                possibleMoves.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.KNIGHT));
            } else{
                possibleMoves.add(new ChessMove(startPosition, potentialPosition, null));}
        }
    }

}

