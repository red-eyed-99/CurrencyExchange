package dto;

import java.math.BigDecimal;

public class ExchangeCurrencyRequestDTO {
    private final String fromCurrencyCode;
    private final String toCurrencyCode;
    private final BigDecimal amount;

    public ExchangeCurrencyRequestDTO(String fromCurrencyCode, String toCurrencyCode, BigDecimal amount) {
        this.fromCurrencyCode = fromCurrencyCode;
        this.toCurrencyCode = toCurrencyCode;
        this.amount = amount;
    }

    public String getFromCurrencyCode() {
        return fromCurrencyCode;
    }

    public String getToCurrencyCode() {
        return toCurrencyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
