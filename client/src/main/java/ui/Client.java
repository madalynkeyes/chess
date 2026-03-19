package ui;

import dataaccess.exceptions.ResponseException;

import server.ServerFacade;
import service.requests.CreateGameRequest;
import service.requests.LoginRequest;

import service.requests.RegisterRequest;

import java.io.PrintStream;

import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Scanner;

import static ui.EscapeSequences.ERASE_SCREEN;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);
    public final ServerFacade serverFacade;

    public Client(String url) {
        serverFacade = new ServerFacade(url);
    }

    public void run() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        System.out.println("♕ Welcome to Chess CS 240 ♕");
        mainMenu();

    }

    private void mainMenu() {
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
                        break;
                    case 2:
                        registerPrompt();
                        break;
                    case 3:
                        System.out.println("Thanks for playing! Exiting the game.");
                        System.exit(0);
                        break;
                    case 4:
                        helpPrompt();
                        break;
                    default:
                        System.out.println("Please choose a number: 1,2,3, or 4");
                }
            }
            catch (InputMismatchException ex){
                System.out.println("Please enter an number 1-4: ");
                scanner.nextLine();
            } catch (ResponseException e){
                System.out.println(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    private static void helpPrompt() {
        System.out.println("Tips: Please enter a number for the option you would like to choose.");
        System.out.println("Type the number and then press the 'enter' key on your keyboard.");
    }

    public void loginPrompt() throws Exception {
        try {
            System.out.print("Please Type Username >>> ");
            String username = scanner.next();
            System.out.print("Please Type Password >>> ");
            String password = scanner.next();
            LoginRequest loginRequest = new LoginRequest(username,password);
            serverFacade.login(loginRequest);
            System.out.printf("Welcome %s! Please choose an option:%n", username);
            postLoginPrompt();
        } catch (ResponseException e) {
            System.out.println("Username or Password incorrect. Please try again or register an account.");
            mainMenu();
        }
    }

    public void registerPrompt() throws Exception {
        try {
            System.out.print("Please Type Username >>> ");
            String username = scanner.next();
            System.out.print("Please Type Password >>> ");
            String password = scanner.next();
            System.out.print("Please Type Email >>> ");
            String email = scanner.next();
            RegisterRequest registerRequest = new RegisterRequest(username,password,email);
            serverFacade.register(registerRequest);
            System.out.printf("Account for %s has been created.%n",username);
            postLoginPrompt();
        } catch (Exception e) {
            System.out.println("Error: username already taken. Please login or register different username.");
            mainMenu();
        }

    }

    public void postLoginPrompt() {
        System.out.println(" 1. Create Game");
        System.out.println(" 2. Join Game");
        System.out.println(" 3. Observe Game");
        System.out.println(" 4. Logout");
        System.out.println(" 5. Help");
        while (true) {
            System.out.print(">>> ");
            try {
                int selectedNumber = scanner.nextInt();

                switch (selectedNumber) {
                    case 1:
                        createGamePrompt();
                        System.out.println("You chose to create a game");
                        break;
                    case 2:
                        System.out.println("You join game");
                        break;
                    case 3:
                        System.out.println("observe a gameee");
                        break;
                    case 4:
                        logoutPrompt();
                        mainMenu();
                        break;
                    case 5:
                        helpPrompt();
                        postLoginPrompt();
                        break;
                }
            } catch (InputMismatchException ex) {
                System.out.println("Please enter an number 1-5: ");
                scanner.nextLine();
            } catch (ResponseException e) {
//                System.out.println("Error");
                postLoginPrompt();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void logoutPrompt() throws Exception {
        try {
            serverFacade.logout();
            System.out.println("You have logged out.");
        } catch(Exception e){
            System.out.println("Log out failed.");
        }

    }

    public void createGamePrompt() throws ResponseException {
        try {
            System.out.print("Please Type New Game Name >>> ");
            String gameName = scanner.next();
            CreateGameRequest createGameRequest = new CreateGameRequest(null,gameName);
            serverFacade.createGame(createGameRequest);
            System.out.println("Game Created.");
            postLoginPrompt();
        } catch (Exception e) {
//            System.out.println(e.getMessage());
            System.out.println("Game name already exists. Please join game or create different game name.");
            throw new ResponseException(ResponseException.Code.ServerError,"game name already taken");
        }
    }
}
