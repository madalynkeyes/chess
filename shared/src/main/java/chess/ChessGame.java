package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor currentTeam;
    ChessBoard currentBoard;
    ChessBoard boardCopy;

    public ChessGame() {
        currentTeam = TeamColor.WHITE;
        currentBoard = new ChessBoard();
        currentBoard.resetBoard();
        setBoard(currentBoard);

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
        ChessPiece piece = currentBoard.getPiece(startPosition);
        if (piece != null) {
            TeamColor pieceColor = piece.getTeamColor();
            Collection<ChessMove> allPossibleMoves = new ArrayList<>();
            Collection<ChessMove> possibleMoves = piece.pieceMoves(boardCopy, startPosition);
            for (ChessMove move : possibleMoves) {
                boardCopy.movePiece(move.getStartPosition(), move.getEndPosition(), move.getPromotionPiece()); //move piece
                if (!isInCheck(pieceColor)) { //double check king is still good
                    allPossibleMoves.add(move);
                }
                boardCopy = new ChessBoard(currentBoard);

            }
            return allPossibleMoves;
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
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = currentBoard.getPiece(startPosition);
        if (piece != null && piece.getTeamColor() == currentTeam) {
            Collection<ChessMove> possibleMoves = validMoves(startPosition);
            boolean canMove = possibleMoves.contains(move);
            if (canMove) {
                currentBoard.movePiece(startPosition, endPosition, move.getPromotionPiece());
            } else {
                throw new InvalidMoveException("Error: Move not valid");
            }
            currentTeam = (currentTeam == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
            boardCopy = new ChessBoard(currentBoard);
        } else if (piece == null){
            throw new InvalidMoveException("Error: Move not valid.");
        } else {
            throw new InvalidMoveException("Error: Please wait for your turn.");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition;
        kingPosition = getKingPosition(teamColor);
        Collection<ChessMove> possibleMoves;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = boardCopy.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    possibleMoves = piece.pieceMoves(boardCopy, new ChessPosition(row, col));
                    if (canCaptureKing(possibleMoves, kingPosition)) {
                        return true;
                    }
                    possibleMoves.clear();
                }
            }
        }
        return false;
    }

    /**
     * Determines if enemy piece can capture king or put king in check
     * @param possibleMoves list of moves the enemy's piece can make
     * @param kingPosition position of king
     * @return whether piece can capture other team's king
     */
    private boolean canCaptureKing(Collection<ChessMove> possibleMoves, ChessPosition kingPosition) {
        for (ChessMove move : possibleMoves) {
            ChessPosition endPosition = move.getEndPosition();
            if (Objects.equals(endPosition, kingPosition)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets the position of king piece for specific team
     *
     * @param teamColor which team wants to find king's position
     * @return king piece position
     */
    public ChessPosition getKingPosition(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = boardCopy.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    kingPosition = new ChessPosition(row, col);
                }
            }
        }
        return kingPosition;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return ifNoValidMoves(teamColor);
        }
        return false;

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return ifNoValidMoves(teamColor);
        }
        return false;
    }

    /**
     * Determine if there are no valid moves for any of the team's pieces
     * If no valid moves, then it is either a checkmate or stalemate
     *
     * @param teamColor the team who wants to see if in checkmate or stalemate
     * @return whether a team has any valid moves for any of their pieces
     */
    private boolean ifNoValidMoves(TeamColor teamColor) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = currentBoard.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == teamColor && validMoves(new ChessPosition(row, col)) != null) {
                    Collection<ChessMove> move = validMoves(new ChessPosition(row, col));
                    possibleMoves.addAll(move);
                }

            }
        }
        return possibleMoves.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
        boardCopy = new ChessBoard(currentBoard);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        boolean currTeam = currentTeam == chessGame.currentTeam;
        boolean currBoard = Objects.equals(currentBoard, chessGame.currentBoard);
        boolean boardCp = Objects.equals(boardCopy, chessGame.boardCopy);
        return currTeam && currBoard && boardCp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeam, currentBoard, boardCopy);
    }
}
