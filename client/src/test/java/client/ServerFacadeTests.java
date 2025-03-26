package client;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.fail;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static String existingAuth;
    private static String newAuth;
    private static LoginRequest loginExisting;
    private static LoginRequest loginNew;
    private static RegisterRequest registerExisting;
    private static RegisterRequest registerNew;
    private static CreateGameRequest createGame;
    private static JoinGameRequest joinRequest;
    private static int existingGameID;
    private static CreateGameRequest createNewGame;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new ServerFacade(Integer.toString(port));

        String existingName = "existingUser";
        String password = "password";
        String newName = "newuser";
        String email = "email";

        loginExisting = new LoginRequest(existingName, password);
        loginNew = new LoginRequest(newName, password);
        registerExisting = new RegisterRequest(existingName, password, email);
        registerNew = new RegisterRequest(newName, password, email);
        createGame = new CreateGameRequest("game name");
        createNewGame = new CreateGameRequest("newGame");
    }

    @AfterAll
    static void stopServer() throws ResponseException {
        serverFacade.clear();
        server.stop();
    }

    @BeforeEach
    void setup() throws ResponseException {
        existingAuth = serverFacade.register(registerExisting).authToken();
        existingGameID = serverFacade.createGame(createGame, existingAuth).gameID();
    }

    @AfterEach
    void reset() throws ResponseException {
        serverFacade.clear();
    }

    @Test
    @DisplayName("Login Succeeds With Valid Input")
    void loginSucceeds() {
        try {
            serverFacade.logout(existingAuth);
            Assertions.assertDoesNotThrow(() -> existingAuth = serverFacade.login(loginExisting).authToken());
            Assertions.assertNotNull(existingAuth);
        } catch (ResponseException e) {
            fail("test failed due to exception:" + e.getMessage());
        }
    }

    @Test
    @DisplayName("Login Fails With Unregistered User")
    void loginFails() {
        newAuth = null;
        Assertions.assertThrows(ResponseException.class, () -> newAuth = serverFacade.login(loginNew).authToken());
        Assertions.assertNull(newAuth);
    }

    @Test
    @DisplayName("Register Succeeds With Valid Input")
    void registerSucceeds() {
        Assertions.assertDoesNotThrow(() -> newAuth = serverFacade.register(registerNew).authToken());
        Assertions.assertNotNull(newAuth);
    }

    @Test
    @DisplayName("Register Fails With Pre-Existing User")
    void registerFails() {
        Assertions.assertThrows(ResponseException.class, () -> newAuth = serverFacade.register(registerExisting).authToken());
        Assertions.assertNull(newAuth);
    }

    @Test
    @DisplayName("Logout Succeeds And Prevents Further Access")
    void logoutSucceeds() {
        Assertions.assertDoesNotThrow(() -> existingAuth = serverFacade.login(loginExisting).authToken());
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(existingAuth));
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.listGames(existingAuth));
    }

    @Test
    @DisplayName("Logout Fails If Authtoken Doesn't Exist")
    void logoutFails() {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.logout("not an authtoken"));
    }

    @Test
    @DisplayName("CreateGame Succeeds")
    void createGameSucceeds(){
        Assertions.assertDoesNotThrow(()-> serverFacade.createGame(createNewGame, existingAuth));
    }

    @Test
    @DisplayName("CreateGame Fails With Bad Input")
    void createGameFails(){
        Assertions.assertThrows(
                ResponseException.class,
                ()-> serverFacade.createGame(new CreateGameRequest(null), existingAuth)
        );
    }

    @Test
    @DisplayName("ListGames Succeeds with Valid Input")
    void listGamesSucceeds() {
        try {
            Assertions.assertEquals(1, serverFacade.listGames(existingAuth).games().size());
        } catch (ResponseException e) {
            fail("test failed due to exception:" + e.getMessage());
        }
    }

    @Test
    @DisplayName("ListGames Fails With Bad Auth")
    void ListGamesFails() {
        Assertions.assertThrows(ResponseException.class, () -> newAuth = serverFacade.register(registerExisting).authToken());
        Assertions.assertNull(newAuth);
    }

    @Test
    @DisplayName("JoinGame Succeeds With Valid Input")
    void joinGameSucceeds() {
        joinRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, existingGameID);
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(joinRequest, existingAuth));
    }

    @Test
    @DisplayName("joinGame Fails If Already Taken")
    void joinGameFails() {
        try {
            joinRequest = new JoinGameRequest(ChessGame.TeamColor.BLACK, existingGameID);
            serverFacade.joinGame(joinRequest, existingAuth);
            newAuth = serverFacade.register(registerNew).authToken();
            Assertions.assertThrows(ResponseException.class, () -> serverFacade.joinGame(joinRequest, newAuth));
        } catch (ResponseException e) {
            fail("test failed due to exception:" + e.getMessage());
        }

    }

    @Test
    @DisplayName("Clear Removes All Data")
    void clearRemovesAllData() {
        try {
            serverFacade.clear();
            Assertions.assertThrows(ResponseException.class, () -> serverFacade.listGames(existingAuth));
            Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(loginExisting));
            existingAuth = serverFacade.register(registerExisting).authToken();
            Assertions.assertEquals(new ArrayList<>(), serverFacade.listGames(existingAuth).games());
        } catch (ResponseException e) {
            fail("test failed due to exception:" + e.getMessage());
        }


    }

}
