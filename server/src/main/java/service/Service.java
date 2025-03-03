package service;

import dataAccess.DataAccessException;
import dataAccess.DataAccess;

public class Service {
    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clearData() throws DataAccessException {
        dataAccess.clearData();
    }
}
