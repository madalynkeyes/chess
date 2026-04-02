package ui;

import chess.ChessBoard;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.ResponseException;

import server.ServerFacade;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.LoginRequest;

import service.requests.RegisterRequest;
import service.responses.GameListFormat;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.io.PrintStream;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ui.ClientChessBoard.draw;
import static ui.EscapeSequences.ERASE_SCREEN;

public class Client implements NotificationHandler {
    private static final Scanner SCANNER = new Scanner(System.in);
    public final ServerFacade serverFacade;
    private final WebSocketFacade ws;
    private final Map<Integer,Integer> gameIDmap = new HashMap<>();
    private String authToken;
    ChessBoard currentBoard;

    public Client(String url) throws ResponseException {
        serverFacade = new ServerFacade(url);
        ws = new WebSocketFacade(url,this);
    }

    public void run() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        System.out.println("♕ Welcome to Chess CS 240 ♕");
        mainMenu();

    }

    private void mainMenu() {
        while(true){
            System.out.println("Please Enter Number To Select Option:");
            System.out.println(" 1. Login");
            System.out.println(" 2. Register");
            System.out.println(" 3. Quit");
            System.out.println(" 4. Help");
            System.out.print(">>> ");
            String input = SCANNER.nextLine();
            if(input.equalsIgnoreCase("quit") || input.equals("3")){
                System.out.println("Thanks for playing! Exiting the game.");
                return;
            }
            try {
                int option = Integer.parseInt(input);
                switch (option){
                    case 1 -> {
                        boolean loginSuccess = loginPrompt();
                        promptToPostLogin(loginSuccess);
                    }
                    case 2 -> {
                        boolean registerSuccess = registerPrompt();
                        promptToPostLogin(registerSuccess);
                    }
                    case 4 -> helpPrompt();
                    default ->  System.out.println("Please enter an number 1-4: ");
                }
            } catch (Exception e) {
                System.out.println("Please enter a number or type 'quit' to exit.");
            }
        }
    }

    private void promptToPostLogin(boolean loginSuccess) {
        if (loginSuccess){
            postLoginPrompt();
        }
    }


    private static void helpPrompt() {
        System.out.println("Tips: Please enter a number for the option you would like to choose.");
        System.out.println("Type the number and then press the 'enter' key on your keyboard.");
        System.out.println("If at any time you want to exit or go back, type 'quit' or 'back'.");
    }

    public boolean loginPrompt()  {
        try {
            System.out.print("Please Type Username >>> ");
            String username = SCANNER.nextLine();
            if (isBackOrQuit(username)){
                return false;
            }
            System.out.print("Please Type Password >>> ");
            String password = SCANNER.nextLine();
            if (isBackOrQuit(password)){
                return false;
            }
            LoginRequest loginRequest = new LoginRequest(username,password);
            authToken = serverFacade.login(loginRequest);
            System.out.printf("Welcome %s! Please choose an option:%n", username);
            return true;
        } catch (ResponseException | URISyntaxException | IOException | InterruptedException e) {
            System.out.println("Username or Password incorrect. Please try again or register an account.");
            return false;
        }
    }

    public boolean registerPrompt(){
        try {
            System.out.print("Please Type Username >>> ");
            String username = SCANNER.nextLine();
            if (isBackOrQuit(username)){
                return false;
            }
            System.out.print("Please Type Password >>> ");
            String password = SCANNER.nextLine();
            if (isBackOrQuit(password)){
                return false;
            }
            System.out.print("Please Type Email >>> ");
            String email = SCANNER.nextLine();
            if (isBackOrQuit(email)){
                return false;
            }
            RegisterRequest registerRequest = new RegisterRequest(username,password,email);
            authToken = serverFacade.register(registerRequest);
            System.out.printf("Account for %s has been created.%n",username);
            return true;
        } catch (AlreadyTakenException e) {
            System.out.println("Error: username already taken. Please login or choose a different username.");
            return false;
        } catch (BadRequestException e) {
            System.out.println("Error: invalid input.");
            return false;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }

    }

    public void postLoginPrompt() {
        while (true) {
            System.out.println("Please Enter Number To Select Option:");
            System.out.println(" 1. Create Game");
            System.out.println(" 2. Join Game");
            System.out.println(" 3. Observe Game");
            System.out.println(" 4. List Games");
            System.out.println(" 5. Logout");
            System.out.println(" 6. Help");
            System.out.print(">>> ");
            String input = SCANNER.nextLine();
            if (isBackOrQuit(input)) {
                return;
            }
            try {
                int option = Integer.parseInt(input);
                switch (option) {
                    case 1 -> createGamePrompt();
                    case 2 -> joinGamePrompt();
                    case 3 -> observeGamePrompt();
                    case 4 -> listGamesPrompt();
                    case 5 -> {
                        logoutPrompt();
                        return;
                    }
                    case 6 -> helpPrompt();
                    default -> System.out.println("Please enter an number 1-6: ");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number or type 'quit' to exit.");
            } catch (AlreadyTakenException e){
                System.out.println("Please choose different username");
            }
        }
    }


    public void logoutPrompt(){
        try {
            serverFacade.logout();
            System.out.println("You have logged out.");
        } catch(Exception e){
            System.out.println("Log out failed.");
        }

    }

    public void createGamePrompt() {

        try {
            System.out.print("Please Type New Game Name >>> ");
            String gameName = SCANNER.nextLine();
            if (isBackOrQuit(gameName)){
                return;
            }
            CreateGameRequest createGameRequest = new CreateGameRequest(authToken,gameName);
            serverFacade.createGame(createGameRequest);
            System.out.printf("Game Created: %s%n",gameName);
        } catch (AlreadyTakenException | URISyntaxException | IOException | InterruptedException | ResponseException e) {
            System.out.println("Game name already exists. Please join game or create different game name.");
        }
    }

    public void listGamesPrompt(){
        try{
            List<GameListFormat> gamesList = serverFacade.listGames();
            if (gamesList.isEmpty()){
                System.out.println("No active games. Please create a game.");
            }
            printGamesList(gamesList);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void printGamesList(List<GameListFormat> gamesList) {
        int counter = 1;
        gameIDmap.clear();
        for (GameListFormat game: gamesList){
            gameIDmap.put(counter,game.gameID());
            System.out.printf("%d. Game Name: %s%n",counter++,game.gameName());
            if(game.whiteUsername()==null){
                System.out.println("   White Player: Available");
            } else{
                System.out.printf("   White Player: %s%n",game.whiteUsername());
            }
            if (game.blackUsername()==null){
                System.out.println("   Black Player: Available");
            }
            else{
                System.out.printf("   Black Player: %s%n",game.blackUsername());
            }
        }
    }
//
//    private void updateGamesList(String color) throws ResponseException, URISyntaxException, IOException, InterruptedException {
//        List<GameListFormat> gamesList = serverFacade.listGames();
//        for (GameListFormat game: gamesList){
//            if(Objects.equals(color, "WHITE")){
//                game.whiteUsername=null;
//            }
//        }
//    }

    public void joinGamePrompt(){
        try{
            List<GameListFormat> gamesList = serverFacade.listGames();
            printGamesList(gamesList);
            if(gamesList.isEmpty()){
                System.out.println("No games have been created. Please create a game.");
                return;
            }
            System.out.print("Please Type Game Number You Would Like To Join >>> ");
            String input = SCANNER.nextLine();
            if (isBackOrQuit(input)){
                return;
            }
            int inputGameNum = Integer.parseInt(input);
            int gameID = gameIDmap.get(inputGameNum);
            System.out.print("Please Type Which Player Color You Would Like To Be: WHITE/BLACK >>> ");
            String playerColor = SCANNER.nextLine().toUpperCase();
            if (isBackOrQuit(playerColor)){
                return;
            }
            JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, playerColor,gameID);
            serverFacade.joinGame(joinGameRequest);
            System.out.printf("Successfully Joined Game #%d as %s Player.%n",inputGameNum,playerColor);
            ws.sendConnectMsg(authToken,gameID,playerColor);
            gamePlayPrompt(authToken,gameID,playerColor);
        } catch (AlreadyTakenException e) {
            System.out.println("Error: Player Color Unavailable. Please Choose Different Color.");
        }catch (BadRequestException e) {
            System.out.println("Error: Please Specify Player Color. Enter 'WHITE' or 'BLACK'.");
        }catch (Exception e) {
            System.out.println("Error: Game Not Found. Please Enter Number of Existing Game.");
            System.out.println("Tip: To see existing games, choose 'List Games' option.");
        }
    }

    public void observeGamePrompt(){
        try{
            List<GameListFormat> gamesList = serverFacade.listGames();
            printGamesList(gamesList);
            if(gamesList.isEmpty()){
                System.out.println("No games have been created. Please create a game.");
                return;
            }
            System.out.print("Please Type Game Number You Would Like To Join >>> ");
            String input = SCANNER.nextLine();
            if (isBackOrQuit(input)){
                return;
            }
            int inputGameNum = Integer.parseInt(input);
            int gameID = gameIDmap.get(inputGameNum);
            ws.sendConnectMsg(authToken,gameID,"OBSERVER");
            observeGamePlayPrompt(authToken,gameID,"OBSERVER");
//            ClientChessBoard.draw(loadGameMessage.getGameData().game().getBoard(), "WHITE");
        } catch (Exception e) {
            System.out.println("Error: Game Not Found. Please Enter Number of Existing Game.");
            System.out.println("Tip: To see existing games, choose 'List Games' option.");
        }
    }

    public void observeGamePlayPrompt(String authToken,int gameID, String playerType){
        while(true){
            System.out.println("Please Enter Number To Select Option:");
            System.out.println(" 1. Highlight Legal Moves");
            System.out.println(" 2. Redraw Chessboard");
            System.out.println(" 3. Leave Game");
            System.out.println(" 4. Help");
            System.out.print(">>> ");
            String input = SCANNER.nextLine();
            try {
                int option = Integer.parseInt(input);
                switch (option) {
                    case 1 -> System.out.println("highlighhtht");
                    case 2 -> WebSocketFacade.drawBoard(null,null);
                    case 3 -> {
                        ws.sendLeaveMsg(authToken,gameID,playerType);
                        return;
                    }
                    case 4 -> helpPrompt();
                    default -> System.out.println("Please enter an number 1-4: ");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number or type 'quit' to exit.");
            } catch (AlreadyTakenException e){
                System.out.println("Please choose diff username");
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void gamePlayPrompt(String authToken, int gameID, String playerType) {
        while (true) {
            System.out.println("Please Enter Number To Select Option:");
            System.out.println(" 1. Make Move");
            System.out.println(" 2. Highlight Legal Moves");
            System.out.println(" 3. Redraw Chessboard");
            System.out.println(" 4. Leave Game");
            System.out.println(" 5. Resign");
            System.out.println(" 6. Help");
            System.out.print(">>> ");
            String input = SCANNER.nextLine();
//            if (isBackOrQuit(input)) {
//                return;
//            }
            try {
                int option = Integer.parseInt(input);
                switch (option) {
                    case 1 -> System.out.println("You want to move?");
                    case 2 -> System.out.println("Lets get the highlighter :)");
                    case 3 -> WebSocketFacade.drawBoard(null,null);
                    case 4 -> {
                        ws.sendLeaveMsg(authToken,gameID,playerType);
                        return;
                    }
                    case 5 -> System.out.println("Resigning is worse");
                    case 6 -> helpPrompt();
                    default -> System.out.println("Please enter an number 1-6: ");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number or type 'quit' to exit.");
            } catch (AlreadyTakenException e){
                System.out.println("Please choose diff username");
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isBackOrQuit(String input){
        return input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("back");
    }


    @Override
    public void notify(NotificationMessage message) {
        System.out.println(message.getMessage());

    }

}
