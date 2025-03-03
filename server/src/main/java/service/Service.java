package service;

import dataAccess.DataAccessException;
import dataAccess.DataAccess;
import model.*;
import java.util.UUID;

public class Service {
    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clearData() throws ResponseException {
        try {
            dataAccess.clearData();
        } catch (DataAccessException e) {
            throw new ResponseException(500, "something has gone terribly wrong");
        }
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        String authToken;
        UserData user;
        if (request.username() == null || request.email() == null || request.password() == null) {
            throw new ResponseException(400, "bad request");
        }
        try {
            user = dataAccess.getUser(request.username());
            if (user != null) {
                throw new ResponseException(403, "already taken");
            }
            UserData newUser = new UserData(request.username(), request.password(), request.email());
            dataAccess.createUser(newUser);
            authToken = UUID.randomUUID().toString();
            AuthData newAuthData = new AuthData(authToken, request.username());
            dataAccess.createAuth(newAuthData);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "something has gone terribly wrong");
        }
        return new RegisterResult(request.username(), authToken);
    }
}
