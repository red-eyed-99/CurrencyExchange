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
import utils.calculation.CalculationResult;
import utils.calculation.ExchangeCurrencyCalculator;
import utils.quotation.QuoteType;
import utils.quotation.QuoteTypeIdentifier;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeCurrencyService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();

    public ExchangeCurrencyResponseDTO exchangeCurrency(ExchangeCurrencyRequestDTO exchangeCurrencyRequestDTO)
            throws DatabaseConnectionException, NotFoundException, QueryExecuteException {

        Currency baseCurrency = currencyDAO.get(exchangeCurrencyRequestDTO.getFromCurrencyCode());
        Currency targetCurrency = currencyDAO.get(exchangeCurrencyRequestDTO.getToCurrencyCode());
        BigDecimal amount = exchangeCurrencyRequestDTO.getAmount();
        BigDecimal rate;
        BigDecimal convertedAmount;

        CurrencyPair currencyPair = new CurrencyPair(baseCurrency, targetCurrency);

        Optional<ExchangeRate> exchangeRate = exchangeRateDAO.findByPair(currencyPair);

        CalculationResult calculationResult;

        if (exchangeRate.isPresent()) {
            rate = exchangeRate.get().getRate();
            calculationResult = ExchangeCurrencyCalculator.calculateDirectRate(rate, amount);
        } else {
            exchangeRate = exchangeRateDAO.findByPair(new CurrencyPair(targetCurrency, baseCurrency));
            if (exchangeRate.isPresent()) {
                rate = exchangeRate.get().getRate();
                calculationResult = ExchangeCurrencyCalculator.calculateIndirectRate(rate, amount);
            } else {
                calculationResult = calculateCrossRate(currencyPair, amount);
            }
        }

        rate = calculationResult.getRate().stripTrailingZeros();
        amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        convertedAmount = calculationResult.getResult();

        return new ExchangeCurrencyResponseDTO(baseCurrency, targetCurrency, rate, amount, convertedAmount);
    }

    private CalculationResult calculateCrossRate(CurrencyPair currencyPair, BigDecimal amount)
            throws DatabaseConnectionException, NotFoundException, QueryExecuteException {

        Currency baseCurrency = currencyPair.getBaseCurrency();
        Currency targetCurrency = currencyPair.getTargetCurrency();

        Currency currencyUSD = currencyDAO.get("USD");

        ExchangeRate[] exchangeRates = getCrossExchangeRates(baseCurrency, targetCurrency, currencyUSD);

        ExchangeRate baseExchangeRate = exchangeRates[0];
        ExchangeRate targetExchangeRate = exchangeRates[1];

        QuoteType typeOfQuote = QuoteTypeIdentifier.determine(baseExchangeRate, targetExchangeRate, currencyUSD);

        return ExchangeCurrencyCalculator.calculateCrossRate(
                typeOfQuote, baseExchangeRate, targetExchangeRate, currencyPair, amount);
    }

    private ExchangeRate[] getCrossExchangeRates(Currency baseCurrency, Currency targetCurrency, Currency currencyUSD)
            throws DatabaseConnectionException, NotFoundException, QueryExecuteException {

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
