package ui;

import dataaccess.exceptions.ResponseException;
import model.UserData;
import server.ServerFacade;
import service.requests.RegisterRequest;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Scanner;

import static ui.EscapeSequences.ERASE_SCREEN;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);
    public final ServerFacade serverFacade;

    public Client(String url) {
//        ServerFacade serverFacade = new ServerFacade(url);
        serverFacade = new ServerFacade(url);
    }

    public void run() {
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
                        registerPrompt();
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
            catch (InputMismatchException ex){
                System.out.println("Please enter an number");
            } catch (ResponseException e){
                System.out.println("Error");
            } catch (URISyntaxException | InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

    public void loginPrompt() {
        System.out.print("Please Type Username >>> ");
        String username = scanner.next();
        System.out.print("Please Type Password >>> ");
        String password = scanner.next();
    }

    public void registerPrompt() throws ResponseException, URISyntaxException, IOException, InterruptedException {
        System.out.print("Please Type Username >>> ");
        String username = scanner.next();
        System.out.print("Please Type Password >>> ");
        String password = scanner.next();
        System.out.print("Please Type Email >>> ");
        String email = scanner.next();
//        UserData userData = new UserData(username,password,email);
        RegisterRequest registerRequest = new RegisterRequest(username,password,email);
        serverFacade.register(registerRequest);
        System.out.printf("Thank you for registering %s",username);
    }
}
