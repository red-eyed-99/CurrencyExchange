package datasource;

import exceptions.DatabaseConnectionException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteConnector {
    private static final DataSource DATA_SOURCE;

    static {
        InitialContext initialContext;
        try {
            initialContext = new InitialContext();
            DATA_SOURCE = (DataSource) initialContext.lookup("java:comp/env/jdbc/CurrencyExchange");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private SQLiteConnector() {}

    public static Connection getConnection() throws DatabaseConnectionException {
        try {
            return DATA_SOURCE.getConnection();
        } catch (SQLException e) {
            throw new DatabaseConnectionException();
        }
    }
}
