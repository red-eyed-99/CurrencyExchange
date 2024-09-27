package utils.validators;

import exceptions.BadRequestException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class PostRequestValidator extends RequestValidator {

    public void validateOnPostCurrency(HttpServletRequest request) throws BadRequestException {
        validate(request, CURRENCY_PARAM_LIMITS);
    }

    public void validateOnPostExchangeRate(HttpServletRequest request) throws BadRequestException {
        validate(request, EXCHANGERATES_PARAM_LIMITS);
    }

    private void validate(HttpServletRequest request, Map<String, Integer> paramsLimits) throws BadRequestException {
        for (Map.Entry<String, Integer> entry : paramsLimits.entrySet()) {
            String parameterName = entry.getKey();
            String parameterValue = request.getParameter(parameterName);

            int lengthLimit = entry.getValue();

            checkNullOrBlank(parameterName, parameterValue);

            if (isCode(parameterName)) {
                if (parameterName.equals("baseCurrencyCode")) {
                    String targetCurrencyCode = request.getParameter("targetCurrencyCode");
                    checkCodesEquals(parameterValue, targetCurrencyCode);
                }

                checkCodeValue(parameterValue, lengthLimit);
            }

            checkParameterLength(parameterName, parameterValue, lengthLimit);

            if (parameterName.equals("rate")) {
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
        return paramName.equals("code") || paramName.equals("baseCurrencyCode") || paramName.equals("targetCurrencyCode");
    }

    private void checkCodesEquals(String baseCode, String targetCode) throws BadRequestException {
        if (baseCode.compareToIgnoreCase(targetCode) == 0) {
            throw new BadRequestException("Currency codes must not be equal");
        }
    }

    private void checkCodeValue(String codeValue, int lengthLimit) throws BadRequestException {
        if (codeValue.length() != lengthLimit) {
            throw new BadRequestException(codeValue +
                    " parameter length must be equal to " + lengthLimit + " characters");
        }
    }

    private void checkParameterLength(String parameterName, String parameterValue, int lengthLimit) throws BadRequestException {
        if (parameterValue.length() > lengthLimit) {
            throw new BadRequestException(parameterName +
                    " parameter length must not exceed " + lengthLimit + " characters");
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
