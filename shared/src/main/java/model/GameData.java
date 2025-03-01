package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUserName, String gameName, ChessGame game) {
}
