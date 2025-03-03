package service;

import dataAccess.DataAccessException;
import model.*;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.fail;

public class ServiceUnitTests {
    static Service service;
    static DataAccess dataAccess;
    static UserData existingUser;
    static UserData newUser;

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
            service.register(new RegisterRequest(existingUser.username(), existingUser.password(), existingUser.email()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @DisplayName("Register Adds a User")
    void registerOneUser(){
        try{
            RegisterRequest request = new RegisterRequest(newUser.username(), newUser.password(), newUser.email());
            service.register(request);
            UserData thing = dataAccess.getUser("NewUser");
            Assertions.assertEquals(thing, newUser, "Response did not contain the same UserData as expected");
        } catch (Exception e) {
            fail("test failed due to exception" + e.getMessage());
        }
    }
    @Test
    @DisplayName("Cannot register same user twice")
    void registerTwice(){
        RegisterRequest request = new RegisterRequest(existingUser.username(), existingUser.password(), existingUser.email());
        Assertions.assertThrows(ResponseException.class, () -> service.register(request), "Did not throw an exception");
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

    @Test
    @DisplayName("Login fails after clear")
    void clearFailsLogin(){
    //TODO
    }
}