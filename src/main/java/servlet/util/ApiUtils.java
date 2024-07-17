package servlet.util;

import exception.HttpBadRequestException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Вспомогательный класс для работы с API, предоставляющий методы для обработки HTTP-запросов.
 */
public class ApiUtils {

    /**
     * Извлекает часть пути из запроса и возвращает ее.
     * Проверяет наличие и корректность пути.
     *
     * @param request HTTP-запрос
     * @return часть пути запроса
     * @throws HttpBadRequestException если путь отсутствует или имеет некорректный формат
     */
    public static String splitPathInfo(HttpServletRequest request) {
        // Проверка, что информация о пути не является null
        if (request.getPathInfo() == null) {
            throw new HttpBadRequestException("Path info is null");
        }
        // Разделение пути на части
        String[] paths = request.getPathInfo().split("/");
        // Проверка, что путь содержит не более двух частей
        if (paths.length > 2) {
            throw new HttpBadRequestException("Invalid path info");
        }
        // Возвращает вторую часть пути (первая часть после символа "/")
        return paths[1];
    }

    /**
     * Проверяет, является ли тип содержимого запроса корректным для метода POST.
     *
     * @param req HTTP-запрос
     * @return true, если тип содержимого является "application/json", иначе false
     */
    public static boolean isCorrectContentTypeForPost(HttpServletRequest req) {
        final String JSON_CONTENT_TYPE = "application/json";
        // Проверка, что тип содержимого не является null
        if (req.getContentType() == null) {
            return false;
        }
        // Проверка, что тип содержимого является "application/json"
        return req.getContentType().equals(JSON_CONTENT_TYPE);
    }
}
