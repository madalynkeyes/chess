package service.responses;

//public record ListGamesResponse(java.util.Map<String, model.GameData> games) {
//}

import java.util.List;

public record ListGamesResponse(List<GameListFormat> games) {
}
