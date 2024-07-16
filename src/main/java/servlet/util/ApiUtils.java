package servlet.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import exception.HttpBadRequestException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Вспомогательный класс для работы с API, предоставляющий методы для обработки HTTP-запросов.
 */
public class ApiUtils {
    public static final Gson gson = new GsonBuilder().serializeNulls().create();

    /**
     *
     */
    public static <T> T convertJsonToDTO(HttpServletRequest request, Class<T> typeClass) throws ServletException {
        try {
            //
            Scanner scanner = new Scanner(request.getInputStream(), StandardCharsets.UTF_8);
            //
            String jsonData = scanner.useDelimiter("\\A").next();
            scanner.close();
            //
            return gson.fromJson(jsonData, typeClass);
        } catch (JsonSyntaxException | IOException e) {
            throw new ServletException(e);
        }
    }

    /**
     *
     */
    public static String splitPathInfo(HttpServletRequest request) {
        //
        if (request.getPathInfo() == null) {
            throw new HttpBadRequestException("Path info is null");
        }
        //
        String[] paths = request.getPathInfo().split("/");
        if (paths.length > 2) {
            throw new HttpBadRequestException("Invalid path info");
        }
        return paths[1];
    }

    /**
     *
     */
    public static boolean isCorrectContentTypeForPost(HttpServletRequest req) {
        final String JSON_CONTENT_TYPE = "application/json";
        if (req.getContentType() == null) {
            return true;
        }
        return !req.getContentType().equals(JSON_CONTENT_TYPE);
    }

    /**
     *
     */
    public static <T> void sendJsonResponse(HttpServletResponse response, T dto) throws IOException {
        //
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        //
        PrintWriter writer = response.getWriter();
        writer.print(gson.toJson(dto));
        //
        writer.flush();
    }
}
