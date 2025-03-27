package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class Service {
    private final DataAccess dataAccess;

    private String createAuthData(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData newAuthData = new AuthData(authToken, username);
        dataAccess.createAuth(newAuthData);
        return authToken;
    }

    private AuthData verifyAuthData(String authToken) throws ResponseException {
        try {
            AuthData authData = dataAccess.getAuth(authToken);
            if (authData == null) {
                throw new ResponseException(401, "unauthorized");
            }
            return authData;
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clearData() throws ResponseException {
        try {
            dataAccess.clearData();
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public RegisterResponse register(RegisterRequest request) throws ResponseException {
        UserData user;
        String authToken;

        try {
            if (request.username() == null || request.email() == null || request.password() == null) {
                throw new ResponseException(400, "bad request");
            }
            user = dataAccess.getUser(request.username());
            if (user != null) {
                throw new ResponseException(403, "already taken");
            }
            String hashPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
            UserData newUser = new UserData(request.username(), hashPassword, request.email());
            dataAccess.createUser(newUser);
            authToken = createAuthData(request.username());
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }

        return new RegisterResponse(request.username(), authToken);
    }

    public LoginResponse login(LoginRequest request) throws ResponseException {
        UserData user;
        String authToken;
        if (request.username() == null || request.password() == null) {
            throw new ResponseException(400, "bad request");
        }
        try {
            user = dataAccess.getUser(request.username());
            if (user == null || !BCrypt.checkpw(request.password(), user.password())) {
                throw new ResponseException(401, "unauthorized");
            }
            authToken = createAuthData(user.username());
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }

        return new LoginResponse(user.username(), authToken);
    }

    public void logout(String authToken) throws ResponseException {
        try {
            AuthData authData = verifyAuthData(authToken);
            dataAccess.deleteAuth(authData);
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public ListGamesResponse listGames(String authToken) throws ResponseException {
        try {
            verifyAuthData(authToken);
            return new ListGamesResponse(dataAccess.listGames());
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public CreateGameResponse createGame(String authToken, CreateGameRequest createGameRequest) throws ResponseException {
        String gameName = createGameRequest.gameName();
        if (gameName == null) {
            throw new ResponseException(400, "bad request");
        }
        try {
            verifyAuthData(authToken);
            int gameID =  Math.abs(UUID.randomUUID().hashCode());
            GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
            dataAccess.addGame(game);
            return new CreateGameResponse(gameID);
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void joinGame(String authToken, JoinGameRequest joinRequest) throws ResponseException {
        try {
            AuthData authData = verifyAuthData(authToken);
            int gameID = joinRequest.gameID();
            GameData game = dataAccess.getGame(gameID);
            ChessGame.TeamColor teamColor = joinRequest.playerColor();
            String userName = authData.username();
            GameData newGameData;
            if (game == null || joinRequest.playerColor() == null) {
                throw new ResponseException(400, "bad request");
            }
            newGameData = switch (teamColor) {
                case WHITE -> {
                    if (game.whiteUsername() != null) {
                        throw new ResponseException(403, "already taken");
                    }
                    yield new GameData(gameID, userName, game.blackUsername(), game.gameName(), game.game());
                }
                case BLACK -> {
                    if (game.blackUsername() != null) {
                        throw new ResponseException(403, "already taken");
                    }
                    yield new GameData(gameID, game.whiteUsername(), userName, game.gameName(), game.game());
                }
            };
            dataAccess.updateGame(newGameData);
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}