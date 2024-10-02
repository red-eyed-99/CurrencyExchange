package utils.quotation;

import models.Currency;
import models.ExchangeRate;

public class QuoteTypeIdentifier {

    public static QuoteType determine(ExchangeRate exchangeRate, Currency currency) {
        String exchangeRateBaseCurrencyCode = exchangeRate.getBaseCurrency().getCode();
        String currencyCode = currency.getCode();

        if (currencyCode.equals(exchangeRateBaseCurrencyCode)) {
            return QuoteType.DIRECT;
        }

        return QuoteType.INDIRECT;
    }

    public static QuoteType determine(ExchangeRate baseExchangeRate,
                                      ExchangeRate targetExchangeRate,
                                      Currency currency) {

        QuoteType baseQuoteType = determine(baseExchangeRate, currency);
        QuoteType targetQuoteType = determine(targetExchangeRate, currency);

        if (baseQuoteType.equals(targetQuoteType) && baseQuoteType == QuoteType.DIRECT) {
            return QuoteType.DIRECT;
        }
        if (baseQuoteType.equals(targetQuoteType) && baseQuoteType == QuoteType.INDIRECT) {
            return QuoteType.INDIRECT;
        }

        return QuoteType.BOTH;
    }
}
