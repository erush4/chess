package model;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;

public class ResponseException extends Exception {
    final private int statusCode;


    public ResponseException(int statusCode, String message) {
        super("Error: " + message);
        this.statusCode = statusCode;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage()));
    }
    public static ResponseException fromStream(InputStream stream, int status) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
        String message = map.get("message").toString().substring(7);
        return new ResponseException(status, message);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
