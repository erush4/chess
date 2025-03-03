package model;

import com.google.gson.Gson;

import java.util.Map;

public class ResponseException extends RuntimeException {
    final private int statusCode;


    public ResponseException(int statusCode, String message) {
        super("Error: " + message);
        this.statusCode= statusCode;
    }

    public String toJson(){
        return new Gson().toJson(Map.of("message", getMessage()));
    }

    public int getStatusCode() {
        return statusCode;
    }
}
