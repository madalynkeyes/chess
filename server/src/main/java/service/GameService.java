package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.Exceptions.AlreadyTakenException;
import dataaccess.Exceptions.BadRequestException;
import dataaccess.Exceptions.NotFoundException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.Requests.CreateGameRequest;
import service.Requests.JoinGameRequest;
import service.Responses.CreateGameResponse;
import service.Responses.GameListFormat;
import service.Responses.JoinGameResponse;
import service.Responses.ListGamesResponse;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GameService extends Service {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        super(userDAO,authDAO,gameDAO);

        this.gameDAO = gameDAO;
        this.authDAO = authDAO;

    }

    public ListGamesResponse listGames(String authToken){
        hasAuthToken(authToken);
        List<GameListFormat> games = gameDAO.listGames();
        return new ListGamesResponse(games);
    }

    public CreateGameResponse createGame(CreateGameRequest request){
        String authToken = request.authToken();
        hasAuthToken(authToken);
        if (request.gameName()==null){
            throw new BadRequestException("Error: game name not entered");
        }
        if (gameDAO.getGameByName(request.gameName())!=null){
            throw new AlreadyTakenException("Error: game name already taken");
        }
        Random random = new Random();
        GameData gameData = new GameData(
                Math.abs(random.nextInt()),
                "",
                "",
                request.gameName(),
                new ChessGame()
        );
        gameDAO.createGame(gameData);
        return new CreateGameResponse(gameData.gameId());

    }

    public JoinGameResponse joinGame(JoinGameRequest request){
        hasAuthToken(request.authToken());
        if (request.playerColor()==null || request.playerColor().equals("WHITE/BLACK")){
            throw new BadRequestException("Error: please specify player color");
        }
        if(request.gameID()<0){
            throw new BadRequestException("Error: please enter gameID");
        }
        if(gameDAO.getGameByID(request.gameID())==null){
            throw new NotFoundException("Error: game not found");
        }
        GameData gameData = gameDAO.getGameByID(request.gameID());
        String username = authDAO.getUserByToken(request.authToken());
        GameData newGame = updateGameByColor(request, gameData, username);
        gameDAO.createGame(newGame);

        return new JoinGameResponse("{}");
    }

    @NotNull
    private static GameData updateGameByColor(JoinGameRequest request, GameData gameData, String username) {
        GameData newGame;
        if(Objects.equals(request.playerColor(), "WHITE")){
            if(!Objects.equals(gameData.whiteUsername(), "")){
                throw new AlreadyTakenException("Error: color already taken");
            }
            newGame = new GameData(
                    gameData.gameId(),
                    username,
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game()

            );


        } else {
            if (!Objects.equals(gameData.blackUsername(), "")){
                throw new AlreadyTakenException("Error: color already taken");
            }
            newGame = new GameData(
                    gameData.gameId(),
                    gameData.whiteUsername(),
                    username,
                    gameData.gameName(),
                    gameData.game()

            );

        }
        return newGame;
    }
}
