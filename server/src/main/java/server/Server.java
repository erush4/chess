package server;

import com.google.gson.Gson;
import dataAccess.MemoryDataAccess;
import model.RegisterRequest;
import model.RegisterResult;
import model.ResponseException;
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
       Spark.exception(ResponseException.class, this::exceptionHandler);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object exceptionHandler(ResponseException e, Request request, Response response) {
        response.status(e.getStatusCode());
        String message = e.toJson();
        response.body(message);
        return message;
    }

    private Object register(Request request, Response response) throws ResponseException {
        RegisterRequest registerRequest= new Gson().fromJson(request.body(), RegisterRequest.class);

        RegisterResult result= service.register(registerRequest);
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
