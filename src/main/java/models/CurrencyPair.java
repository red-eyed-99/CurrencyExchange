package models;

public class CurrencyPair {
    private final Currency baseCurrency;
    private final Currency targetCurrency;

    public CurrencyPair(Currency baseCurrency, Currency targetCurrency) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }
}
