package client;

import chess.*;
import ui.Client;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Scanner;

import static ui.EscapeSequences.*;



public class ClientMain {

    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
        String serverUrl = "http://localhost:3306";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        try {
            new Client(serverUrl).run();

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }

    }
}
