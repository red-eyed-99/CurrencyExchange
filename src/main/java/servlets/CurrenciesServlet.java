package servlets;

import dao.CurrencyDAO;
import exceptions.*;
import models.Currency;
import utils.ExceptionHandler;
import utils.JsonResponsePrinter;
import utils.RequestValidator;
import utils.ErrorMessage;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/currencies", "/currencies*"})
public class CurrenciesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CurrencyDAO currencyDAO = new CurrencyDAO();

        List<Currency> currencies;

        try {
            currencies = currencyDAO.getAll();
        } catch (DatabaseConnectionException | QueryExecuteException | NotFoundException e) {
            ErrorMessage message = ExceptionHandler.handle(e, response);
            JsonResponsePrinter.print(response, message);
            return;
        }

        JsonResponsePrinter.print(response, currencies);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CurrencyDAO currencyDAO = new CurrencyDAO();

        try {
            RequestValidator validator = new RequestValidator();
            validator.validatePostingCurrency(request);

            if (currencyDAO.checkExistence(request.getParameter("code"))) {
                throw new AlreadyExistException("Currency with this code already exists");
            }

            Currency currency = new Currency();
            currency.setCode(request.getParameter("code"));
            currency.setName(request.getParameter("name"));
            currency.setSign(request.getParameter("sign"));

            currencyDAO.save(currency);

            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonResponsePrinter.print(response, currency);

        } catch (DatabaseConnectionException | QueryExecuteException | AlreadyExistException |
                 BadRequestException e) {

            ErrorMessage message = ExceptionHandler.handle(e, response);
            JsonResponsePrinter.print(response, message);
        }
    }
}
