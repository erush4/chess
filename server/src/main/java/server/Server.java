package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import spark.*;
import service.Service;

public class Server {
    private final Service service;

    public Server(Service service){
        this.service = service;
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

    private Object clear(Request request, Response response) throws DataAccessException{
       service.clearData();
        response.status(200);
        return "";

    }
}
