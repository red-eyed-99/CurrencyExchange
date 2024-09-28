package dto;

import models.Currency;

public class ExchangeCurrencyResponseDTO {
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private final double rate;
    private final double amount;
    private final double convertedAmount;

    public ExchangeCurrencyResponseDTO(
            Currency baseCurrency,
            Currency targetCurrency,
            double rate,
            double amount,
            double convertedAmount) {

        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }
}
