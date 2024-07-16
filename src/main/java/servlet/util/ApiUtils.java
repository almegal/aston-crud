package servlet.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import exception.HttpBadRequestException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Вспомогательный класс для работы с API, предоставляющий методы для обработки HTTP-запросов.
 */
public class ApiUtils {

    /**
     * Преобразует JSON-данные из запроса в объект ProductDTO.
     *
     * @param request HTTP-запрос, содержащий JSON-данные
     * @return объект ProductDTO, созданный из JSON-данных
     * @throws ServletException если происходит ошибка при чтении или парсинге JSON-данных
     */
    public static <T> T convertJsonToProductDTO(HttpServletRequest request, Class<T> typeClass) throws ServletException {
        try {
            Scanner scanner = new Scanner(request.getInputStream(), StandardCharsets.UTF_8);
            String jsonData = scanner.useDelimiter("\\A").next();
            scanner.close();
            return new GsonBuilder()
                    .serializeNulls()
                    .create()
                    .fromJson(jsonData, typeClass);
        } catch (JsonSyntaxException | IOException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Разделяет pathInfo запроса на части и возвращает идентификатор продукта.
     *
     * @param request HTTP-запрос
     * @return строка, содержащая идентификатор продукта
     * @throws HttpBadRequestException если pathInfo пуст или содержит более двух частей
     */
    public static String splitPathInfo(HttpServletRequest request) {
        if (request.getPathInfo() == null) {
            throw new HttpBadRequestException("Path info is null");
        }
        String[] paths = request.getPathInfo().split("/");
        if (paths.length > 2) {
            throw new HttpBadRequestException("Invalid path info");
        }
        return paths[1];
    }

    /**
     * Проверяет, является ли тип содержимого запроса корректным для POST-запроса.
     *
     * @param req HTTP-запрос
     * @return true, если тип содержимого корректен, иначе false
     */
    public static boolean isCorrectContentTypeForPost(HttpServletRequest req) {
        final String JSON_CONTENT_TYPE = "application/json";
        if (req.getContentType() == null) {
            return true;
        }
        return !req.getContentType().equals(JSON_CONTENT_TYPE);
    }
}
