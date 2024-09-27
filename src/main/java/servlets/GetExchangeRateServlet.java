package servlets;

import dao.CurrencyDAO;
import dao.ExchangeRateDAO;
import exceptions.BadRequestException;
import exceptions.QueryExecuteException;
import exceptions.DatabaseConnectionException;
import exceptions.NotFoundException;
import models.Currency;
import models.CurrencyPair;
import models.ExchangeRate;
import utils.ExceptionHandler;
import utils.JsonResponsePrinter;
import utils.validators.GetRequestValidator;
import utils.ErrorMessage;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class GetExchangeRateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
        CurrencyDAO currencyDAO = new CurrencyDAO();

        ExchangeRate exchangeRate;

        try {
            GetRequestValidator validator = new GetRequestValidator();

            String[] currencyPairCodes = validator.validateOnGetExchangeRate(request);

            Currency baseCurrency = currencyDAO.get(currencyPairCodes[0]);
            Currency targetCurrency = currencyDAO.get(currencyPairCodes[1]);

            CurrencyPair currencyPair = new CurrencyPair(baseCurrency, targetCurrency);

            exchangeRate = exchangeRateDAO.get(currencyPair);

        } catch (DatabaseConnectionException | QueryExecuteException | NotFoundException
                 | BadRequestException e) {
            ErrorMessage message = ExceptionHandler.handle(e, response);
            JsonResponsePrinter.print(response, message);
            return;
        }

        JsonResponsePrinter.print(response, exchangeRate);
    }
}