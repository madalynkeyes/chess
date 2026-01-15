package chess;

public class PieceMovesCalculator {

    public PieceMovesCalculator(ChessPiece.PieceType type, ChessGame.TeamColor pieceColor) {
    }

    public void movePieceForward(ChessGame.TeamColor pieceColor, ChessPosition position){
        if (pieceColor == ChessGame.TeamColor.BLACK){ //it is black, move down the board
            System.out.printf(String.valueOf(position));
        } else { //it is white, move up the board
            System.out.println("it is white");
        }
    }
}
