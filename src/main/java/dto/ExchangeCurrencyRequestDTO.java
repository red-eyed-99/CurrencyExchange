package dto;

public class ExchangeCurrencyRequestDTO {
    private final String fromCurrencyCode;
    private final String toCurrencyCode;
    private final double amount;

    public ExchangeCurrencyRequestDTO(String fromCurrencyCode, String toCurrencyCode, double amount) {
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

    public double getAmount() {
        return amount;
    }
}
