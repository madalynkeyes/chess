package service.Responses;

//public record ListGamesResponse(java.util.Map<String, model.GameData> games) {
//}

import java.util.List;

public record ListGamesResponse(List<GameListFormat> games) {
}
