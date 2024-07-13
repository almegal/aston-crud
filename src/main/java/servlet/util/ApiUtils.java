package servlet.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ProductDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ApiUtils {
    static private final Gson gson = new GsonBuilder().serializeNulls().create();

    static public ProductDTO convertJsonToProductDTO(HttpServletRequest request) throws IOException {
        Scanner scanner = new Scanner(request.getInputStream(), StandardCharsets.UTF_8);
        String jsonData = scanner.useDelimiter("\\A").next();
        scanner.close();
        return gson.fromJson(jsonData, ProductDTO.class);
    }

    static public void sendJsonResponse(HttpServletResponse response, ProductDTO productDTO) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print(gson.toJson(productDTO));
        writer.flush();
    }

    static public String splitPathInfo(HttpServletRequest request) throws ServletException {
        if (request.getPathInfo() == null) {
            throw new ServletException("");
        }
        String[] paths = request.getPathInfo().split("/");
        if (paths.length > 2) {
            throw new ServletException("");
        }
        return paths[1];
    }

    static public boolean isCorrectContentTypeForPost(HttpServletRequest req) {
        final String JSON_CONTENT_TYPE = "application/json";
        if (req.getContentType() == null) {
            return true;
        }
        return !req.getContentType().equals(JSON_CONTENT_TYPE);
    }

}
