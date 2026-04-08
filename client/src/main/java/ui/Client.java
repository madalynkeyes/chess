package ui;

import chess.ChessMove;
import chess.ChessPosition;
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
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.io.PrintStream;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ui.EscapeSequences.*;

public class Client implements NotificationHandler {
    private static final Scanner SCANNER = new Scanner(System.in);
    public final ServerFacade serverFacade;
    private final WebSocketFacade ws;
    private final Map<Integer,Integer> gameIDmap = new HashMap<>();
    private String authToken;

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
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("Error: Please enter a number or type 'quit' to exit.");
                System.out.print(RESET_TEXT_COLOR);
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
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Error: Username or Password incorrect. Please try again or register an account.");
            System.out.print(RESET_TEXT_COLOR);
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
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Error: username already taken. Please login or choose a different username.");
            System.out.print(RESET_TEXT_COLOR);
            return false;
        } catch (BadRequestException e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Error: invalid input.");
            System.out.print(RESET_TEXT_COLOR);
            return false;
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Error: " + e.getMessage());
            System.out.print(RESET_TEXT_COLOR);
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
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("Please enter a number or type 'quit' to exit.");
                System.out.print(RESET_TEXT_COLOR);
            } catch (AlreadyTakenException e){
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("Please choose different username");
                System.out.print(RESET_TEXT_COLOR);
            }
        }
    }


    public void logoutPrompt(){
        try {
            serverFacade.logout();
            System.out.println("You have logged out.");
        } catch(Exception e){
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Log out failed.");
            System.out.print(RESET_TEXT_COLOR);
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
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Game name already exists. Please join game or create different game name.");
            System.out.print(RESET_TEXT_COLOR);
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
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println(e.getMessage());
            System.out.print(RESET_TEXT_COLOR);
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
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Error: Player Color Unavailable. Please Choose Different Color.");
            System.out.print(RESET_TEXT_COLOR);
        }catch (BadRequestException e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Error: Please Specify Player Color. Enter 'WHITE' or 'BLACK'.");
            System.out.print(RESET_TEXT_COLOR);
        }catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Error: Game Not Found. Please Enter Number of Existing Game.");
            System.out.println("Tip: To see existing games, choose 'List Games' option.");
            System.out.print(RESET_TEXT_COLOR);
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
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Error: Game Not Found. Please Enter Number of Existing Game.");
            System.out.println("Tip: To see existing games, choose 'List Games' option.");
            System.out.print(RESET_TEXT_COLOR);
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
                    case 1 -> highlightPrompt("WHITE");
                    case 2 -> WebSocketFacade.drawBoard(null,null);
                    case 3 -> {
                        ws.sendLeaveMsg(authToken,gameID,playerType);
                        return;
                    }
                    case 4 -> helpObservePrompt();
                    default -> System.out.println("Please enter an number 1-4: ");
                }
            } catch (NumberFormatException e) {
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("Error: Please enter a number or type 'quit' to exit.");
                System.out.print(RESET_TEXT_COLOR);
            } catch (AlreadyTakenException e){
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("Error: Please choose different username");
                System.out.print(RESET_TEXT_COLOR);
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void helpObservePrompt() {
        System.out.println("Tips: Please enter a number for the option you would like to choose.");
        System.out.println("Type the number and then press the 'enter' key on your keyboard.");
        System.out.println("To view legal moves of a piece, choose '2' and then type location of piece (i.e. 'a4')");
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
                    case 1 -> makeMovePrompt(gameID);
                    case 2 -> highlightPrompt(playerType);
                    case 3 -> WebSocketFacade.drawBoard(null,null);
                    case 4 -> {
                        ws.sendLeaveMsg(authToken,gameID,playerType);
                        return;
                    }
                    case 5 -> resignPrompt(authToken,gameID,playerType);
                    case 6 -> helpPlayPrompt();
                    default -> System.out.println("Please enter an number 1-6: ");
                }
            } catch (NumberFormatException e) {
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("Error: Please enter a number or type 'quit' to exit.");
                System.out.print(RESET_TEXT_COLOR);
            } catch (AlreadyTakenException e){
                System.out.print(SET_TEXT_COLOR_RED);
                System.out.println("Error: Please choose different username");
                System.out.print(RESET_TEXT_COLOR);
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void resignPrompt(String authToken, int gameID, String playerType) throws ResponseException {
        System.out.println("Are you sure you want to resign? Please type 'yes' or 'no'");
        String input = SCANNER.nextLine();
        if(input.equalsIgnoreCase("yes")) {
            ws.sendResignMsg(authToken, gameID, playerType);
        }
    }

    private void helpPlayPrompt() {
        System.out.println("Tips: Please enter a number for the option you would like to choose.");
        System.out.println("Type the number and then press the 'enter' key on your keyboard.");
        System.out.println("To make a move, choose '1' and then type start and end position of the piece (i.e. 'a2b3')");
        System.out.println("To view legal moves of a piece, choose '2' and then type location of piece (i.e. 'a4')");
    }

    private void highlightPrompt(String playerType) {
        System.out.println("What piece would you like to see the legal moves for?");
        try {
            String pieceLocation = SCANNER.nextLine().toUpperCase();
            ChessPosition startPos = translateToChessPos(pieceLocation);
            ws.highlightMoves(startPos, playerType);
        } catch (Exception e){
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Error: Please enter valid piece location (i.e. 'e6')");
            System.out.print(RESET_TEXT_COLOR);
        }

    }

    private ChessPosition translateToChessPos(String pieceLocation) {
        List<Integer>positions = new ArrayList<>();
        for (char c: pieceLocation.toCharArray()) {
            if (Character.isLetter(c)) {
                int value = c - 'A' + 1;
                positions.add(value);
            } else if (Character.isDigit(c)) {
                positions.add(Character.getNumericValue(c));
            } else {
                throw new BadRequestException("Error: that square doesn't have a piece.'");
            }
        }
        return new ChessPosition(positions.get(1), positions.get(0));

    }


    private void makeMovePrompt(int gameID) throws ResponseException {
        System.out.println("What move would you like to make?");
        try{
        String inputMove = SCANNER.nextLine().toUpperCase();
        ChessMove move = translateToChessMove(inputMove);
//        System.out.println(move);
        ws.sendMoveMsg(authToken,gameID,move);}
        catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println("Error: Please enter valid move (i.e. 'a2a3')");
            System.out.print(RESET_TEXT_COLOR);
        }
        //done: right now my error messages show that making a move out of turn / trying to move opponent piece produces the same error. Don't quite know how to fix lol.
        //done-ish: fix UI so the menu doesn't print before board and mess things up
        //done: highlight legal moves (for player and observer)
        //done: resign & end game so no more moves can be made
        //done: revise help menu
        //done: when in check or resigned, it should say player's name
        //done: if move results in check, checkmate or stalemate the server sends a notification to all clients
        //TODO: check for anything else in spec and check for bugs
        //TODO: run websocket tests
        //TODO: implement pawn promotions
        //done: resigning should require a confirmation and does not kick players from the game
    }

    private ChessMove translateToChessMove(String inputMove) {
        List<Integer>positions = new ArrayList<>();
        for (char c: inputMove.toCharArray()){
            if (Character.isLetter(c)){
                int value = c - 'A'+1;
                positions.add(value);
            } else if (Character.isDigit(c)) {
                positions.add(Character.getNumericValue(c));
            } else{
               throw new BadRequestException("Error: not valid move. Please enter move like 'c3d4'");
            }
        }
        ChessPosition startPos = new ChessPosition(positions.get(1), positions.get(0));
        ChessPosition endPos = new ChessPosition(positions.get(3),positions.get(2));
        return new ChessMove(startPos,endPos,null);
    }

    public boolean isBackOrQuit(String input){
        return input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("back");
    }


    @Override
    public void notify(NotificationMessage notificationMessage) {
        System.out.println(notificationMessage.getMessage());
    }


}
