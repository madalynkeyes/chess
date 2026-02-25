package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.Exceptions.AlreadyTakenException;
import dataaccess.Exceptions.BadRequestException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import service.Requests.CreateGameRequest;
import service.Responses.CreateGameResponse;
import service.Responses.GameListFormat;
import service.Responses.ListGamesResponse;

import java.util.List;
import java.util.Random;

public class GameService extends Service {


    private final GameDAO gameDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        super(userDAO,authDAO,gameDAO);


        this.gameDAO = gameDAO;

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
        if (gameDAO.getGame(request.gameName())!=null){
            throw new AlreadyTakenException("Error: game name already taken");
        }
        Random random = new Random();
        GameData gameData = new GameData(
                random.nextInt(),
                "",
                "",
                request.gameName(),
                new ChessGame()
        );
        gameDAO.createGame(gameData);
        return new CreateGameResponse(gameData.gameId());

    }
}
