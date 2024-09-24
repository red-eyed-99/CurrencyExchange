package servlets;

import com.google.gson.Gson;
import dao.CurrencyDAO;
import exceptions.DataReadException;
import exceptions.DatabaseConnectionException;
import models.Currency;
import utils.ResponseMessage;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/currencies")
public class CurrencyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        CurrencyDAO currencyDAO = new CurrencyDAO();

        List<Currency> currencies;

        try {
            currencies = currencyDAO.getAll();
        } catch (DatabaseConnectionException | DataReadException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            int statusCode = response.getStatus();

            ResponseMessage message = new ResponseMessage(statusCode, e.getMessage());
            String jsonResponse = new Gson().toJson(message);

            out.print(jsonResponse);
            out.flush();
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);

        String jsonResponse = new Gson().toJson(currencies);

        out.print(jsonResponse);
        out.flush();
    }
}
