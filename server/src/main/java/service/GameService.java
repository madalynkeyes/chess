package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.exceptions.*;
import dataaccess.GameDAO;
import model.GameData;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.LogoutOrListGamesRequest;
import service.responses.CreateGameResponse;
import service.responses.GameListFormat;
import service.responses.JoinClearLogoutResponse;
import service.responses.ListGamesResponse;


import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GameService extends Service {

    private static GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        super(authDAO);

        this.gameDAO = gameDAO;
        this.authDAO = authDAO;

    }

    /**
     * List Games Service Class
     * Take an auth token, if valid, return a list of current games.
     *
     * @param request list game request
     * @return list game response
     */
    public ListGamesResponse listGames(LogoutOrListGamesRequest request) throws ResponseException {
        hasAuthToken(request.authToken());
        List<GameListFormat> games = gameDAO.listGames();
        return new ListGamesResponse(games);
    }

    public CreateGameResponse createGame(CreateGameRequest request) throws ResponseException {
        String authToken = request.authToken();
        hasAuthToken(authToken);
        if (request.gameName() == null) {
            throw new BadRequestException("Error: game name not entered");
        }
        if (gameDAO.getGameByName(request.gameName()) != null) {
            throw new AlreadyTakenException("Error: game name already taken");
        }
        Random random = new Random();
        GameData gameData = new GameData(
                Math.abs(random.nextInt()),
                null,
                null,
                request.gameName(),
                new ChessGame()
        );
        gameDAO.createGame(gameData);
        return new CreateGameResponse(gameData.gameId());

    }

    /**
     * Join Game Service Class
     * Take an auth token and check if it is valid.
     * Take gameID and getGame by gameID. If gameID isn't valid, throw NotFoundException.
     * Take specified player color and check if it is available.
     * If it is, create new game with updated game information.
     *
     * @param request join game request
     * @return join game response
     */
    public JoinClearLogoutResponse joinGame(JoinGameRequest request)
            throws BadRequestException,AlreadyTakenException,NotFoundException, ResponseException {
        hasAuthToken(request.authToken());
        if (request.gameID() < 0) {
            throw new BadRequestException("Error: please enter gameID");
        }
        if (gameDAO.getGameByID(request.gameID()) == null) {
            throw new NotFoundException("Error: game not found");
        }
        GameData gameData = gameDAO.getGameByID(request.gameID());
        String username = authDAO.getUserByToken(request.authToken());
        GameData newGame = updateGameByColor(request, gameData, username);
        try {
            gameDAO.updateGame(newGame,username, request.playerColor());
        } catch (AlreadyTakenException e){
            throw new AlreadyTakenException("Error: unable to join game cuz color is already stolen");
        } catch (NotFoundException e){
            throw new NotFoundException("Error: game not found");
        }
        catch (BadRequestException e){
            throw new BadRequestException("Error: please enter valid gameID");
        }
        catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError,"Error: unable to join game");
        }

        return new JoinClearLogoutResponse("{}");
    }

    public static void leaveGame(int gameID, String username) throws ResponseException{
        GameData game = gameDAO.getGameByID(gameID);
        String color;
        if (username.equals(game.whiteUsername())){
            color = "WHITE";
        } else {color = "BLACK";}
        gameDAO.leaveUpdateGame(game,username,color);
    }

    /**
     * See if requested color already has a user in a specific game.
     * If color is available, create new game with username listed for specific color
     *
     * @param request  join game request
     * @param gameData current game info
     * @param username current player's username
     * @return new game data
     */
    private static GameData updateGameByColor(JoinGameRequest request, GameData gameData, String username) {
        GameData newGame;
        if (Objects.equals(request.playerColor(), "WHITE")) {
            if (!Objects.equals(gameData.whiteUsername(), null)) {
                throw new AlreadyTakenException("Error: color already taken");
            }
            newGame = new GameData(
                    gameData.gameId(),
                    username,
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game()

            );
            return newGame;

        } else if (Objects.equals(request.playerColor(), "BLACK")) {
            if (!Objects.equals(gameData.blackUsername(), null)) {
                throw new AlreadyTakenException("Error: color already taken");
            }
            newGame = new GameData(
                    gameData.gameId(),
                    gameData.whiteUsername(),
                    username,
                    gameData.gameName(),
                    gameData.game()

            );
            return newGame;
        }
        throw new BadRequestException("Error: please specify player color");
    }

}
