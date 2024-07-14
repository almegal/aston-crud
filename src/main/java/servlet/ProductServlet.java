package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ProductDTO;
import exception.HttpMediaTypeException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.impl.ProductServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;

import static servlet.util.ApiUtils.*;

/**
 * Сервлет для обработки HTTP-запросов, связанных с продуктами.
 * Поддерживает операции GET, POST, PUT и DELETE для управления продуктами.
 */
@WebServlet(urlPatterns = {
        "/api/products/*",
        "/api/products/"
})
public class ProductServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private ProductServiceImpl service;

    /**
     * Инициализация сервлета. Получает экземпляр ProductServiceImpl из контекста сервлета.
     *
     * @param config конфигурация сервлета
     * @throws ServletException если происходит ошибка инициализации
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        service = (ProductServiceImpl) getServletContext().getAttribute("productService");
    }

    /**
     * Обработка GET-запросов для получения продукта по его идентификатору.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws ServletException если происходит ошибка при обработке запроса
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            String path = splitPathInfo(request);

            Long id = Long.parseLong(path);

            ProductDTO productDTO = service.getById(id);

            sendJsonResponse(response, productDTO);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Обработка POST-запросов для создания нового продукта.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws ServletException если происходит ошибка при обработке запроса
     * @throws IOException      если происходит ошибка ввода-вывода
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (isCorrectContentTypeForPost(request)) {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }
        ProductDTO productDTO = convertJsonToProductDTO(request);
        try {
            service.save(productDTO);
            sendJsonResponse(response, productDTO);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Обработка PUT-запросов для обновления существующего продукта.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws ServletException если происходит ошибка при обработке запроса
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (isCorrectContentTypeForPost(request)) {
            String errorMessage = "content type: %S not support for PUT method".formatted(request.getContentType());
            HttpMediaTypeException e = new HttpMediaTypeException(errorMessage);
            throw new ServletException(e);
        }
        ProductDTO productDTO = convertJsonToProductDTO(request);
        try {
            productDTO = service.updateByEntity(productDTO);
            sendJsonResponse(response, productDTO);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Обработка DELETE-запросов для удаления продукта по его идентификатору.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws ServletException если происходит ошибка при обработке запроса
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ProductDTO productDTO;
        try {
            String path = splitPathInfo(request);

            Long id = Long.parseLong(path);

            productDTO = service.deleteById(id);

            sendJsonResponse(response, productDTO);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Отправляет JSON-ответ с объектом ProductDTO.
     *
     * @param response   HTTP-ответ
     * @param productDTO объект ProductDTO для отправки
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void sendJsonResponse(HttpServletResponse response, ProductDTO productDTO) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print(gson.toJson(productDTO));
        writer.flush();
    }
}
