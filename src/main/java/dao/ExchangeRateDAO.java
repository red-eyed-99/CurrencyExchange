package dao;

import datasource.SQLiteConnector;
import exceptions.DatabaseConnectionException;
import exceptions.NotFoundException;
import exceptions.QueryExecuteException;
import models.Currency;
import models.CurrencyPair;
import models.ExchangeRate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAO implements DAO<ExchangeRate, CurrencyPair> {

    @Override
    public ExchangeRate get(CurrencyPair currencyPair)
            throws DatabaseConnectionException, QueryExecuteException, NotFoundException {

        String sql = "SELECT * FROM ExchangeRates WHERE base_currency_id = ? AND target_currency_id = ?";

        Currency baseCurrency = currencyPair.getBaseCurrency();
        Currency targetCurrency = currencyPair.getTargetCurrency();

        ExchangeRate exchangeRate = null;

        try (Connection connection = SQLiteConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, baseCurrency.getId());
            preparedStatement.setInt(2, targetCurrency.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    exchangeRate = getExchangeRate(resultSet, baseCurrency, targetCurrency);
                }
            }

            if (exchangeRate == null) {
                throw new NotFoundException("Exchange rate with code " +
                        baseCurrency.getCode() + targetCurrency.getCode() +
                        " not found");
            }

        } catch (SQLException e) {
            throw new QueryExecuteException();
        }

        return exchangeRate;
    }

    @Override
    public List<ExchangeRate> getAll() throws DatabaseConnectionException, QueryExecuteException, NotFoundException {
        String sql = "SELECT * FROM ExchangeRates, Currencies " +
                "WHERE ExchangeRates.target_currency_id = Currencies.id";

        List<ExchangeRate> exchangeRates = new ArrayList<>();

        try (Connection connection = SQLiteConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Currency baseCurrency = getCurrencyById(resultSet.getInt("base_currency_id"));
                Currency targetCurrency = getCurrencyById(resultSet.getInt("target_currency_id"));

                ExchangeRate exchangeRate = getExchangeRate(resultSet, baseCurrency, targetCurrency);

                exchangeRates.add(exchangeRate);
            }

            if (exchangeRates.isEmpty()) {
                throw new NotFoundException("Not found list of exchangeRates");
            }

        } catch (SQLException e) {
            throw new QueryExecuteException();
        }

        return exchangeRates;
    }

    private ExchangeRate getExchangeRate(ResultSet resultSet,
                                         Currency baseCurrency,
                                         Currency targetCurrency) throws SQLException {
        ExchangeRate exchangeRate = new ExchangeRate();

        exchangeRate.setId(resultSet.getInt("id"));
        exchangeRate.setBaseCurrency(baseCurrency);
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRate.setRate(resultSet.getBigDecimal("rate"));

        return exchangeRate;
    }

    public Currency getCurrencyById(int id)
            throws DatabaseConnectionException, QueryExecuteException, NotFoundException {

        String sql = "SELECT * FROM Currencies WHERE id = ?";

        Currency currency = null;

        try (Connection connection = SQLiteConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

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
                throw new NotFoundException("Currency with id " + id + " not found");
            }

        } catch (SQLException e) {
            throw new QueryExecuteException();
        }

        return currency;
    }

    @Override
    public void save(ExchangeRate exchangeRate) throws DatabaseConnectionException, QueryExecuteException {
        String sql = "INSERT INTO ExchangeRates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";

        try (Connection connection = SQLiteConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());

            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    exchangeRate.setId(resultSet.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new QueryExecuteException();
        }
    }

    public void update(ExchangeRate exchangeRate) throws DatabaseConnectionException, QueryExecuteException {
        String sql = "UPDATE ExchangeRates SET rate = ? WHERE base_currency_id = ? AND target_currency_id = ?";

        try (Connection connection = SQLiteConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setBigDecimal(1, exchangeRate.getRate());
            preparedStatement.setInt(2, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setInt(3, exchangeRate.getTargetCurrency().getId());

            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    exchangeRate.setRate(resultSet.getBigDecimal(1));
                }
            }

        } catch (SQLException e) {
            throw new QueryExecuteException();
        }
    }

    public boolean checkExistence(int baseId, int targetId) throws DatabaseConnectionException, QueryExecuteException {
        String sql = "SELECT * FROM ExchangeRates WHERE base_currency_id = ? AND target_currency_id = ?";

        try (Connection connection = SQLiteConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, baseId);
            preparedStatement.setInt(2, targetId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            throw new QueryExecuteException();
        }
    }

    public Optional<ExchangeRate> findByPair(CurrencyPair currencyPair)
            throws DatabaseConnectionException, QueryExecuteException {

        try {
            ExchangeRate exchangeRate = get(currencyPair);
            return Optional.of(exchangeRate);
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }
}
