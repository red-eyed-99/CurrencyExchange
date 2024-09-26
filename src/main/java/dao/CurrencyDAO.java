package dao;

import exceptions.NotFoundException;
import datasource.SQLiteConnector;
import exceptions.QueryExecuteException;
import exceptions.DatabaseConnectionException;
import models.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO implements DAO<Currency> {

    @Override
    public Currency get(String code) throws
            DatabaseConnectionException, QueryExecuteException, NotFoundException {

        String sql = "SELECT * FROM Currencies WHERE code = ?";

        Currency currency = null;

        try (Connection connection = SQLiteConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, code);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    currency = new Currency();
                    currency.setId(resultSet.getInt("id"));
                    currency.setCode(resultSet.getString("code"));
                    currency.setName(resultSet.getString("full_name"));
                    currency.setSign(resultSet.getString("sign"));
                }
            }

            if (currency == null) {
                throw new NotFoundException("Currency with code " + code + " not found");
            }

        } catch (SQLException e) {
            throw new QueryExecuteException();
        }

        return currency;
    }

    @Override
    public List<Currency> getAll() throws
            DatabaseConnectionException, QueryExecuteException, NotFoundException {

        String sql = "SELECT * FROM Currencies";

        List<Currency> currencies = new ArrayList<>();

        try (Connection connection = SQLiteConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Currency currency = new Currency();

                currency.setId(resultSet.getInt("id"));
                currency.setCode(resultSet.getString("code"));
                currency.setName(resultSet.getString("full_name"));
                currency.setSign(resultSet.getString("sign"));

                currencies.add(currency);
            }

            if (currencies.isEmpty()) {
                throw new NotFoundException("Not found list of currencies");
            }

        } catch (SQLException e) {
            throw new QueryExecuteException();
        }

        return currencies;
    }

    @Override
    public void update(Currency item) {

    }

    @Override
    public void save(Currency currency) throws DatabaseConnectionException, QueryExecuteException {
        String sql = "INSERT INTO Currencies (code, full_name, sign) VALUES (?, ?, ?)";

        try (Connection connection = SQLiteConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getName());
            preparedStatement.setString(3, currency.getSign());

            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    currency.setId(resultSet.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new QueryExecuteException();
        }
    }

    public boolean checkExistence(String code) throws DatabaseConnectionException, QueryExecuteException {
        String sql = "SELECT * FROM Currencies WHERE code = ?";

        try (Connection connection = SQLiteConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, code);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            throw new QueryExecuteException();
        }
    }
}
