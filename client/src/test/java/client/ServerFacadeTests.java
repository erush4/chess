package client;

import model.LoginRequest;
import model.RegisterRequest;
import model.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

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

    @BeforeAll
    public static void init() {
        int portNumber = 3001;
        server = new Server();
        var port = server.run(portNumber);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new ServerFacade(Integer.toString(portNumber));

        String existingName = "existingUser";
        String password = "password";
        String newName = "newuser";
        String email = "email";

        loginExisting = new LoginRequest(existingName, password);
        loginNew = new LoginRequest(newName, password);
        registerExisting = new RegisterRequest(existingName, password, email);
        registerNew = new RegisterRequest(newName, password, email);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void setup() throws ResponseException {
        existingAuth = serverFacade.register(registerExisting).authToken();

    }

    @AfterEach
    void reset() throws ResponseException {
        serverFacade.clear();
    }

    @Test
    void clearRemovesAllData() {
        try {
            serverFacade.clear();
            Assertions.assertThrows(ResponseException.class, () -> serverFacade.listGames(existingAuth));
            Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(loginExisting));
//            existingAuth = serverFacade.register(registerExisting).authToken();
//            Assertions.assertNull(serverFacade.listGames(existingAuth));

        } catch (ResponseException e) {
            fail("test failed due to exception:" + e.getMessage());
        }


    }

}
