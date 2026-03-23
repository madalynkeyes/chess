package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ClientChessBoard {

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 3;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;
    private static final int CHESS_SQUARES_NUM = 8;

    // Padded characters.
    private static final String EMPTY = "   ";
    private static final String EMPTY2 = " ";
    private static final String X = " X ";
    private static final String O = " O ";

    private static Random rand = new Random();


    public static void main() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        drawChessHeaders(out);
        drawChessBoard(out);
        drawChessHeaders(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);

    }

    private static void drawChessHeaders(PrintStream out) {

        setBlack(out);

        String[] headers = { "a","b","c","d","e","f","g","h" };
        setRed(out);
        out.print(EMPTY.repeat(1));
        for (int boardCol = 0; boardCol < CHESS_SQUARES_NUM; ++boardCol) {
            setRedSquare(out, headers[boardCol]);
        }
        out.print(EMPTY.repeat(1));
        setBlack(out);
        out.println();
    }


    private static void printChessHeader(PrintStream out, String headerText){
        out.print(EMPTY2.repeat(1));
        printPiece(out, headerText);
        out.print(EMPTY2.repeat(1));
    }


    private static void drawChessBoard(PrintStream out) {
        for (int boardRow = 8; boardRow > 0; boardRow--){
            setRedSquare(out, String.valueOf(boardRow));
            drawRowOfChessSquares(out,boardRow);

        }
    }

    private static void drawRowOfChessSquares(PrintStream out, int boardRow) {
        for (int boardCol = 0; boardCol <CHESS_SQUARES_NUM; boardCol++){
            setSquareColor(out, boardRow, boardCol);
            printPiece(out, rand.nextBoolean() ? X : O);

        }
        setRedSquare(out, String.valueOf(boardRow));
        setBlack(out);
        out.println();
    }

    private static void setRedSquare(PrintStream out, String boardRow) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(SET_TEXT_BOLD);
        printChessHeader(out, boardRow);
        out.print(RESET_TEXT_BOLD_FAINT);
    }


    private static void setSquareColor(PrintStream out, int boardRow, int boardCol) {
        if(boardRow %2 != 0){ //odd row 1,3,5,7
            if(boardCol %2==0){
                out.print(SET_BG_COLOR_DARK_GREY);
                out.print(SET_TEXT_COLOR_WHITE);
            } else{
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
            }
        } else{
            if(boardCol %2==0){
                out.print(SET_BG_COLOR_LIGHT_GREY);
                out.print(SET_TEXT_COLOR_BLACK);
            } else {
                out.print(SET_BG_COLOR_DARK_GREY);
                out.print(SET_TEXT_COLOR_WHITE);
            }
        }
    }


    private static void drawHorizontalLine(PrintStream out) {

        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_PADDED_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
            setBlack(out);
            out.print(EMPTY.repeat(boardSizeInSpaces));

            setBlack(out);
            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }


    private static void printPiece(PrintStream out, String piece){
        out.print(piece);
    }


}

