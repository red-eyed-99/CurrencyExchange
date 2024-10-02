package utils.calculation;

import java.math.BigDecimal;

public class CalculationResult {
    private final BigDecimal result;
    private final BigDecimal rate;

    public CalculationResult(BigDecimal result, BigDecimal rate) {
        this.result = result;
        this.rate = rate;
    }

    public BigDecimal getResult() {
        return result;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
