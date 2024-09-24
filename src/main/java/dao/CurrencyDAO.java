package dao;

import utils.SQLiteConnector;
import exceptions.DataReadException;
import exceptions.DatabaseConnectionException;
import models.Currency;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAO implements DAO<Currency> {

    @Override
    public Optional<Currency> get(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<Currency> getAll() throws DatabaseConnectionException, DataReadException {
        String sql = "SELECT * FROM Currencies";

        List<Currency> currencies = new ArrayList<>();

        try (Connection connection = SQLiteConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Currency currency = new Currency();

                currency.setId(resultSet.getInt("id"));
                currency.setCode(resultSet.getString("code"));
                currency.setFullName(resultSet.getString("full_name"));
                currency.setSign(resultSet.getString("sign"));

                currencies.add(currency);
            }
        } catch (SQLException e) {
            throw new DataReadException();
        }

        return currencies;
    }

    @Override
    public void update(Currency item) {

    }

    @Override
    public void save(Currency item) {

    }
}
