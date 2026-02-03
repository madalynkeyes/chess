package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor currentTeam;
    ChessBoard board;
    ChessBoard boardCopy;
    public ChessGame() {
        currentTeam = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
        boardCopy = new ChessBoard(board);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        TeamColor player = currentTeam;
        Collection<ChessMove> allPossibleMoves = new ArrayList<>();
        setTeamTurn(TeamColor.WHITE);
        if (currentTeam == player){
            if (isInCheck(TeamColor.WHITE)){
                System.out.println("you are in check");
            } else {
                for (int row =1;row<=8;row++){
                    for (int col=1;col<=8;col++){
                        ChessPiece piece = boardCopy.getPiece(new ChessPosition(row,col));
                        if(piece!=null && piece.getTeamColor()==currentTeam){
                            Collection<ChessMove>possibleMoves = piece.pieceMoves(boardCopy,new ChessPosition(row,col));
                            allPossibleMoves.addAll(possibleMoves);
                        }
                    }
                }
                return allPossibleMoves;
            }
        } else{ //it is black's turn

        }
        return null;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        Collection<ChessMove>possibleMoves = validMoves(startPosition);
        boolean canMove = possibleMoves.contains(move);
        if (canMove){
            //make move
//            ChessPiece piece = board.getPiece(move.getStartPosition());
            board.squares[move.getEndPosition().getRow()][move.getEndPosition().getColumn()]=board.squares[move.getStartPosition().getRow()][move.getStartPosition().getColumn()] ;
            board.squares[move.getStartPosition().getRow()][move.getStartPosition().getColumn()] = null;
        } else {
            throw new InvalidMoveException("Move not valid");
        }
        currentTeam= (currentTeam==TeamColor.WHITE)? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        TeamColor enemyColor = (teamColor==TeamColor.WHITE)? TeamColor.BLACK : TeamColor.WHITE;
        for (int row =1;row<=8;row++){
            for (int col=1;col<=8;col++){
                ChessPiece piece = boardCopy.getPiece(new ChessPosition(row,col));
                if(piece!=null){

                    if (piece.getPieceType()== ChessPiece.PieceType.KING && piece.getTeamColor()==TeamColor.WHITE){
                        kingPosition = new ChessPosition(row,col);
                    }
                }
            }
        }
        for (int row =1;row<=8;row++){
            for (int col=1;col<=8;col++){
                ChessPiece piece = boardCopy.getPiece(new ChessPosition(row,col));
                if(piece!=null && piece.getTeamColor()==enemyColor){
                    Collection<ChessMove>possibleMoves = piece.pieceMoves(boardCopy,new ChessPosition(row,col));
                    if (possibleMoves!=null && kingPosition!=null && possibleMoves.contains(kingPosition)){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
