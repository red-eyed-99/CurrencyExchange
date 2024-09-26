package datasource;

import exceptions.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnector {
    private static final String USER = "";
    private static final String PASSWORD = "";
    private static final String URL = "jdbc:sqlite:D:/Java Projects/CurrencyExchange/src/main/resources/currency_exchange.db";

    private static final String DRIVER = "org.sqlite.JDBC";

    public static Connection getConnection() throws DatabaseConnectionException {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException | SQLException e) {
            throw new DatabaseConnectionException();
        }
    }
}
