package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.ErrorResult;
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

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request request, Response response) {
        try {
            service.clearData();
        } catch (DataAccessException e) {
            response.status(500);
            return new Gson().toJson(new ErrorResult(e.getMessage()));
        }
        response.status(200);
        return "";

    }
}
