package service;

import dao.CurrencyDAO;
import dao.ExchangeRateDAO;
import dto.ExchangeCurrencyRequestDTO;
import dto.ExchangeCurrencyResponseDTO;
import exceptions.DatabaseConnectionException;
import exceptions.NotFoundException;
import exceptions.QueryExecuteException;
import models.Currency;
import models.CurrencyPair;
import models.ExchangeRate;
import utils.TypeOfQuote;

import java.util.Optional;

public class ExchangeCurrencyService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();

    public ExchangeCurrencyResponseDTO exchangeCurrency(ExchangeCurrencyRequestDTO exchangeCurrencyRequestDTO)
            throws DatabaseConnectionException, NotFoundException, QueryExecuteException {

        Currency baseCurrency = currencyDAO.get(exchangeCurrencyRequestDTO.getFromCurrencyCode());
        Currency targetCurrency = currencyDAO.get(exchangeCurrencyRequestDTO.getToCurrencyCode());
        double amount = exchangeCurrencyRequestDTO.getAmount();
        double rate;
        double convertedAmount;

        ExchangeRate exchangeRate;

        if (exchangeRateDAO.checkExistence(baseCurrency.getId(), targetCurrency.getId())) {
            exchangeRate = exchangeRateDAO.get(new CurrencyPair(baseCurrency, targetCurrency));
            rate = exchangeRate.getRate();
            convertedAmount = calculateDirectRate(rate, amount);
        } else if (exchangeRateDAO.checkExistence(targetCurrency.getId(), baseCurrency.getId())) {
            exchangeRate = exchangeRateDAO.get(new CurrencyPair(targetCurrency, baseCurrency));
            rate = exchangeRate.getRate();
            convertedAmount = calculateIndirectRate(rate, amount);
        } else {
            convertedAmount = calculateCrossRate(baseCurrency, targetCurrency, amount);
            rate = convertedAmount / amount;
        }

        return new ExchangeCurrencyResponseDTO(baseCurrency, targetCurrency, rate, amount, convertedAmount);
    }

    private double calculateDirectRate(double rate, double amount) {
        return amount * rate;
    }

    private double calculateIndirectRate(double rate, double amount) {
        return amount / rate;
    }

    private double calculateCrossRate(Currency baseCurrency, Currency targetCurrency, double amount)
            throws NotFoundException, DatabaseConnectionException, QueryExecuteException {

        ExchangeRate[] exchangeRates = getCrossExchangeRates(baseCurrency, targetCurrency);

        ExchangeRate baseExchangeRate = exchangeRates[0];
        ExchangeRate targetExchangeRate = exchangeRates[1];

        double baseRateValue = baseExchangeRate.getRate();
        double targetRateValue = targetExchangeRate.getRate();

        TypeOfQuote typeOfQuote = determineExchangeRatesQuoteType(baseExchangeRate, targetExchangeRate);

        if (typeOfQuote == TypeOfQuote.DIRECT) {
            return targetRateValue / baseRateValue * amount;
        } else if (typeOfQuote == TypeOfQuote.INDIRECT) {
            return baseRateValue / targetRateValue * amount;
        } else {
            if (determineTypeOfQuote(baseExchangeRate) == TypeOfQuote.DIRECT) {
                return baseRateValue * (amount / targetRateValue);
            } else {
                return (amount / baseRateValue) * targetRateValue;
            }
        }
    }

    private TypeOfQuote determineTypeOfQuote(ExchangeRate exchangeRate) {
        String baseCurrencyCode = exchangeRate.getBaseCurrency().getCode();
        if (baseCurrencyCode.equals("USD")) {
            return TypeOfQuote.DIRECT;
        }

        return TypeOfQuote.INDIRECT;
    }

    private TypeOfQuote determineExchangeRatesQuoteType(ExchangeRate baseExchangeRate, ExchangeRate targetExchangeRate) {
        String numeratorBase = baseExchangeRate.getBaseCurrency().getCode();
        String numeratorTarget = targetExchangeRate.getBaseCurrency().getCode();

        if (numeratorBase.equals(numeratorTarget)) {
            return TypeOfQuote.DIRECT;
        }

        String denominatorBase = baseExchangeRate.getTargetCurrency().getCode();
        String denominatorTarget = targetExchangeRate.getTargetCurrency().getCode();

        if (denominatorBase.equals(denominatorTarget)) {
            return TypeOfQuote.INDIRECT;
        }

        return TypeOfQuote.BOTH;
    }

    private ExchangeRate[] getCrossExchangeRates(Currency baseCurrency, Currency targetCurrency)
            throws DatabaseConnectionException, NotFoundException, QueryExecuteException {

        Currency currencyUSD = currencyDAO.get("USD");

        CurrencyPair[] USDBaseCurrencyPairs = getUSDCurrencyPairs(currencyUSD, baseCurrency);
        CurrencyPair[] USDTargetCurrencyPairs = getUSDCurrencyPairs(currencyUSD, targetCurrency);

        Optional<ExchangeRate> USDBaseRate = getUSDExchangeRate(USDBaseCurrencyPairs);

        if (USDBaseRate.isEmpty()) {
            throw new NotFoundException("There are no suitable exchange rates");
        }

        Optional<ExchangeRate> USDTargetRate = getUSDExchangeRate(USDTargetCurrencyPairs);

        if (USDTargetRate.isPresent()) {
            return new ExchangeRate[]{USDBaseRate.get(), USDTargetRate.get()};
        } else {
            throw new NotFoundException("There are no suitable exchange rates");
        }
    }

    private CurrencyPair[] getUSDCurrencyPairs(Currency currencyUSD, Currency currency) {
        CurrencyPair[] currencyPairs = new CurrencyPair[2];

        currencyPairs[0] = new CurrencyPair(currencyUSD, currency);
        currencyPairs[1] = new CurrencyPair(currency, currencyUSD);

        return currencyPairs;
    }

    private Optional<ExchangeRate> getUSDExchangeRate(CurrencyPair[] currencyPairs)
            throws DatabaseConnectionException, QueryExecuteException {

        Optional<ExchangeRate> exchangeRate = Optional.empty();

        for (CurrencyPair currencyPair : currencyPairs) {
            exchangeRate = exchangeRateDAO.getIfExist(currencyPair);
            if (exchangeRate.isPresent()) {
                return exchangeRate;
            }
        }

        return exchangeRate;
    }
}
