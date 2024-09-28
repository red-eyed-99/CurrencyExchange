package utils;

import exceptions.BadRequestException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class RequestValidator {
    private static final Map<String, Integer> CURRENCY_PARAM_LIMITS = new HashMap<>();
    private static final Map<String, Integer> EXCHANGERATES_PARAM_LIMITS = new HashMap<>();
    private static final Map<String, Integer> EXCHANGECURRENCY_PARAM_LIMITS = new HashMap<>();

    private static final int PAIR_CODE_LENGTH = 6;

    private static final String CURRENCY_CODE = "code";
    private static final String CURRENCY_NAME = "name";
    private static final String CURRENCY_SIGN = "sign";

    private static final String EXCHANGERATES_BASECODE = "baseCurrencyCode";
    private static final String EXCHANGERATES_TARGETCODE = "targetCurrencyCode";
    private static final String EXCHANGERATES_RATE = "rate";

    private static final String EXCHANGECURRENCY_BASECODE = "from";
    private static final String EXCHANGECURRENCY_TARGETCODE = "to";
    private static final String EXCHANGECURRENCY_AMOUNT = "amount";

    static {
        CURRENCY_PARAM_LIMITS.put(CURRENCY_CODE, 3);
        CURRENCY_PARAM_LIMITS.put(CURRENCY_NAME, 255);
        CURRENCY_PARAM_LIMITS.put(CURRENCY_SIGN, 5);
    }

    static {
        EXCHANGERATES_PARAM_LIMITS.put(EXCHANGERATES_BASECODE, 3);
        EXCHANGERATES_PARAM_LIMITS.put(EXCHANGERATES_TARGETCODE, 3);
        EXCHANGERATES_PARAM_LIMITS.put(EXCHANGERATES_RATE, 40);
    }

    static {
        EXCHANGECURRENCY_PARAM_LIMITS.put(EXCHANGECURRENCY_BASECODE, 3);
        EXCHANGECURRENCY_PARAM_LIMITS.put(EXCHANGECURRENCY_TARGETCODE, 3);
        EXCHANGECURRENCY_PARAM_LIMITS.put(EXCHANGECURRENCY_AMOUNT, 40);
    }

    public double validateRate(String rateValue) throws BadRequestException {
        checkRateValue(rateValue);
        return Double.parseDouble(rateValue);
    }

    public String validateCurrencyCode(HttpServletRequest request) throws BadRequestException {
        return validateCode(request, CURRENCY_PARAM_LIMITS.get(CURRENCY_CODE));
    }

    public String[] validateCurrencyPairCode(HttpServletRequest request) throws BadRequestException {
        String codeValue = validateCode(request, PAIR_CODE_LENGTH);

        String[] codes = new String[2];
        codes[0] = codeValue.substring(0, codeValue.length() / 2);
        codes[1] = codeValue.substring(codeValue.length() / 2);

        return codes;
    }

    private String validateCode(HttpServletRequest request, int lengthLimit) throws BadRequestException {
        String pathInfo;

        if ((pathInfo = request.getPathInfo()) == null) {
            throw new BadRequestException("Code parameter missing in request address");
        }

        String codeValue = pathInfo.substring(1);

        if (codeValue.isBlank() || codeValue.length() != lengthLimit) {
            throw new BadRequestException("Code parameter must be equal to " + lengthLimit + " characters");
        }

        return codeValue;
    }

    public void validateCurrencyQueryParams(HttpServletRequest request) throws BadRequestException {
        validateParams(request, CURRENCY_PARAM_LIMITS);
    }

    public void validateExchangeRateQueryParams(HttpServletRequest request) throws BadRequestException {
        validateParams(request, EXCHANGERATES_PARAM_LIMITS);
    }

    public void validateExchangeCurrencyQueryParams(HttpServletRequest request) throws BadRequestException {
        validateParams(request, EXCHANGECURRENCY_PARAM_LIMITS);
    }

    private void validateParams(HttpServletRequest request, Map<String, Integer> paramsLimits) throws BadRequestException {
        for (Map.Entry<String, Integer> entry : paramsLimits.entrySet()) {
            String parameterName = entry.getKey();
            String parameterValue = request.getParameter(parameterName);

            int lengthLimit = entry.getValue();

            checkNullOrBlank(parameterName, parameterValue);

            if (isCode(parameterName)) {
                if (parameterName.equals(EXCHANGERATES_BASECODE)) {
                    String targetCurrencyCode = request.getParameter(EXCHANGERATES_TARGETCODE);
                    checkCodesEquals(parameterValue, targetCurrencyCode);
                }

                checkCodeValue(parameterValue, lengthLimit);
            }

            checkParameterLength(parameterName, parameterValue, lengthLimit);

            if (parameterName.equals(EXCHANGERATES_RATE)) {
                checkRateValue(parameterValue);
            }
        }
    }

    private void checkNullOrBlank(String parameterName, String parameterValue) throws BadRequestException {
        if (parameterValue == null || parameterValue.isBlank()) {
            throw new BadRequestException(parameterName + " parameter missing in request address");
        }
    }

    private boolean isCode(String paramName) {
        return paramName.equals(CURRENCY_CODE)
                || paramName.equals(EXCHANGERATES_BASECODE)
                || paramName.equals(EXCHANGERATES_TARGETCODE);
    }

    private void checkCodesEquals(String baseCode, String targetCode) throws BadRequestException {
        if (baseCode.compareToIgnoreCase(targetCode) == 0) {
            throw new BadRequestException("Currency codes must not be equal");
        }
    }

    private void checkCodeValue(String codeValue, int lengthLimit) throws BadRequestException {
        if (codeValue.length() != lengthLimit) {
            throw new BadRequestException("Parameter length must be equal to " + lengthLimit + " characters");
        }
    }

    private void checkParameterLength(String parameterName, String parameterValue, int lengthLimit) throws BadRequestException {
        if (parameterValue.length() > lengthLimit) {
            throw new BadRequestException("Parameter length must not exceed " + lengthLimit + " characters");
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
