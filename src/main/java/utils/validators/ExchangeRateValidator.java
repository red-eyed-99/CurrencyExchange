package utils.validators;

import exceptions.BadRequestException;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRateValidator extends RequestValidator {
    private static final Map<String, Integer> EXCHANGERATES_PARAM_LIMITS = new HashMap<>();
    private static final int PAIR_CODE_LENGTH = 6;

    private static final String EXCHANGERATES_BASECODE = "baseCurrencyCode";
    private static final String EXCHANGERATES_TARGETCODE = "targetCurrencyCode";
    private static final String EXCHANGERATES_RATE = "rate";

    static {
        EXCHANGERATES_PARAM_LIMITS.put(EXCHANGERATES_BASECODE, 3);
        EXCHANGERATES_PARAM_LIMITS.put(EXCHANGERATES_TARGETCODE, 3);
        EXCHANGERATES_PARAM_LIMITS.put(EXCHANGERATES_RATE, 40);
    }

    public String[] validateCurrencyPairCode(HttpServletRequest request) throws BadRequestException {
        String codeValue = validatePathCode(request, PAIR_CODE_LENGTH);

        String[] codes = new String[2];
        codes[0] = codeValue.substring(0, codeValue.length() / 2);
        codes[1] = codeValue.substring(codeValue.length() / 2);

        return codes;
    }

    public BigDecimal validateRate(String rateValue) throws BadRequestException {
        checkRateValue(rateValue);
        return new BigDecimal(rateValue);
    }

    @Override
    public void validateQueryParams(HttpServletRequest request) throws BadRequestException {
        for (Map.Entry<String, Integer> entry : EXCHANGERATES_PARAM_LIMITS.entrySet()) {
            String parameterName = entry.getKey();
            String parameterValue = request.getParameter(parameterName);

            int lengthLimit = entry.getValue();

            checkNullOrBlank(parameterName, parameterValue);

            if (parameterName.equals(EXCHANGERATES_BASECODE)) {
                String targetCurrencyCode = request.getParameter(EXCHANGERATES_TARGETCODE);
                checkCodesEquals(parameterValue, targetCurrencyCode);
            }

            checkParameterLength(parameterValue, lengthLimit);

            if (parameterName.equals(EXCHANGERATES_RATE)) {
                checkRateValue(parameterValue);
            }
        }
    }

    private void checkCodesEquals(String baseCode, String targetCode) throws BadRequestException {
        final int ARE_EQUAL = 0;

        if (baseCode.compareToIgnoreCase(targetCode) == ARE_EQUAL) {
            throw new BadRequestException("Currency codes must not be equal");
        }
    }

    private void checkRateValue(String rateValue) throws BadRequestException {
        double rate;

        try {
            rate = Double.parseDouble(rateValue);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Rate parameter value must be a number");
        }

        if (rate < 0) {
            throw new BadRequestException("Rate parameter value must be a positive number");
        }
    }
}
