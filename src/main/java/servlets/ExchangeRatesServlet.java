package servlets;

import dao.CurrencyDAO;
import dao.ExchangeRateDAO;
import exceptions.*;
import models.Currency;
import models.ExchangeRate;
import utils.ErrorMessage;
import utils.ExceptionHandler;
import utils.JsonResponsePrinter;
import utils.validators.ExchangeRateValidator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/exchangeRates", "/exchangeRates*"})
public class ExchangeRatesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();

        List<ExchangeRate> exchangeRates;

        try {
            exchangeRates = exchangeRateDAO.getAll();
        } catch (DatabaseConnectionException | QueryExecuteException | NotFoundException e) {
            ErrorMessage message = ExceptionHandler.handle(e, response);
            JsonResponsePrinter.print(response, message);
            return;
        }

        JsonResponsePrinter.print(response, exchangeRates);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
        CurrencyDAO currencyDAO = new CurrencyDAO();

        try {
            ExchangeRateValidator validator = new ExchangeRateValidator();
            validator.validateQueryParams(request);

            String baseCurrencyCode = request.getParameter("baseCurrencyCode").toUpperCase();
            String targetCurrencyCode = request.getParameter("targetCurrencyCode").toUpperCase();

            Currency baseCurrency = currencyDAO.get(baseCurrencyCode);
            Currency targetCurrency = currencyDAO.get(targetCurrencyCode);

            if (exchangeRateDAO.checkExistence(baseCurrency.getId(), targetCurrency.getId())) {
                throw new AlreadyExistException("Exchange rate with this codes already exists");
            }

            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setBaseCurrency(currencyDAO.get(baseCurrencyCode));
            exchangeRate.setTargetCurrency(currencyDAO.get(targetCurrencyCode));
            exchangeRate.setRate(Double.parseDouble(request.getParameter("rate")));

            exchangeRateDAO.save(exchangeRate);

            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonResponsePrinter.print(response, exchangeRate);

        } catch (DatabaseConnectionException | QueryExecuteException | AlreadyExistException |
                 BadRequestException | NotFoundException e) {

            ErrorMessage message = ExceptionHandler.handle(e, response);
            JsonResponsePrinter.print(response, message);
        }
    }
}
