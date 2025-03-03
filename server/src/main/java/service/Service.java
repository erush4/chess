package service;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.DataAccess;
import model.*;

import java.util.Objects;
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
            throw new ResponseException(500, "could not get data");
        }
    }

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clearData() throws ResponseException {
        try {
            dataAccess.clearData();
        } catch (DataAccessException e) {
            throw new ResponseException(500, "could not get data");
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
            UserData newUser = new UserData(request.username(), request.password(), request.email());
            dataAccess.createUser(newUser);
            authToken = createAuthData(request.username());
        } catch (DataAccessException e) {
            throw new ResponseException(500, "could not get data");
        }

        return new RegisterResponse(request.username(), authToken);
    }

    public LoginResponse login(LoginRequest request) throws ResponseException{
        UserData user;
        String authToken;
        if (request.username() == null || request.password() == null) {
            throw new ResponseException(400, "bad request");
        }
        try {
            user = dataAccess.getUser(request.username());
            if (user == null || !Objects.equals(user.password(), request.password())){
                throw new ResponseException(401, "unauthorized");
            }
            authToken = createAuthData(user.username());
        } catch (DataAccessException e) {
            throw new ResponseException(500, "could not get data");
        }

        return new LoginResponse(user.username(), authToken);
    }

    public void logout(String authToken) throws ResponseException {
        try{
            AuthData authData= verifyAuthData(authToken);
            dataAccess.deleteAuth(authData);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "could not get data");
        }
    }

    public ListGamesResponse listGames(String authToken) throws ResponseException{
        try{
            verifyAuthData(authToken);
            return new ListGamesResponse(dataAccess.listGames());
        } catch (DataAccessException e) {
            throw new ResponseException(500, "could not get data");
        }
    }

    public Integer createGame(String authToken, String gameName) throws ResponseException {
        if (gameName == null){
            throw new ResponseException(400, "bad request");
        }
        try{
            verifyAuthData(authToken);
            int gameID = UUID.randomUUID().hashCode();
            GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
            dataAccess.addGame(game);
            return gameID;
        } catch (DataAccessException e){
            throw new ResponseException(500, "could not get data");
        }
    }
}