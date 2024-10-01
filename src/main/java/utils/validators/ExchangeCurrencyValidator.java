package utils.validators;

import exceptions.BadRequestException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ExchangeCurrencyValidator extends RequestValidator {
    private static final Map<String, Integer> EXCHANGECURRENCY_PARAM_LIMITS = new HashMap<>();

    private static final String EXCHANGECURRENCY_BASECODE = "from";
    private static final String EXCHANGECURRENCY_TARGETCODE = "to";
    private static final String EXCHANGECURRENCY_AMOUNT = "amount";

    static {
        EXCHANGECURRENCY_PARAM_LIMITS.put(EXCHANGECURRENCY_BASECODE, 3);
        EXCHANGECURRENCY_PARAM_LIMITS.put(EXCHANGECURRENCY_TARGETCODE, 3);
        EXCHANGECURRENCY_PARAM_LIMITS.put(EXCHANGECURRENCY_AMOUNT, 40);
    }

    @Override
    public void validateQueryParams(HttpServletRequest request) throws BadRequestException {
        for (Map.Entry<String, Integer> entry : EXCHANGECURRENCY_PARAM_LIMITS.entrySet()) {
            String parameterName = entry.getKey();
            String parameterValue = request.getParameter(parameterName);

            int lengthLimit = entry.getValue();

            checkNullOrBlank(parameterName, parameterValue);

            if (parameterName.equals(EXCHANGECURRENCY_BASECODE)) {
                String targetCurrencyCode = request.getParameter(EXCHANGECURRENCY_TARGETCODE);
                checkCodesEquals(parameterValue, targetCurrencyCode);
            }

            checkParameterLength(parameterName, parameterValue, lengthLimit);

            if (parameterName.equals(EXCHANGECURRENCY_AMOUNT)) {
                checkAmountValue(parameterValue);
            }
        }
    }

    private void checkCodesEquals(String baseCode, String targetCode) throws BadRequestException {
        final int ARE_EQUAL = 0;

        if (baseCode.compareToIgnoreCase(targetCode) == ARE_EQUAL) {
            throw new BadRequestException("Currency codes must not be equal");
        }
    }

    private void checkAmountValue(String rateValue) throws BadRequestException {
        double amount;

        try {
            amount = Double.parseDouble(rateValue);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Amount parameter value must be a number");
        }

        if (amount <= 0) {
            throw new BadRequestException("Amount parameter value must be a positive number and greater than zero");
        }
    }
}
