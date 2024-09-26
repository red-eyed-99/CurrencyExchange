package utils;

import exceptions.AlreadyExistException;
import exceptions.BadRequestException;
import exceptions.NotFoundException;

import javax.servlet.http.HttpServletResponse;

public class ExceptionHandler {
    public static ErrorMessage handle(Exception e, HttpServletResponse response) {
        if (e instanceof NotFoundException) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else if (e instanceof BadRequestException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else if (e instanceof AlreadyExistException) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return new ErrorMessage(response.getStatus(), e.getMessage());
    }
}
