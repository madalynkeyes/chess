package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PieceMovesCalculator {
    private final List<ChessMove> possibleMovesList = new ArrayList<>();
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessPiece piece;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.piece = board.getPiece(position);
    }

    public List<ChessMove> getPossibleMovesList() {
        return possibleMovesList;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public ChessPosition getPosition() {
        return position;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public Collection<ChessMove> getPossibleMoves() {
//        addMoveIfPossible(null);
        return possibleMovesList;
    }

    public void addMoveIfPossible(String direction) {
        ChessPosition potentialPosition = getDesiredPosition(direction, position);
        if (potentialPosition != null) {
            if (ifCanCaptureOrMove(potentialPosition)) {
                possibleMovesList.add(new ChessMove(position, potentialPosition, null));
            }
        }
    }

    public ChessPosition getDesiredPosition(String direction, ChessPosition position) {
        int startCol = position.getColumn();
        int startRow = position.getRow();
        if (Objects.equals(direction, "north")) {
            if (startRow != 8) {
                return new ChessPosition(startRow + 1, startCol);
            }
        } else if (Objects.equals(direction, "south")) {
            if (startRow != 1) {
                return new ChessPosition(startRow - 1, startCol);
            }
        } else if (Objects.equals(direction, "west")) {
            if (startCol != 1) {
                return new ChessPosition(startRow, startCol - 1);
            }
        } else if (Objects.equals(direction, "east")) {
            if (startCol != 8) {
                return new ChessPosition(startRow, startCol + 1);
            }
        } else if (Objects.equals(direction, "northwest")) {
            if (startRow != 8 && startCol != 1) {
                return new ChessPosition(startRow + 1, startCol - 1);
            }
        } else if (Objects.equals(direction, "northeast")) {
            if (startRow != 8 && startCol != 8) {
                return new ChessPosition(startRow + 1, startCol + 1);
            }
        } else if (Objects.equals(direction, "southwest")) {
            if (startRow != 1 && startCol != 1) {
                return new ChessPosition(startRow - 1, startCol - 1);
            }
        } else if (Objects.equals(direction, "southeast")) {
            if (startRow != 1 && startCol != 8) {
                return new ChessPosition(startRow - 1, startCol + 1);
            }
        }
        return null;
    }

    public boolean ifCanCaptureOrMove(ChessPosition position) {
        if (board.getPiece(position) != null) {
            return board.getPiece(position).getTeamColor() != piece.getTeamColor();
        } else {
            return true;
        }
    }

    /**
     * addMoveIfPossible method specifically for ROOK, BISHOP and QUEEN pieces.
     */
    public void addMultipleMovesIfPossible(String direction, ChessPosition startPosition, ChessPosition nextPosition) {
        ChessPosition potentialPosition = getDesiredPosition(direction, nextPosition);
        if (potentialPosition != null) {
            if (ifCanCaptureOrMove(potentialPosition)) {
                possibleMovesList.add(new ChessMove(startPosition, potentialPosition, null));
            }
        }
    }


}
