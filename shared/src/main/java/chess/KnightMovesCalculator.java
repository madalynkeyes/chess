package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Calculates the moves a KNIGHT can take at a given position based on chess rules.
 */
public class KnightMovesCalculator extends PieceMovesCalculator {
    private final List<ChessMove> possibleMovesList;

    public KnightMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position);
        possibleMovesList = getMovesList();
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
    public Collection<ChessMove> getPossibleMoves() {
        String[] directions = {"NWW", "NNW", "NNE", "NEE", "SEE", "SSE", "SSW", "SWW"};
        for (String direction : directions) {
            addMoveIfPossible(direction);
        }
        return possibleMovesList;
    }

    @Override
    public ChessPosition getDesiredPosition(ChessPosition position, String direction) {
        if (Objects.equals(direction,"NNE") && position.getRow()<7 && position.getColumn()!=8){
            return new ChessPosition(position.getRow()+2, position.getColumn()+1);
        } else if (Objects.equals(direction,"NEE") && position.getRow()!=8 && position.getColumn()<7) {
            return new ChessPosition(position.getRow()+1, position.getColumn()+2);
        } else if (Objects.equals(direction,"NNW") && position.getRow()<7 && position.getColumn()!=1) {
            return new ChessPosition(position.getRow()+2, position.getColumn()-1);
        } else if (Objects.equals(direction,"NWW") && position.getRow()!=8 && position.getColumn()>2) {
            return new ChessPosition(position.getRow()+1, position.getColumn()-2);
        } else if (Objects.equals(direction, "SSE") && position.getRow()>2 && position.getColumn()!=8) {
            return new ChessPosition(position.getRow()-2, position.getColumn()+1);
        } else if (Objects.equals(direction,"SEE") && position.getRow()!=1 && position.getColumn()<7) {
            return new ChessPosition(position.getRow()-1, position.getColumn()+2);
        } else if (Objects.equals(direction,"SSW") && position.getRow()>2 && position.getColumn()!=1) {
            return new ChessPosition(position.getRow()-2, position.getColumn()-1);
        } else if (Objects.equals(direction, "SWW") && position.getRow()!=1 && position.getColumn()>2) {
            return new ChessPosition(position.getRow()-1, position.getColumn()-2);
        }
        return null;
    }

}
