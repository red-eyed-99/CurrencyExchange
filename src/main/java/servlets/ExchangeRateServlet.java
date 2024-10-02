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
import utils.ErrorMessage;
import utils.validators.ExchangeRateValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String method = request.getMethod();

        if (!method.equalsIgnoreCase("PATCH")) {
            super.service(request, response);
            return;
        }

        this.doPatch(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
        CurrencyDAO currencyDAO = new CurrencyDAO();

        ExchangeRate exchangeRate;

        try {
            ExchangeRateValidator validator = new ExchangeRateValidator();

            String[] currencyPairCodes = validator.validateCurrencyPairCode(request);

            Currency baseCurrency = currencyDAO.get(currencyPairCodes[0]);
            Currency targetCurrency = currencyDAO.get(currencyPairCodes[1]);

            CurrencyPair currencyPair = new CurrencyPair(baseCurrency, targetCurrency);

            exchangeRate = exchangeRateDAO.get(currencyPair);

        } catch (DatabaseConnectionException | QueryExecuteException | NotFoundException |
                 BadRequestException e) {

            ErrorMessage message = ExceptionHandler.handle(e, response);
            JsonResponsePrinter.print(response, message);
            return;
        }

        JsonResponsePrinter.print(response, exchangeRate);
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
        CurrencyDAO currencyDAO = new CurrencyDAO();

        ExchangeRate exchangeRate;

        try {
            ExchangeRateValidator validator = new ExchangeRateValidator();

            String[] currencyPairCodes = validator.validateCurrencyPairCode(request);
            BigDecimal rate = validator.validateRate(request.getParameter("rate"));

            Currency baseCurrency = currencyDAO.get(currencyPairCodes[0]);
            Currency targetCurrency = currencyDAO.get(currencyPairCodes[1]);

            CurrencyPair currencyPair = new CurrencyPair(baseCurrency, targetCurrency);

            exchangeRate = exchangeRateDAO.get(currencyPair);
            exchangeRate.setRate(rate);
            exchangeRateDAO.update(exchangeRate);

        } catch (DatabaseConnectionException | QueryExecuteException | NotFoundException |
                 BadRequestException e) {

            ErrorMessage message = ExceptionHandler.handle(e, response);
            JsonResponsePrinter.print(response, message);
            return;
        }

        JsonResponsePrinter.print(response, exchangeRate);
    }
}