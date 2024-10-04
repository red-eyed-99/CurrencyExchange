package utils.calculation;

import models.Currency;
import models.CurrencyPair;
import models.ExchangeRate;
import utils.quotation.QuoteType;
import utils.quotation.QuoteTypeIdentifier;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeCurrencyCalculator {

    public static CalculationResult calculateDirectRate(BigDecimal rate, BigDecimal amount) {
        BigDecimal result = amount
                .multiply(rate)
                .setScale(2, RoundingMode.HALF_EVEN)
                .stripTrailingZeros();

        return new CalculationResult(result, rate);
    }

    public static CalculationResult calculateIndirectRate(BigDecimal rate, BigDecimal amount) {
        BigDecimal result = amount
                .divide((rate), 2, RoundingMode.HALF_EVEN)
                .stripTrailingZeros();

        rate = BigDecimal.ONE.divide(rate, 6, RoundingMode.HALF_EVEN);

        return new CalculationResult(result, rate);
    }

    public static CalculationResult calculateCrossRate(QuoteType typeOfQuote,
                                                       ExchangeRate baseExchangeRate,
                                                       ExchangeRate targetExchangeRate,
                                                       CurrencyPair currencyPair,
                                                       BigDecimal amount) {

        BigDecimal baseRateValue = baseExchangeRate.getRate();
        BigDecimal targetRateValue = targetExchangeRate.getRate();

        if (typeOfQuote == QuoteType.DIRECT) {
            return calculateSingleQuoteCrossRate(targetRateValue, baseRateValue, amount);
        } else if (typeOfQuote == QuoteType.INDIRECT) {
            return calculateSingleQuoteCrossRate(baseRateValue, targetRateValue, amount);
        } else {
            Currency baseCurrency = currencyPair.getBaseCurrency();
            return calculateBothQuoteCrossRate(baseCurrency, baseExchangeRate, baseRateValue, targetRateValue, amount);
        }
    }

    private static CalculationResult calculateSingleQuoteCrossRate(BigDecimal baseRate,
                                                                   BigDecimal targetRate,
                                                                   BigDecimal amount) {
        BigDecimal result = amount
                .multiply(baseRate)
                .divide(targetRate, 2, RoundingMode.HALF_EVEN)
                .stripTrailingZeros();

        BigDecimal rate = baseRate.divide(targetRate, 6, RoundingMode.HALF_EVEN);

        return new CalculationResult(result, rate);
    }

    private static CalculationResult calculateBothQuoteCrossRate(Currency baseCurrency,
                                                                 ExchangeRate baseExchangeRate,
                                                                 BigDecimal baseRateValue,
                                                                 BigDecimal targetRateValue,
                                                                 BigDecimal amount) {

        QuoteType baseCurrencyExchangeRateQuotation = QuoteTypeIdentifier.determine(baseExchangeRate, baseCurrency);
        if (baseCurrencyExchangeRateQuotation == QuoteType.DIRECT) {
            BigDecimal result = amount
                    .multiply(baseRateValue)
                    .multiply(targetRateValue)
                    .setScale(2, RoundingMode.HALF_EVEN)
                    .stripTrailingZeros();

            BigDecimal rate = multiplyRates(baseRateValue, targetRateValue);

            return new CalculationResult(result, rate);
        } else {
            BigDecimal result = amount
                    .divide(baseRateValue.multiply(targetRateValue), 2, RoundingMode.HALF_EVEN)
                    .stripTrailingZeros();

            BigDecimal rate = BigDecimal.ONE
                    .divide(multiplyRates(baseRateValue, targetRateValue), 6, RoundingMode.HALF_EVEN);

            return new CalculationResult(result, rate);
        }
    }

    private static BigDecimal multiplyRates(BigDecimal baseRate, BigDecimal targetRate) {
        return targetRate.multiply((baseRate).setScale(6, RoundingMode.HALF_EVEN));
    }
}
