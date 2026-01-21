package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Calculates the potential moves a specific piece can make.
 * Based on what type of piece the piece is, PieceMovesCalculator will call a specific calculator that knows the rules of that piece.
 * Note that a QUEEN has the same functionality of a BISHOP and ROOK combined.
 */
public class PieceMovesCalculator {
    private final List<ChessMove> possibleMoves = new ArrayList<>();
    private boolean hasCapturedPiece = false;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            new KingMovesCalculator(board, piece, position);
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            new RookMovesCalculator(board, piece, position);
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            new BishopMovesCalculator(board, piece, position);
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            new RookMovesCalculator(board, piece, position);
            new BishopMovesCalculator(board, piece, position);
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            new KnightMovesCalculator(board, piece, position);
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            new PawnMovesCalculator(board, piece, position);
        }
    }

    /**
     * @return a collection of positions a specific piece can move to
     */
    public Collection<ChessMove> getPossibleMoves() {
        return possibleMoves;
    }

    /**
     * Checks if piece is legally allowed to move to different position in specified direction.
     * Uses getDesiredPosition to give a position. With that position, we can check if enemy piece is there. If so, we can capture the piece and proceed to that position.
     * @param startPosition the current position of the piece
     * @param nextPosition the next position to look at, specifically for ROOK, BISHOP, & QUEEN pieces
     * @param direction the direction of the desired move
     * @param color the color of the piece
     * @return boolean of whether a piece can move in specified direction.
     */
    public boolean moveIfPossible(ChessBoard board, ChessPosition startPosition, ChessPosition nextPosition, String direction, ChessGame.TeamColor color) {
        hasCapturedPiece = false;
        ChessPosition potentialPosition = getDesiredPosition(nextPosition, direction);
        if (potentialPosition != null) {
            ChessPiece potentialCapture = board.getPiece(potentialPosition);
            if (potentialCapture != null) {
                if (potentialCapture.getTeamColor() != color) {
                    hasCapturedPiece = true;
                    possibleMoves.add(new ChessMove(startPosition, potentialPosition, null));
                    return true;
                }
            } else {
                possibleMoves.add(new ChessMove(startPosition, potentialPosition, null));
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if legal to move in desired direction and if so, gives the goal position.
     * @param startPosition the current position
     * @param direction the direction of desired move
     * @return a new position in the specified direction from the current position
     */
    public ChessPosition getDesiredPosition(ChessPosition startPosition, String direction) {
        int startCol = startPosition.getColumn();
        int startRow = startPosition.getRow();
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

    /**
     * Calculates the moves a KING can take at a given position based on chess rules.
     */
    public class KingMovesCalculator {

        public KingMovesCalculator(ChessBoard board, ChessPiece piece, ChessPosition startPosition) {
            String[] directions = {"northwest", "north", "northeast", "east", "southeast", "south", "southwest", "west"};
            for (String direction : directions) {
                moveIfPossible(board, startPosition, startPosition, direction, piece.getTeamColor());
            }
        }
    }

    /**
     * Calculates the moves a ROOK can take at a given position based on chess rules.
     */
    public class RookMovesCalculator {

        public RookMovesCalculator(ChessBoard board, ChessPiece piece, ChessPosition startPosition) {
            ChessPosition nextPosition = startPosition;
            int row = nextPosition.getRow();
            int col = nextPosition.getColumn();

            while (moveIfPossible(board, startPosition, nextPosition, "north", piece.getTeamColor()) && nextPosition.getRow() != 8 && !hasCapturedPiece) {
                nextPosition = new ChessPosition(nextPosition.getRow() + 1, col);
            }
            nextPosition = startPosition;
            while (moveIfPossible(board, startPosition, nextPosition, "south", piece.getTeamColor()) && nextPosition.getRow() != 1 && !hasCapturedPiece) {
                nextPosition = new ChessPosition(nextPosition.getRow() - 1, col);
            }
            nextPosition = startPosition;
            while (moveIfPossible(board, startPosition, nextPosition, "east", piece.getTeamColor()) && nextPosition.getColumn() != 8 && !hasCapturedPiece) {
                nextPosition = new ChessPosition(row, nextPosition.getColumn() + 1);
            }
            nextPosition = startPosition;
            while (moveIfPossible(board, startPosition, nextPosition, "west", piece.getTeamColor()) && nextPosition.getColumn() != 1 && !hasCapturedPiece) {
                nextPosition = new ChessPosition(row, nextPosition.getColumn() - 1);
            }

        }
    }

    /**
     * Calculates the moves a BISHOP can take at a given position based on chess rules.
     */
    public class BishopMovesCalculator {

        public BishopMovesCalculator(ChessBoard board, ChessPiece piece, ChessPosition startPosition) {
            ChessPosition nextPosition = startPosition;

            while (moveIfPossible(board, startPosition, nextPosition, "northeast", piece.getTeamColor()) && nextPosition.getRow() != 8 && nextPosition.getColumn() != 8 && !hasCapturedPiece) {
                nextPosition = new ChessPosition(nextPosition.getRow() + 1, nextPosition.getColumn() + 1);
            }
            nextPosition = startPosition;
            while (moveIfPossible(board, startPosition, nextPosition, "northwest", piece.getTeamColor()) && nextPosition.getRow() != 8 && nextPosition.getColumn() != 1 && !hasCapturedPiece) {
                nextPosition = new ChessPosition(nextPosition.getRow() + 1, nextPosition.getColumn() - 1);
            }
            nextPosition = startPosition;
            while (moveIfPossible(board, startPosition, nextPosition, "southeast", piece.getTeamColor()) && nextPosition.getRow() != 1 && nextPosition.getColumn() != 8 && !hasCapturedPiece) {
                nextPosition = new ChessPosition(nextPosition.getRow() - 1, nextPosition.getColumn() + 1);
            }
            nextPosition = startPosition;
            while (moveIfPossible(board, startPosition, nextPosition, "southwest", piece.getTeamColor()) && nextPosition.getRow() != 1 && nextPosition.getColumn() != 1 && !hasCapturedPiece) {
                nextPosition = new ChessPosition(nextPosition.getRow() - 1, nextPosition.getColumn() - 1);
            }
        }
    }

    /**
     * Calculates the moves a KNIGHT can take at a given position based on chess rules.
     */
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
            String[] directions = {"NWW", "NNW", "NNE", "NEE", "SEE", "SSE", "SSW", "SWW"};
            for (String direction : directions) {
                moveIfPossible(board, startPosition, direction, piece.getTeamColor());
            }
        }

        /**
         * Checks if KNIGHT piece can legally move in specified direction to goal position.
         * Uses get DesiredPosition to determine if goal position is on the board.
         * @param startPosition current position of KNIGHT piece
         * @param direction desired direction to take. With KNIGHT piece, the form will be "NNW" meaning "north north west".
         * @param color KNIGHT piece color
         */
        private void moveIfPossible(ChessBoard board, ChessPosition startPosition, String direction, ChessGame.TeamColor color) {
            ChessPosition potentialPosition = getDesiredPosition(startPosition, direction);
            if (potentialPosition != null) {
                ChessPiece potentialCapture = board.getPiece(potentialPosition);
                if (potentialCapture != null) {
                    if (potentialCapture.getTeamColor() != color) {
                        possibleMoves.add(new ChessMove(startPosition, potentialPosition, null));
                    }
                } else {
                    possibleMoves.add(new ChessMove(startPosition, potentialPosition, null));
                }
            }
        }

        /**
         * Checks if KNIGHT piece can move in specified direction to new position.
         * Checks the starting position of the piece, calculates the end position and determines whether the end position is on the board and therefore legal.
         * @param startPosition current position of KNIGHT piece
         * @param direction desired direction to take from current position
         * @return new position in specified direction from current position
         */
        private ChessPosition getDesiredPosition(ChessPosition startPosition, String direction) {
            int startCol = startPosition.getColumn();
            int startRow = startPosition.getRow();
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

    /**
     * Calculates the moves a PAWN can take at a given position based on chess rules.
     */
    public class PawnMovesCalculator {
        private boolean hasMoved;


        public PawnMovesCalculator(ChessBoard board, ChessPiece piece, ChessPosition startPosition) {
            hasMoved = false;
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (startPosition.getRow() != 2) {
                    hasMoved = true;
                }
                moveForwardIfPossible(board, startPosition, "north");
                moveDiagonalIfPossible(board, startPosition, "northwest", piece.getTeamColor());
                moveDiagonalIfPossible(board, startPosition, "northeast", piece.getTeamColor());

            } else {
                if (startPosition.getRow() != 7) {
                    hasMoved = true;
                }
                moveForwardIfPossible(board, startPosition, "south");
                moveDiagonalIfPossible(board, startPosition, "southwest", piece.getTeamColor());
                moveDiagonalIfPossible(board, startPosition, "southeast", piece.getTeamColor());
            }
        }

        /**
         * Determines whether a PAWN piece can move forward.
         * If a piece is in front of PAWN, the PAWN cannot move forward.
         * If PAWN is in original starting position, it has the option to move forward two spaces if both spaces in front of it are empty.
         * @param startPosition current position of PAWN
         * @param direction desired direction to go based on color of the PAWN. If PAWN is white, it will go north. If PAWN is black, it will go south.
         */
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
                    hasMoved = true;
                }
            }
        }

        /**
         * Determines if PAWN can capture a piece and move diagonally.
         * If enemy piece is diagonal to PAWN, the PAWN can capture the piece and move to that position.
         * @param startPosition current position of PAWN
         * @param direction diagonal direction to PAWN
         * @param color color of PAWN
         */
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

        /**
         * Checks if PAWN is eligible to be promoted. If so, add to possibleMoves.
         * If PAWN reaches opposite edge, it can be promoted to a BISHOP, QUEEN, ROOK or KNIGHT
         */
        private void promotePawn(ChessPosition startPosition, ChessPosition potentialPosition) {
            if (potentialPosition.getRow() == 8 || potentialPosition.getRow() == 1) {
                possibleMoves.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.BISHOP));
                possibleMoves.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.QUEEN));
                possibleMoves.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.ROOK));
                possibleMoves.add(new ChessMove(startPosition, potentialPosition, ChessPiece.PieceType.KNIGHT));
            } else {
                possibleMoves.add(new ChessMove(startPosition, potentialPosition, null));
            }
        }
    }

}

