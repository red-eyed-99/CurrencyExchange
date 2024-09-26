package utils;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JsonResponsePrinter {
    public static void print(HttpServletResponse response, Object obj) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        String jsonResponse = new Gson().toJson(obj);

        out.print(jsonResponse);
        out.flush();
    }
}
