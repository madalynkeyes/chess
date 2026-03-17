package client;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Scanner;

import static ui.EscapeSequences.*;



public class ClientMain {
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        System.out.println("♕ Welcome to Chess CS 240 ♕");
        System.out.println("Please Enter Number To Select Option:");
        System.out.println(" 1. Login");
        System.out.println(" 2. Register");
        System.out.println(" 3. Quit");
        System.out.println(" 4. Help");

        while (true) {
            System.out.print(">>> ");

            try{
                int selectedNumber = scanner.nextInt();

                switch(selectedNumber){
                    case 1:
                        loginPrompt();
                        System.out.printf("You chose: %d. Login\n",selectedNumber);
                        break;
                    case 2:
                        System.out.printf("You chose: %d. Register\n",selectedNumber);
                        break;
                    case 3:
                        System.out.printf("You chose: %d. Quit\n",selectedNumber);
                        System.exit(0);
                        break;
                    case 4:
                        System.out.printf("You chose: %d. Help\n",selectedNumber);
                        break;
                    default:
                        System.out.println("Please choose a number: 1,2,3, or 4");
                }
            }
            catch (InputMismatchException e){
                System.out.println("Please enter an number");
            }

        }

    }

    private static void loginPrompt() {
        System.out.print("Please Type Username >>> ");
        String username = scanner.next();
        System.out.print("Please Type Password >>> ");
        String password = scanner.next();

    }


}
