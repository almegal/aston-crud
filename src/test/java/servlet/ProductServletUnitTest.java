package servlet;

import com.google.gson.Gson;
import exception.ElementNotFoundException;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.impl.ProductServiceImpl;

import java.io.*;

import static config.MockProps.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Сервлет для обработки запросов к продуктам.
 * Обрабатывает запросы GET, POST, PUT и DELETE к конечной точке /api/products.
 * <p>
 * Сценарии тестирования для этого сервлета
 *
 * <h3>Тестирование метода doDelete:</h3>
 *
 * <b>Позитивные сценарии:</b>
 * <ul>
 *     <li>Запрос на удаление существующего продукта:
 *         <ul>
 *             <li>Отправить запрос DELETE /api/products/1</li>
 *             <li>Проверить, что статус ответа - 200 OK</li>
 *             <li>Проверить, что ответ содержит сообщение об успешном удалении</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * <b>Негативные сценарии:</b>
 * <ul>
 *     <li>Запрос на удаление несуществующего продукта:
 *         <ul>
 *             <li>Отправить запрос DELETE /api/products/999</li>
 *             <li>Проверить, что статус ответа - 404 Not Found</li>
 *             <li>Проверить, что ответ содержит сообщение об ошибке "Product not found"</li>
 *         </ul>
 *     </li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class ProductServletUnitTest {
    /**
     * Mock объекты для HttpServletRequest, HttpServletResponse и ProductServiceImpl,
     * которые будут использоваться в тестах.
     */

    @Mock
    private HttpServletRequest mockHttpRequest;
    @Mock
    private HttpServletResponse mockHttpResponse;
    @Mock
    private ProductServiceImpl service;
    @InjectMocks
    private ProductServlet servlet;

    /**
     * Подготовка StringWriter и PrintWriter для записи вывода HttpServletResponse.
     */

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    public void init() throws IOException {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }


    /**
     * Позитивный тест для метода `doGet`, проверяет успешное получение продукта по ID.
     * Проверяет корректность ответа с данными продукта в формате JSON.
     */

    @Test
    @DisplayName("Запрос на получение существующего продукта")
    public void doGetWhenProductExist() throws ServletException, IOException {
        when(service.getById(anyLong())).thenReturn(PRODUCT_DTO_TO_SAVE);
        when(mockHttpResponse.getWriter()).thenReturn(printWriter);
        when(mockHttpRequest.getPathInfo()).thenReturn("/1");

        servlet.doGet(mockHttpRequest, mockHttpResponse);

        String expected = new Gson().toJson(PRODUCT_DTO_TO_SAVE);
        // Проверка результатов
        stringWriter.flush();
        assertAll(
                () -> verify(mockHttpResponse, times(1)).setStatus(HttpServletResponse.SC_OK),
                () -> verify(mockHttpResponse, times(1)).setContentType("application/json")
        );

        assertEquals(expected, stringWriter.toString());

    }

    /**
     * Негативный тест для метода `doGet`, проверяет поведение при запросе несуществующего продукта.
     * Ожидает исключение `ElementNotFoundException` с сообщением "Product not found".
     */

    @Test
    @DisplayName("Запрос на получение несуществующего продукта")
    public void doGetWhenProductNotExist() {
        when(service.getById(1L)).thenThrow(new ElementNotFoundException(ERROR_MESSAGE_NOT_FOUND.formatted(1L)));
        when(mockHttpRequest.getPathInfo()).thenReturn("/1");
        ServletException thrown = assertThrows(ServletException.class,
                () -> servlet.doGet(mockHttpRequest, mockHttpResponse));
        // Проверка результатов
        assertAll(
                () -> verify(mockHttpResponse, never()).setStatus(HttpServletResponse.SC_OK),
                () -> verify(mockHttpResponse, never()).setContentType("application/json")
        );
        assertTrue(thrown.getCause() instanceof ElementNotFoundException);
        assertEquals(ERROR_MESSAGE_NOT_FOUND.formatted(1L), thrown.getCause().getMessage().formatted(1L));
    }

    /**
     * Негативный тест для метода `doGet`, проверяет обработку запроса с некорректным ID продукта.
     * Ожидается ServletException без изменения HTTP статуса и контента.
     */

    @ParameterizedTest
    @ValueSource(strings = {"", "/abc", "/null", "/one", "/"})
    @DisplayName("Запрос с некорректным ID продукта")
    public void doGetWithIncorrectIdParams(String path) {
        when(mockHttpRequest.getPathInfo()).thenReturn(path);
        assertThrows(ServletException.class,
                () -> servlet.doGet(mockHttpRequest, mockHttpResponse));

        verify(mockHttpResponse, never()).setStatus(HttpServletResponse.SC_OK);
        verify(mockHttpResponse, never()).setContentType("application/json");
    }

    /**
     * Позитивный тест для метода `doPost`, проверяет успешное создание нового продукта с корректным JSON.
     * Проверяет корректность ответа с данными созданного продукта в формате JSON.
     */

    @Test
    @DisplayName("Запрос на создание нового продукта")
    public void doPostWithCorrectJson() throws IOException, ServletException {
        InputStream inputStream = new ByteArrayInputStream(PRODUCT_AS_STRING.getBytes());
        ServletInputStream servletInputStream = new MockServletInputStream(inputStream);

        when(mockHttpResponse.getWriter()).thenReturn(printWriter);
        when(mockHttpRequest.getInputStream()).thenReturn(servletInputStream);
        when(mockHttpRequest.getContentType()).thenReturn("application/json");

        servlet.doPost(mockHttpRequest, mockHttpResponse);

        String expected = new Gson().toJson(PRODUCT_DTO_TO_SAVE);

        stringWriter.flush();


        verify(mockHttpResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockHttpResponse, times(1)).setContentType("application/json");
        assertEquals(expected, stringWriter.toString());
    }

    /**
     * Негативниый тест для метода `doPost`, проверяет что выбрасывается исключение при создание нового продукта с некорректным JSON.
     * Проверяет что возращается SQLException.
     */
    @Test
    @DisplayName("Запрос на создание нового продукта c некорректным json")
    public void doPostWhenJsonDataIncorrect() {

    }

    /**
     * <h3>Тестирование метода doPut:</h3>
     *
     * <b>Позитивные сценарии:</b>
     * <ul>
     *     <li>Запрос на обновление существующего продукта:
     *         <ul>
     *             <li>Отправить запрос PUT /api/products/1 с корректным JSON телом запроса</li>
     *             <li>Проверить, что статус ответа - 200 OK</li>
     *             <li>Проверить, что ответ содержит корректный JSON с данными обновленного продукта</li>
     *         </ul>
     *     </li>
     * </ul>
     */
    @Test
    @DisplayName("Запрос на обновление существующего продукта")
    public void doPutWhenProductIsExist() {

    }
    /** <b>Негативные сценарии:</b>
     * <ul>
     *     <li>Запрос на обновление несуществующего продукта:
     *         <ul>
     *             <li>Отправить запрос PUT /api/products/999 с корректным JSON телом запроса</li>
     *             <li>Проверить, что статус ответа - 404 Not Found</li>
     *             <li>Проверить, что ответ содержит сообщение об ошибке "Product not found"</li>
     *         </ul>
     *     </li>
     *     <li>Запрос с некорректным JSON телом запроса:
     *         <ul>
     *             <li>Отправить запрос PUT /api/products/1 с некорректным JSON телом</li>
     *             <li>Проверить, что статус ответа - 400 Bad Request</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * */


    /**
     * Мок класс для имитации ServletInputStream, используемый в тестах для передачи данных запроса.
     */

    private static class MockServletInputStream extends ServletInputStream {

        private final InputStream inputStream;

        public MockServletInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public boolean isFinished() {
            try {
                return inputStream.available() == 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }
}
