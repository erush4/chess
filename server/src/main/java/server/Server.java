package server;

import com.google.gson.Gson;
import dataAccess.MemoryDataAccess;
import model.*;
import spark.*;
import service.Service;

public class Server {
    private final Service service;

    public Server(){
        this.service = new Service(new MemoryDataAccess());
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

       Spark.delete("/db", this::clear);
       Spark.post("/user", this::register);
       Spark.post("/session", this::login);
       Spark.delete("/session", this::logout);
       Spark.get("/game", this::listGames);
       Spark.post("/game", this::createGame);
       Spark.put("/game", this::joinGame);
       Spark.exception(ResponseException.class, this::exceptionHandler);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object joinGame(Request request, Response response) throws ResponseException {
        String authToken = new Gson().fromJson(request.headers("authorization"), String.class);
        JoinGameRequest joinRequest = new Gson().fromJson(request.body(), JoinGameRequest.class);
        service.joinGame(authToken, joinRequest);
        response.status(200);
        return "";
    }

    private Object createGame(Request request, Response response) throws ResponseException {
        String authToken = new Gson().fromJson(request.headers("authorization"), String.class);
        CreateGameRequest gameRequest = new Gson().fromJson(request.body(), CreateGameRequest.class);
        return new Gson().toJson(service.createGame(authToken, gameRequest));
    }

    private Object listGames(Request request, Response response) throws ResponseException{
        String authToken = new Gson().fromJson(request.headers("authorization"), String.class);
        ListGamesResponse listGames= service.listGames(authToken);
        return new Gson().toJson(listGames);
    }

    private Object logout(Request request, Response response) throws ResponseException {

        String authToken = new Gson().fromJson(request.headers("authorization"), String.class);
        service.logout(authToken);
        response.status(200);
        return "";
    }

    private Object login(Request request, Response response) throws ResponseException {
        LoginRequest loginRequest= new Gson().fromJson(request.body(), LoginRequest.class);
        LoginResponse result = service.login(loginRequest);
        return new Gson().toJson(result);
    }

    private Object exceptionHandler(ResponseException e, Request request, Response response) {
        response.status(e.getStatusCode());
        String message = e.toJson();
        response.body(message);
        return message;
    }

    private Object register(Request request, Response response) throws ResponseException {
        RegisterRequest registerRequest= new Gson().fromJson(request.body(), RegisterRequest.class);
        RegisterResponse result= service.register(registerRequest);
        return new Gson().toJson(result);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request request, Response response) throws ResponseException {
        service.clearData();
        response.status(200);
        return "";

    }
}
