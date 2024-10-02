package servlets;

import dto.ExchangeCurrencyRequestDTO;
import dto.ExchangeCurrencyResponseDTO;
import exceptions.BadRequestException;
import exceptions.DatabaseConnectionException;
import exceptions.NotFoundException;
import exceptions.QueryExecuteException;
import service.ExchangeCurrencyService;
import utils.ErrorMessage;
import utils.ExceptionHandler;
import utils.JsonResponsePrinter;
import utils.validators.ExchangeCurrencyValidator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeCurrencyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ExchangeCurrencyValidator validator = new ExchangeCurrencyValidator();
        ExchangeCurrencyService service = new ExchangeCurrencyService();
        ExchangeCurrencyResponseDTO responseDTO;

        try {
            validator.validateQueryParams(request);

            String fromCurrencyCode = request.getParameter("from").toUpperCase();
            String toCurrencyCode = request.getParameter("to").toUpperCase();
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));

            ExchangeCurrencyRequestDTO requestDTO =
                    new ExchangeCurrencyRequestDTO(fromCurrencyCode, toCurrencyCode, amount);

            responseDTO = service.exchangeCurrency(requestDTO);

        } catch (BadRequestException | DatabaseConnectionException | NotFoundException |
                 QueryExecuteException e) {

            ErrorMessage message = ExceptionHandler.handle(e, response);
            JsonResponsePrinter.print(response, message);
            return;
        }

        JsonResponsePrinter.print(response, responseDTO);
    }
}
