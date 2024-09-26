package utils;

import exceptions.BadRequestException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class RequestValidator {
    private static final Map<String, Integer> PARAMS_LENGTH_LIMITS = new HashMap<>();

    static {
        PARAMS_LENGTH_LIMITS.put("code", 3);
        PARAMS_LENGTH_LIMITS.put("name", 255);
        PARAMS_LENGTH_LIMITS.put("sign", 5);
    }

    public String validateGettingCurrency(HttpServletRequest request) throws BadRequestException {
        String pathInfo;

        if ((pathInfo = request.getPathInfo()) == null) {
            throw new BadRequestException("Currency code parameter missing in request address");
        }

        String currencyCodeValue = pathInfo.substring(1);

        int lengthLimit = PARAMS_LENGTH_LIMITS.get("code");

        if (currencyCodeValue.isBlank() || currencyCodeValue.length() != lengthLimit) {
            throw new BadRequestException("Currency code parameter must be equal to " + lengthLimit + " characters");
        }

        return currencyCodeValue;
    }

    public void validatePostingCurrency(HttpServletRequest request) throws BadRequestException {
        for (Map.Entry<String, Integer> entry : PARAMS_LENGTH_LIMITS.entrySet()) {
            String parameterName = entry.getKey();
            String parameterValue = request.getParameter(parameterName);

            int lengthLimit = entry.getValue();

            if (parameterValue == null || parameterValue.isBlank()) {
                throw new BadRequestException(parameterName + " parameter missing in request address");
            }
            if (parameterName.equals("code") && parameterValue.length() != lengthLimit) {
                throw new BadRequestException(parameterName +
                        " parameter length must be equal to " + lengthLimit + "characters");
            }
            if (parameterValue.length() > lengthLimit) {
                throw new BadRequestException(parameterName +
                        " parameter length must not exceed " + lengthLimit + " characters");
            }
        }
    }
}
