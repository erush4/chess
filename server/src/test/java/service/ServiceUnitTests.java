package service;

import dataAccess.DataAccessException;
import model.GameData;
import model.UserData;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import org.junit.jupiter.api.*;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.fail;

public class ServiceUnitTests {
    static Service service;
    static DataAccess dataAccess;
    static UserData existingUser;
    static UserData newUser;
    static GameData existingGame;
    static GameData newGame;

    @BeforeAll
    static void init() {
        dataAccess = new MemoryDataAccess();
        service = new Service(dataAccess);

        existingUser= new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");
    }
    @AfterEach
    public void reset(){
        try {
            dataAccess.clearData();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @BeforeEach
    public void setup(){
        try {
            dataAccess.createUser(existingUser);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Clear Removes All Data")
    void clearRemovesAllData() {
        try {
            service.clearData();
        } catch (DataAccessException e) {
            fail("test failed due to exception:" + e.getMessage());
        }
        assert (Objects.equals(dataAccess, new MemoryDataAccess()));
    }

    @Test
    @DisplayName("Login fails after clear")
    void clearFailsLogin(){
    //TODO
    }
}