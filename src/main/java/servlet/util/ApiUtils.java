package servlet.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dto.ProductDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ApiUtils {

    static public ProductDTO convertJsonToProductDTO(HttpServletRequest request) throws ServletException {
        try {
            Scanner scanner = new Scanner(request.getInputStream(), StandardCharsets.UTF_8);
            String jsonData = scanner.useDelimiter("\\A").next();
            scanner.close();
            return new GsonBuilder()
                    .serializeNulls()
                    .create()
                    .fromJson(jsonData, ProductDTO.class);
        } catch (JsonSyntaxException | IOException e) {
            throw new ServletException(e);
        }
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
