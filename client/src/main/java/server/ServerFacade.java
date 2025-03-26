package server;

import com.google.gson.Gson;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String port) {
        serverUrl = "http://localhost:" + port;
    }

    private static void writeBody(Object requestObject, String authtoken, HttpURLConnection http) throws IOException {
        if (authtoken != null) {
            http.addRequestProperty("authorization", authtoken);
        }
        if (requestObject != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(requestObject);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }



    }

    public CreateGameResponse createGame(CreateGameRequest gameRequest, String authtoken) throws ResponseException {
        return this.request("POST", "/game", gameRequest, authtoken, CreateGameResponse.class);
    }

    public void joinGame(JoinGameRequest joinRequest, String authtoken) throws ResponseException {
        this.request("PUT", "/game", joinRequest, authtoken, null);
    }

    public ListGamesResponse listGames(String authtoken) throws ResponseException {
        return this.request("GET", "/game", null, authtoken, ListGamesResponse.class);
    }

    public LoginResponse login(LoginRequest loginRequest) throws ResponseException {
        return this.request("POST", "/session", loginRequest, null, LoginResponse.class);
    }

    public RegisterResponse register(RegisterRequest registerRequest) throws ResponseException {
        return this.request("POST", "/user", registerRequest, null, RegisterResponse.class);
    }

    public void logout(String authtoken) throws ResponseException {
        this.request("DELETE", "/session", null, authtoken, null);
    }

    public void clear() throws ResponseException {
        this.request("DELETE", "/db", null, null, null);
    }

    private <T> T request(String method, String path, Object requestObject, String authtoken, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(requestObject, authtoken, http);
            http.connect();

            int status = http.getResponseCode();
            if (status != 200) {
                try (InputStream respError = http.getErrorStream()) {
                    if (respError != null) {
                        throw ResponseException.fromStream(respError, status);
                    }
                }
            }

            T response = null;
            if (http.getContentLength() < 0) {
                try (InputStream respBody = http.getInputStream()) {
                    InputStreamReader reader = new InputStreamReader(respBody);
                    if (responseClass != null) {
                        response = new Gson().fromJson(reader, responseClass);
                    }
                }
            }
            return response;
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
