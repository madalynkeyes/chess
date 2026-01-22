package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
/**
 * Calculates the moves a KNIGHT can take at a given position based on chess rules.
 */
public class KnightMovesCalculator extends PieceMovesCalculator2{
    private List<ChessMove> possibleMovesList;
    public KnightMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position);
        possibleMovesList = getPossibleMovesList();
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
    }

    @Override
    public Collection<ChessMove> getPossibleMoves(){
        String[] directions = {"NWW", "NNW", "NNE", "NEE", "SEE", "SSE", "SSW", "SWW"};
        for (String direction : directions) {
            addMoveIfPossible(direction);
        }
        return possibleMovesList;
    }

    @Override
    public ChessPosition getDesiredPosition(String direction, ChessPosition position){
        int startCol = position.getColumn();
        int startRow = position.getRow();
        if (Objects.equals(direction, "NWW")) {
            if (startRow != 8 && startCol > 2) {
                return new ChessPosition(startRow + 1, startCol - 2);
            }
        } else if (Objects.equals(direction, "NNW")) {
            if (startRow < 7 && startCol != 1) {
                return new ChessPosition(startRow + 2, startCol - 1);
            }
        } else if (Objects.equals(direction, "NNE")) {
            if (startRow < 7 && startCol != 8) {
                return new ChessPosition(startRow + 2, startCol + 1);
            }
        } else if (Objects.equals(direction, "NEE")) {
            if (startRow != 8 && startCol < 7) {
                return new ChessPosition(startRow + 1, startCol + 2);
            }
        } else if (Objects.equals(direction, "SEE")) {
            if (startRow != 1 && startCol < 7) {
                return new ChessPosition(startRow - 1, startCol + 2);
            }
        } else if (Objects.equals(direction, "SSE")) {
            if (startRow > 2 && startCol != 8) {
                return new ChessPosition(startRow - 2, startCol + 1);
            }
        } else if (Objects.equals(direction, "SSW")) {
            if (startRow > 2 && startCol != 1) {
                return new ChessPosition(startRow - 2, startCol - 1);
            }
        } else if (Objects.equals(direction, "SWW")) {
            if (startRow != 1 && startCol > 2) {
                return new ChessPosition(startRow - 1, startCol - 2);
            }
        }
        return null;
    }

}
