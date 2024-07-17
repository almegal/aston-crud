package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import exception.HttpMediaTypeException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static servlet.util.ApiUtils.isCorrectContentTypeForPost;
import static servlet.util.ApiUtils.splitPathInfo;

/**
 * Базовый сервлет, обеспечивающий общую функциональность для обработки HTTP-запросов.
 *
 * @param <T> тип DTO ответа
 * @param <C> тип DTO запроса
 */
public abstract class BaseServlet<T, C> extends HttpServlet {
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    protected Service<T, C> service;

    /**
     * Абстрактный метод для получения класса типа T.
     *
     * @return класс типа T
     */
    protected abstract Class<T> getTypeT();

    /**
     * Абстрактный метод для получения класса типа C.
     *
     * @return класс типа C
     */
    protected abstract Class<C> getTypeC();

    /**
     * Инициализация сервлета.
     *
     * @param config конфигурация сервлета
     * @throws ServletException если произошла ошибка при инициализации
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**
     * Обработка GET-запросов.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws ServletException если произошла ошибка в процессе обработки
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            String path = splitPathInfo(request);
            Long id = Long.parseLong(path);
            T dto = service.getById(id);
            sendJsonResponse(response, dto);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Обработка POST-запросов.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws ServletException если произошла ошибка в процессе обработки
     * @throws IOException      если произошла ошибка ввода-вывода
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isCorrectContentTypeForPost(request)) { // Проверка, поддерживается ли тип содержимого запроса
            String errorMessage = String.format("Content type: %s not supported for PUT method", request.getContentType());
            throw new ServletException(new HttpMediaTypeException(errorMessage));
        }
        try {
            C dtoCreate = convertJsonToDTO(request, getTypeC()); // Преобразование JSON-запроса в DTO
            T dto = service.save(dtoCreate);
            sendJsonResponse(response, dto); // Отправка JSON-ответа
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Обработка PUT-запросов.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws ServletException если произошла ошибка в процессе обработки
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (!isCorrectContentTypeForPost(request)) { // Проверка, поддерживается ли тип содержимого запроса
            String errorMessage = String.format("Content type: %s not supported for PUT method", request.getContentType());
            throw new ServletException(new HttpMediaTypeException(errorMessage));
        }
        try {
            T dto = convertJsonToDTO(request, getTypeT()); // Преобразование JSON-запроса в DTO
            T updateDto = service.updateByEntity(dto);
            sendJsonResponse(response, updateDto); // Отправка JSON-ответа
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Обработка DELETE-запросов.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @throws ServletException если произошла ошибка в процессе обработки
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            String path = splitPathInfo(request);
            Long id = Long.parseLong(path);
            T dto = service.deleteById(id);
            sendJsonResponse(response, dto); // Отправка JSON-ответа
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Устанавливает сервис для сервлета.
     *
     * @param service сервис для взаимодействия с бизнес-логикой
     */
    protected void setService(Service<T, C> service) {
        this.service = service;
    }

    /**
     * Отправляет JSON-ответ клиенту.
     *
     * @param response HTTP-ответ
     * @param dto      объект DTO для отправки
     * @throws IOException если произошла ошибка ввода-вывода
     */
    private void sendJsonResponse(HttpServletResponse response, T dto) throws IOException {
        // Установка заголовка и типа ответа
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        // Записиваем в ответ объект
        PrintWriter writer = response.getWriter();
        writer.print(gson.toJson(dto));
        // Заркываем объект и отправляем
        writer.flush();
    }

    /**
     * Преобразует JSON-запрос в объект DTO.
     *
     * @param request   HTTP-запрос
     * @param typeClass класс типа DTO
     * @param <E>       тип DTO
     * @return объект DTO
     * @throws ServletException если произошла ошибка при преобразовании
     */
    private <E> E convertJsonToDTO(HttpServletRequest request, Class<E> typeClass) throws ServletException {
        try (Scanner scanner = new Scanner(request.getInputStream(), StandardCharsets.UTF_8)) {
            String jsonData = scanner.useDelimiter("\\A").next(); // Чтение всего содержимого запроса в строку
            return gson.fromJson(jsonData, typeClass); // Преобразование строки JSON в объект DTO
        } catch (JsonSyntaxException | IOException e) {
            throw new ServletException(e);
        }
    }

}
