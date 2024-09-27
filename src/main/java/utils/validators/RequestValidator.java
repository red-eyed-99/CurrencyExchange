package utils.validators;

import java.util.HashMap;
import java.util.Map;

public abstract class RequestValidator {
    public static final Map<String, Integer> CURRENCY_PARAM_LIMITS = new HashMap<>();
    public static final Map<String, Integer> EXCHANGERATES_PARAM_LIMITS = new HashMap<>();
    public static final int PAIR_CODE_LENGTH = 6;

    static {
        CURRENCY_PARAM_LIMITS.put("code", 3);
        CURRENCY_PARAM_LIMITS.put("name", 255);
        CURRENCY_PARAM_LIMITS.put("sign", 5);
    }

    static {
        EXCHANGERATES_PARAM_LIMITS.put("baseCurrencyCode", 3);
        EXCHANGERATES_PARAM_LIMITS.put("targetCurrencyCode", 3);
        EXCHANGERATES_PARAM_LIMITS.put("rate", 40);
    }
}
