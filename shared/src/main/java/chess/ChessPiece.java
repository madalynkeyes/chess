package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (type == PieceType.KING){
            PieceMovesCalculator2 kingCalculator = new KingMovesCalculator(board,myPosition);
            return kingCalculator.getPossibleMoves();
        } else if (type == PieceType.ROOK) {
            PieceMovesCalculator2 rookCalculator = new RookMovesCalculator(board,myPosition);
            return rookCalculator.getPossibleMoves();
        } else if (type == PieceType.BISHOP) {
            PieceMovesCalculator2 bishopCalculator = new BishopMovesCalculator(board,myPosition);
            return bishopCalculator.getPossibleMoves();
        } else if (type == PieceType.QUEEN) {
            PieceMovesCalculator2 rookCalculator = new RookMovesCalculator(board,myPosition);
            PieceMovesCalculator2 bishopCalculator = new BishopMovesCalculator(board,myPosition);
            List<ChessMove> rookPossibleMoves = (List<ChessMove>) rookCalculator.getPossibleMoves();
            List<ChessMove> bishopPossibleMoves = (List<ChessMove>) bishopCalculator.getPossibleMoves();
            rookPossibleMoves.addAll(bishopPossibleMoves);
            return rookPossibleMoves;
        } else if (type == PieceType.KNIGHT) {
            PieceMovesCalculator2 knightCalculator = new KnightMovesCalculator(board,myPosition);
            return knightCalculator.getPossibleMoves();
        }
        return null;
//        return new PieceMovesCalculator(board, myPosition).getPossibleMoves();
    }
}
