package utils.validators;

import exceptions.BadRequestException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class CurrencyValidator extends RequestValidator {
    private static final Map<String, Integer> CURRENCY_PARAM_LIMITS = new HashMap<>();

    private static final String CURRENCY_CODE = "code";
    private static final String CURRENCY_NAME = "name";
    private static final String CURRENCY_SIGN = "sign";

    static {
        CURRENCY_PARAM_LIMITS.put(CURRENCY_CODE, 3);
        CURRENCY_PARAM_LIMITS.put(CURRENCY_NAME, 255);
        CURRENCY_PARAM_LIMITS.put(CURRENCY_SIGN, 5);
    }

    public String validateCurrencyCode(HttpServletRequest request) throws BadRequestException {
        return validatePathCode(request, CURRENCY_PARAM_LIMITS.get(CURRENCY_CODE));
    }

    @Override
    public void validateQueryParams(HttpServletRequest request) throws BadRequestException {
        for (Map.Entry<String, Integer> entry : CURRENCY_PARAM_LIMITS.entrySet()) {
            String parameterName = entry.getKey();
            String parameterValue = request.getParameter(parameterName);

            int lengthLimit = entry.getValue();

            checkNullOrBlank(parameterName, parameterValue);

            if (parameterName.equals(CURRENCY_CODE)) {
                checkCode(parameterValue, lengthLimit);
            }

            if (parameterName.equals(CURRENCY_SIGN)) {
                checkSignValue(parameterValue, lengthLimit);
                continue;
            }

            checkParameterLength(parameterValue, lengthLimit);
        }
    }

    private void checkSignValue(String signValue, int lengthLimit) throws BadRequestException {
        String regex = "^\\D{1," + lengthLimit + "}$";

        if (!signValue.matches(regex)) {
            throw new BadRequestException("Sign parameter must be no more than " + lengthLimit +
                    " characters long and not contain numbers");
        }
    }
}
