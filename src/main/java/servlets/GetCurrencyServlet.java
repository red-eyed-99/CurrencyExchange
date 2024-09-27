package servlets;

import dao.CurrencyDAO;
import exceptions.BadRequestException;
import exceptions.QueryExecuteException;
import exceptions.DatabaseConnectionException;
import exceptions.NotFoundException;
import models.Currency;
import utils.ExceptionHandler;
import utils.JsonResponsePrinter;
import utils.validators.GetRequestValidator;
import utils.ErrorMessage;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/currency/*")
public class GetCurrencyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CurrencyDAO currencyDAO = new CurrencyDAO();

        Currency currency;

        try {
            GetRequestValidator validator = new GetRequestValidator();

            String currencyCode = validator.validateOnGetCurrency(request);

            currency = currencyDAO.get(currencyCode);

        } catch (DatabaseConnectionException | QueryExecuteException | NotFoundException
                 | BadRequestException e) {
            ErrorMessage message = ExceptionHandler.handle(e, response);
            JsonResponsePrinter.print(response, message);
            return;
        }

        JsonResponsePrinter.print(response, currency);
    }
}