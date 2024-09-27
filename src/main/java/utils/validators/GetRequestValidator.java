package utils.validators;

import exceptions.BadRequestException;

import javax.servlet.http.HttpServletRequest;

public class GetRequestValidator extends RequestValidator {

    public String validateOnGetCurrency(HttpServletRequest request) throws BadRequestException {
        return validate(request, CURRENCY_PARAM_LIMITS.get("code"));
    }

    public String[] validateOnGetExchangeRate(HttpServletRequest request) throws BadRequestException {
        String codeValue = validate(request, PAIR_CODE_LENGTH);

        String[] codes = new String[2];
        codes[0] = codeValue.substring(0, codeValue.length() / 2);
        codes[1] = codeValue.substring(codeValue.length() / 2);

        return codes;
    }

    private String validate(HttpServletRequest request, int lengthLimit) throws BadRequestException {
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


}
