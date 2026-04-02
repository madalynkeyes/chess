package ui;

import chess.ChessBoard;

import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class ClientChessBoard {

    // Board dimensions.
    private static final int CHESS_SQUARES_NUM = 8;

    // Padded characters.
    public static String color = "WHITE";
    private static ChessBoard board;

    public static void draw(ChessBoard loadGameBoard, String playerColor) {
        board = loadGameBoard;
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        System.setProperty("file.encoding", "UTF-8");
        color = playerColor;
        out.print(ERASE_SCREEN);
        if(Objects.equals(playerColor, "WHITE") || Objects.equals(playerColor,"OBSERVER")) {
            drawChessboardWhite(out);
        } else {
            drawChessboardBlack(out);
        }
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);

    }

    private static void drawChessboardBlack(PrintStream out) {
        drawChessHeadersBlack(out);
        drawChessBoardBlack(out);
        drawChessHeadersBlack(out);
    }

    private static void drawChessboardWhite(PrintStream out) {
        drawChessHeadersWhite(out);
        drawChessBoardWhite(out);
        drawChessHeadersWhite(out);
    }

    private static void drawChessHeadersWhite(PrintStream out) {

        setBlack(out);

        String[] headers = { " a ","b","\u2009c","\u2009d ","e","\u2009f","\u2009g"," h " };
        setDarkGrey(out);
        out.print("  ");
        for (int boardCol = 0; boardCol < CHESS_SQUARES_NUM; ++boardCol) {
            setBorderSquare(out, headers[boardCol]);
        }
        out.print("    ");
        setDarkGrey(out);
        out.println();
    }

    private static void drawChessHeadersBlack(PrintStream out) {

        setBlack(out);

        String[] headers = { " h ","g","\u2009f","\u2009e ","d","\u2009c","\u2009b"," a " };
        setDarkGrey(out);
        out.print("  ");
        for (int boardCol = 0; boardCol < CHESS_SQUARES_NUM; ++boardCol) {
            setBorderSquare(out, headers[boardCol]);
        }
        out.print("    ");
        setDarkGrey(out);
        out.println();
    }


    private static void printChessHeader(PrintStream out, String headerText){
        printPiece(out, headerText);
    }


    private static void drawChessBoardWhite(PrintStream out) {
//        board = defaultBoard();
        for (int boardRow = 8; boardRow > 0; boardRow--){
            setBorderSquare(out, String.valueOf(boardRow));
            drawRowOfChessSquaresWhite(out,boardRow);

        }
    }

    private static void drawChessBoardBlack(PrintStream out) {
//        board = defaultBoard();
        for (int boardRow = 0; boardRow <8; boardRow++){
            setBorderSquare(out, String.valueOf(boardRow+1));
            drawRowOfChessSquaresBlack(out,boardRow);

        }
    }

    private static void drawRowOfChessSquaresWhite(PrintStream out, int boardRow) {
        for (int boardCol = 0; boardCol <CHESS_SQUARES_NUM; boardCol++){
            setSquareColorWhite(out, boardRow, boardCol);
            String pieceCode = WHITE_PAWN;
            pieceCode = getPieceCode(out, boardRow, boardCol, pieceCode);
            printPiece(out, pieceCode);
        }
        setBorderSquare(out, String.valueOf(boardRow));
        setDarkGrey(out);
        out.println();
    }


    private static void drawRowOfChessSquaresBlack(PrintStream out, int boardRow) {
        for (int boardCol = 7; boardCol >-1; boardCol--){
            setSquareColorBlack(out, boardRow, boardCol);
            String pieceCode = BLACK_PAWN;
            pieceCode = getPieceCode(out, boardRow + 1, boardCol, pieceCode);
            printPiece(out, pieceCode);
        }
        setBorderSquare(out, String.valueOf(boardRow+1));
        setDarkGrey(out);
        out.println();
    }

    private static String getPieceCode(PrintStream out, int boardRow, int boardCol, String pieceCode) {
        ChessPiece piece = board.getPiece(new ChessPosition(boardRow, boardCol +1));
        if (piece != null){
            switch (piece.getPieceType()){
                case KING -> pieceCode = WHITE_KING;
                case QUEEN -> pieceCode = WHITE_QUEEN;
                case BISHOP -> pieceCode = WHITE_BISHOP;
                case KNIGHT -> pieceCode = WHITE_KNIGHT;
                case ROOK -> pieceCode = WHITE_ROOK;
                case PAWN -> pieceCode = WHITE_PAWN;

            }
            switch (piece.getTeamColor()){
                case WHITE -> out.print(SET_TEXT_COLOR_WHITE);
                case BLACK -> out.print(SET_TEXT_COLOR_BLACK);
            }
        }
        return pieceCode;
    }

    private static void setBorderSquare(PrintStream out, String boardRow) {
        out.print(SET_BG_COLOR_LIGHT_WHITE);
        out.print(SET_TEXT_COLOR_DARK_BLUE);
        out.print(SET_TEXT_BOLD);
        printChessHeader(out, " "+boardRow+" ");
        out.print(RESET_TEXT_BOLD_FAINT);
    }


    private static void setSquareColorWhite(PrintStream out, int boardRow, int boardCol) {
        if(boardRow %2 != 0){ //odd row 1,3,5,7
            setColorOdd(out, boardCol);
        } else{
            setColorEven(out, boardCol);
        }
    }

    private static void setColorEven(PrintStream out, int boardCol) {
        if(boardCol %2==0){
            setLightBlue(out);
        } else {
            setLightYellow(out);
        }
    }

    private static void setLightYellow(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_YELLOW);
        out.print(SET_TEXT_COLOR_LIGHT_YELLOW);
    }

    private static void setLightBlue(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_BLUE);
        out.print(SET_TEXT_COLOR_LIGHT_BLUE);
    }

    private static void setColorOdd(PrintStream out, int boardCol) {
        if(boardCol %2==0){
            setLightYellow(out);
        } else{
            setLightBlue(out);
        }
    }

    private static void setSquareColorBlack(PrintStream out, int boardRow, int boardCol) {
        if(boardRow %2 == 0){ //odd row 1,3,5,7
            setColorOdd(out, boardCol);
        } else{
            setColorEven(out, boardCol);
        }
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setDarkGrey(PrintStream out){
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }


    private static void printPiece(PrintStream out, String piece){
        out.print(piece);
    }

//    private static ChessBoard defaultBoard(){
//        ChessGame defaultGame = new ChessGame();
//        return defaultGame.getBoard();
//    }

}

