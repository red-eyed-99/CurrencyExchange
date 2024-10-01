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
import utils.ExchangeCurrencyCalculator;
import utils.TypeOfQuote;

import java.util.Optional;

public class ExchangeCurrencyService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();

    private final ExchangeCurrencyCalculator calculator = new ExchangeCurrencyCalculator();

    public ExchangeCurrencyResponseDTO exchangeCurrency(ExchangeCurrencyRequestDTO exchangeCurrencyRequestDTO)
            throws DatabaseConnectionException, NotFoundException, QueryExecuteException {

        Currency baseCurrency = currencyDAO.get(exchangeCurrencyRequestDTO.getFromCurrencyCode());
        Currency targetCurrency = currencyDAO.get(exchangeCurrencyRequestDTO.getToCurrencyCode());
        double amount = exchangeCurrencyRequestDTO.getAmount();
        double rate;
        double convertedAmount;

        CurrencyPair currencyPair = new CurrencyPair(baseCurrency, targetCurrency);

        Optional<ExchangeRate> exchangeRate = exchangeRateDAO.findByPair(currencyPair);

        if (exchangeRate.isPresent()) {
            rate = exchangeRate.get().getRate();
            convertedAmount = calculator.calculateDirectRate(rate, amount);
        } else {
            exchangeRate = exchangeRateDAO.findByPair(new CurrencyPair(targetCurrency, baseCurrency));
            if (exchangeRate.isPresent()) {
                rate = exchangeRate.get().getRate();
                convertedAmount = calculator.calculateIndirectRate(rate, amount);
            } else {
                ExchangeRate[] exchangeRates = getCrossExchangeRates(baseCurrency, targetCurrency);

                ExchangeRate baseExchangeRate = exchangeRates[0];
                ExchangeRate targetExchangeRate = exchangeRates[1];

                TypeOfQuote typeOfQuote = determineExchangeRatesQuoteType(baseExchangeRate, targetExchangeRate);

                convertedAmount = calculator.calculateCrossRate(
                        typeOfQuote, baseExchangeRate, targetExchangeRate, currencyPair, amount);

                rate = convertedAmount / amount;
            }
        }

        return new ExchangeCurrencyResponseDTO(baseCurrency, targetCurrency, rate, amount, convertedAmount);
    }

    private TypeOfQuote determineExchangeRatesQuoteType(ExchangeRate baseExchangeRate, ExchangeRate targetExchangeRate) {
        String numeratorBaseCode = baseExchangeRate.getBaseCurrency().getCode();
        String numeratorTargetCode = targetExchangeRate.getBaseCurrency().getCode();

        if (numeratorBaseCode.equals(numeratorTargetCode)) {
            return TypeOfQuote.DIRECT;
        }

        String denominatorBaseCode = baseExchangeRate.getTargetCurrency().getCode();
        String denominatorTargetCode = targetExchangeRate.getTargetCurrency().getCode();

        if (denominatorBaseCode.equals(denominatorTargetCode)) {
            return TypeOfQuote.INDIRECT;
        }

        return TypeOfQuote.BOTH;
    }

    private ExchangeRate[] getCrossExchangeRates(Currency baseCurrency, Currency targetCurrency)
            throws DatabaseConnectionException, NotFoundException, QueryExecuteException {

        Currency currencyUSD = currencyDAO.get("USD");

        CurrencyPair[] USDBaseCurrencyPairs = createCurrencyPairsWithUSD(currencyUSD, baseCurrency);
        CurrencyPair[] USDTargetCurrencyPairs = createCurrencyPairsWithUSD(currencyUSD, targetCurrency);

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

    private CurrencyPair[] createCurrencyPairsWithUSD(Currency currencyUSD, Currency currency) {
        CurrencyPair[] currencyPairs = new CurrencyPair[2];

        currencyPairs[0] = new CurrencyPair(currencyUSD, currency);
        currencyPairs[1] = new CurrencyPair(currency, currencyUSD);

        return currencyPairs;
    }

    private Optional<ExchangeRate> getUSDExchangeRate(CurrencyPair[] currencyPairs)
            throws DatabaseConnectionException, QueryExecuteException {

        Optional<ExchangeRate> exchangeRate = Optional.empty();

        for (CurrencyPair currencyPair : currencyPairs) {
            exchangeRate = exchangeRateDAO.findByPair(currencyPair);
            if (exchangeRate.isPresent()) {
                return exchangeRate;
            }
        }

        return exchangeRate;
    }
}
