package dto;

import models.Currency;

import java.math.BigDecimal;

public class ExchangeCurrencyResponseDTO {
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private final BigDecimal rate;
    private final BigDecimal amount;
    private final BigDecimal convertedAmount;

    public ExchangeCurrencyResponseDTO(
            Currency baseCurrency,
            Currency targetCurrency,
            BigDecimal rate,
            BigDecimal amount,
            BigDecimal convertedAmount) {

        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }
}
