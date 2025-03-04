package service;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.fail;

public class ServiceUnitTests {
    static Service service;
    static DataAccess dataAccess;
    static UserData existingUser;
    static UserData newUser;
    static GameData existingGame;

    private static String loginTestUser(UserData user) throws ResponseException {
        LoginRequest request = new LoginRequest(user.username(), user.password());
        LoginResponse response = service.login(request);
        return response.authToken();
    }

    @BeforeAll
    static void init() {
        dataAccess = new MemoryDataAccess();
        service = new Service(dataAccess);

        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");
        existingGame = new GameData(-1, null, null, "existingGame", new ChessGame());
    }

    @AfterEach
    public void reset() {
        try {
            dataAccess.clearData();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @BeforeEach
    public void setup() {
        try {
            service.register(new RegisterRequest(existingUser.username(), existingUser.password(), existingUser.email()));
            dataAccess.addGame(existingGame);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Register Adds a User")
    void registerOneUser() {
        try {
            RegisterRequest request = new RegisterRequest(newUser.username(), newUser.password(), newUser.email());
            service.register(request);
            UserData thing = dataAccess.getUser("NewUser");
            Assertions.assertEquals(thing, newUser, "Response did not contain the same UserData as expected");
        } catch (Exception e) {
            fail("test failed due to exception" + e.getMessage());
        }
    }

    @Test
    @DisplayName("Register With Bad Request")
    void registerBadRequest() {
        RegisterRequest request = new RegisterRequest(newUser.username(), newUser.password(), null);
        Assertions.assertThrows(ResponseException.class, () -> service.register(request), "Did not throw an exception");
    }

    @Test
    @DisplayName("Prevent Registering Twice")
    void registerTwice() {
        RegisterRequest request = new RegisterRequest(existingUser.username(), existingUser.password(), existingUser.email());
        Assertions.assertThrows(ResponseException.class, () -> service.register(request), "Did not throw an exception");
    }

    @Test
    @DisplayName("Login Existing User Succeeds")
    void loginExistingUser() {
        LoginRequest request = new LoginRequest(existingUser.username(), existingUser.password());
        LoginResponse response = null;
        try {
            response = service.login(request);
        } catch (ResponseException e) {
            fail("test failed due to exception" + e.getMessage());
        }
        Assertions.assertNotNull(response.authToken());
        Assertions.assertEquals(response.username(), existingUser.username());
    }

    @Test
    @DisplayName("Login Fails With Bad Request")
    void loginBadRequest() {
        LoginRequest request = new LoginRequest(existingUser.username(), null);
        Assertions.assertThrows(ResponseException.class, () -> service.login(request));
    }

    @Test
    @DisplayName("Login Existing User With Bad Password")
    void loginBadPassword() {
        LoginRequest request = new LoginRequest(existingUser.username(), "the wrong password");
        Assertions.assertThrows(ResponseException.class, () -> service.login(request));
    }

    @Test
    @DisplayName("Login Nonexistent User Fails")
    void loginNonexistentUser() {
        LoginRequest request = new LoginRequest(newUser.username(), newUser.password());
        Assertions.assertThrows(ResponseException.class, () -> service.login(request), "exception not thrown");
    }

    @Test
    @DisplayName("Logout User Succeeds")
    void logoutValidUser() {
        String authToken = null;
        try {
            authToken = loginTestUser(existingUser);
        } catch (ResponseException e) {
            fail("test failed due to exception" + e.getMessage());
        }
        String finalAuthToken = authToken;
        Assertions.assertDoesNotThrow(() -> service.logout(finalAuthToken), "error thrown");

    }

    @Test
    @DisplayName("Logout With Bad Auth Fails")
    void logoutBadUser() {
        Assertions.assertThrows(ResponseException.class, () -> service.logout("bad token"), "exception not thrown");
    }

    @Test
    @DisplayName("List Games Returns List")
    void listGamesValid() {
        try {
            String authToken = loginTestUser(existingUser);
            Assertions.assertEquals(service.listGames(authToken), new ListGamesResponse(dataAccess.listGames()), "game lists not equal");
        } catch (Exception e) {
            fail("test failed due to exception:" + e.getMessage());
        }
    }

    @Test
    @DisplayName("List Games Rejects Invalid Auth")
    void listGamesBadToken() {
        Assertions.assertThrows(ResponseException.class, () -> service.listGames("bad token"), "error not thrown");
    }

    @Test
    @DisplayName("Create Game Makes Game")
    void createValidGame() {
        try {
            String authToken = loginTestUser(existingUser);
            CreateGameResponse createGameResponse = service.createGame(authToken, new CreateGameRequest("gameName"));
            Assertions.assertNotNull(createGameResponse, "game response returned null");
        } catch (ResponseException e) {
            fail("test failed due to exception:" + e.getMessage());
        }
    }

    @Test
    @DisplayName("Create Game Without Name Fails")
    void createGameNoName() {
        try {
            String authToken = loginTestUser(existingUser);
            Assertions.assertThrows(ResponseException.class, () -> service.createGame(authToken, new CreateGameRequest(null)));
        } catch (ResponseException e) {
            fail("test failed due to exception:" + e.getMessage());
        }
    }

    @Test
    @DisplayName("Create Game With Bad Auth Fails")
    void createGameBadAuth() {
        Assertions.assertThrows(ResponseException.class, () -> service.createGame("bad auth", new CreateGameRequest("gameName")));
    }

    @Test
    @DisplayName("Join Game Passes When Valid")
    void joinGameValid() {
        try {
            String authToken = loginTestUser(existingUser);
            JoinGameRequest joinRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, -1);
            service.joinGame(authToken, joinRequest);
            service.register(new RegisterRequest(newUser.username(), newUser.password(), newUser.email()));
            authToken = loginTestUser(newUser);
            joinRequest = new JoinGameRequest(ChessGame.TeamColor.BLACK, -1);
            service.joinGame(authToken, joinRequest);
            GameData actual = dataAccess.getGame(-1);
            GameData expected = new GameData(-1, existingUser.username(), newUser.username(), "existingGame", existingGame.game());
            Assertions.assertEquals(actual, expected);
        } catch (Exception e) {
            fail("test failed due to exception:" + e.getMessage());
        }
    }

    @Test
    @DisplayName("Join Game Fails without Auth")
    void joinGameBadAuth() {
        try {
            String authToken = "bad Auth";
            JoinGameRequest joinRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, -1);
            Assertions.assertThrows(ResponseException.class, () -> service.joinGame(authToken, joinRequest));
        } catch (Exception e) {
            fail("test failed due to exception:" + e.getMessage());
        }
    }

    @Test
    @DisplayName("Join Game Rejects Two White Teams")
    void joinGameSameTeam() {
        try {
            String authToken = loginTestUser(existingUser);
            JoinGameRequest joinRequest = new JoinGameRequest(ChessGame.TeamColor.BLACK, -1);
            service.joinGame(authToken, joinRequest);
            service.register(new RegisterRequest(newUser.username(), newUser.password(), newUser.email()));
            authToken = loginTestUser(newUser);
            joinRequest = new JoinGameRequest(ChessGame.TeamColor.BLACK, -1);
            String finalAuthToken = authToken;
            JoinGameRequest finalJoinRequest = joinRequest;
            Assertions.assertThrows(ResponseException.class, () -> service.joinGame(finalAuthToken, finalJoinRequest));
        } catch (Exception e) {
            fail("test failed due to exception:" + e.getMessage());
        }
    }

    @Test
    @DisplayName("Join Game Fails with Bad Request")
    void joinGameNullColor() {
        try {
            String authToken = loginTestUser(existingUser);
            JoinGameRequest joinRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, 0);
            JoinGameRequest finalJoinRequest1 = joinRequest;
            Assertions.assertThrows(ResponseException.class, () -> service.joinGame(authToken, finalJoinRequest1));
            joinRequest = new JoinGameRequest(null, 0);
            JoinGameRequest finalJoinRequest = joinRequest;
            Assertions.assertThrows(ResponseException.class, () -> service.joinGame(authToken, finalJoinRequest));
        } catch (Exception e) {
            fail("test failed due to exception:" + e.getMessage());
        }
    }

    @Test
    @DisplayName("Clear Removes All Data")
    void clearRemovesAllData() {
        try {
            service.clearData();
        } catch (ResponseException e) {
            fail("test failed due to exception:" + e.getMessage());
        }
        Assertions.assertEquals(new MemoryDataAccess(), dataAccess, "Response still contained data");
    }
}