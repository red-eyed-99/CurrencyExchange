package utils;

import models.Currency;
import models.CurrencyPair;
import models.ExchangeRate;

public class ExchangeCurrencyCalculator {

    public double calculateDirectRate(double rate, double amount) {
        return amount * rate;
    }

    public double calculateIndirectRate(double rate, double amount) {
        return amount / rate;
    }

    public double calculateCrossRate(TypeOfQuote typeOfQuote,
                                     ExchangeRate baseExchangeRate,
                                     ExchangeRate targetExchangeRate,
                                     CurrencyPair currencyPair,
                                     double amount) {

        Currency baseCurrency = currencyPair.getBaseCurrency();
        Currency targetCurrency = currencyPair.getTargetCurrency();

        double baseRateValue = baseExchangeRate.getRate();
        double targetRateValue = targetExchangeRate.getRate();

        if (typeOfQuote == TypeOfQuote.DIRECT) {
            return amount * targetRateValue / baseRateValue;
        } else if (typeOfQuote == TypeOfQuote.INDIRECT) {
            return amount * baseRateValue / targetRateValue;
        } else {
            TypeOfQuote baseCurrencyExchangeRateQuotation = determineTypeOfQuote(baseExchangeRate, baseCurrency);
            if (baseCurrencyExchangeRateQuotation == TypeOfQuote.INDIRECT) {
                return amount * baseRateValue * targetRateValue;
            } else {
                return amount / (baseRateValue * targetRateValue);
            }
        }
    }

    private TypeOfQuote determineTypeOfQuote(ExchangeRate exchangeRate, Currency currency) {
        String exchangeRateBaseCurrencyCode = exchangeRate.getBaseCurrency().getCode();
        String baseCurrencyCode = currency.getCode();

        if (baseCurrencyCode.equals(exchangeRateBaseCurrencyCode)) {
            return TypeOfQuote.INDIRECT;
        }

        return TypeOfQuote.DIRECT;
    }
}
