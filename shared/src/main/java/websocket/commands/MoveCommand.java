package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

public class MoveCommand extends UserGameCommand {
    private final ChessMove move;

    public MoveCommand(CommandType commandType, String authToken, Integer gameID, String playerType, ChessMove move) {
        super(commandType, authToken, gameID, playerType);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        MoveCommand that = (MoveCommand) o;
        return Objects.equals(move, that.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), move);
    }
}
