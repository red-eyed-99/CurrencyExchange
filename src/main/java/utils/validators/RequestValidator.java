package utils.validators;

import exceptions.BadRequestException;

import javax.servlet.http.HttpServletRequest;

public abstract class RequestValidator {

    public abstract void validateQueryParams(HttpServletRequest request) throws BadRequestException;

    String validatePathCode(HttpServletRequest request, int lengthLimit) throws BadRequestException {
        String pathInfo;

        if ((pathInfo = request.getPathInfo()) == null) {
            throw new BadRequestException("Code parameter missing in request address");
        }

        String codeValue = pathInfo.substring(1);

        checkCode(codeValue, lengthLimit);

        return codeValue.toUpperCase();
    }

    void checkNullOrBlank(String parameterName, String parameterValue) throws BadRequestException {
        if (parameterValue == null || parameterValue.isBlank()) {
            throw new BadRequestException(parameterName + " parameter missing in request address");
        }
    }

    void checkParameterLength(String parameterValue, int lengthLimit) throws BadRequestException {
        if (parameterValue.length() > lengthLimit) {
            throw new BadRequestException("Parameter length must not exceed " + lengthLimit + " characters");
        }
    }

    void checkCode(String codeValue, int lengthLimit) throws BadRequestException {
        String regex = "^[a-zA-z]{" + lengthLimit + "}$";

        if (!codeValue.matches(regex)) {
            throw new BadRequestException("Code parameter must be equal to " + lengthLimit + " latin characters");
        }
    }
}
