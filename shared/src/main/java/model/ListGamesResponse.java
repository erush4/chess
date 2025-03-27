package model;

import java.util.List;
import java.util.Objects;

public record ListGamesResponse(List<GameData> games) {
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("Game List\n");
        for (GameData game : games){
            string.append("Name: ").append(game.gameName()).append("\n");
            string.append("\tID: ").append(game.gameID()).append("\n");
            string.append("\tWhite Player: ").append(nameCheck(game.whiteUsername())).append("\n");
            string.append("\tBlack Player: ").append(nameCheck(game.blackUsername())).append("\n\n");

        }
        return string.toString();
    }
    private String nameCheck(String name){
        return Objects.requireNonNullElse(name, "");
    }
}
