package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dto.product.ProductCreateDto;
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
 * Тестирование сервлета для обработки запросов к продуктам.
 * Обрабатывает запросы GET, POST, PUT и DELETE к конечной точке /api/products.
 */
@ExtendWith(MockitoExtension.class)
public class ProductServletUnitTest {
    private StringWriter STRING_WRITER;
    private PrintWriter PRINT_WRITER;

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
     * Инициализация перед каждым тестом.
     * Настраивает объекты StringWriter и PrintWriter.
     */
    @BeforeEach
    public void init() {
        STRING_WRITER = new StringWriter();
        PRINT_WRITER = new PrintWriter(STRING_WRITER);
    }

    /**
     * Настраивает мок HttpServletRequest для возврата пути запроса.
     *
     * @param path путь запроса
     */
    private void setupMockRequestPath(String path) {
        when(mockHttpRequest.getPathInfo()).thenReturn(path);
    }

    /**
     * Настраивает мок HttpServletRequest для возврата входного потока с данными JSON.
     *
     * @param jsonData данные JSON для запроса
     * @throws IOException если возникает ошибка ввода/вывода
     */
    private void setupMockRequestInputStream(String jsonData) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(jsonData.getBytes());
        ServletInputStream servletInputStream = new MockServletInputStream(inputStream);
        when(mockHttpRequest.getInputStream()).thenReturn(servletInputStream);
        when(mockHttpRequest.getContentType()).thenReturn("application/json");
    }

    /**
     * Проверяет корректность ответа сервлета.
     *
     * @param expected ожидаемый JSON результат
     */
    private void verifyResponse(String expected) {
        String actual = STRING_WRITER.toString();
        STRING_WRITER.flush();
        assertEquals(expected, actual);
        verify(mockHttpResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockHttpResponse).setContentType("application/json");
    }

    /**
     * Позитивный тест для метода doGet, проверяет успешное получение продукта по ID.
     * Проверяет корректность ответа с данными продукта в формате JSON.
     */
    @Test
    @DisplayName("Запрос на получение существующего продукта")
    public void doGetWhenProductExist() throws ServletException, IOException {
        // Настройка поведения мока: вернуть продукт при запросе по ID
        when(service.getById(anyLong())).thenReturn(PRODUCT_DTO_RESPONSE);
        // Настройка мока HttpServletResponse для возврата PrintWriter
        when(mockHttpResponse.getWriter()).thenReturn(PRINT_WRITER);
        // Настройка мока HttpServletRequest для возврата пути запроса
        setupMockRequestPath("/1");

        // Вызов тестируемого метода
        servlet.doGet(mockHttpRequest, mockHttpResponse);

        // Получение ожидаемого результата в формате JSON
        String expected = new Gson().toJson(PRODUCT_DTO_RESPONSE);
        // Проверка результатов
        verifyResponse(expected);
    }

    /**
     * Негативный тест для метода doGet, проверяет поведение при запросе несуществующего продукта.
     * Ожидает исключение ElementNotFoundException с сообщением "Product not found".
     */
    @Test
    @DisplayName("Запрос на получение несуществующего продукта")
    public void doGetWhenProductNotExist() {
        // Настройка поведения мока: выбросить исключение при запросе продукта по ID
        when(service.getById(1L)).thenThrow(new ElementNotFoundException(ERROR_MESSAGE_NOT_FOUND.formatted(1L)));
        // Настройка мока HttpServletRequest для возврата пути запроса
        setupMockRequestPath("/1");

        // Вызов тестируемого метода и проверка выброса исключения
        ServletException thrown = assertThrows(ServletException.class,
                () -> servlet.doGet(mockHttpRequest, mockHttpResponse));

        // Проверка результатов
        assertAll(
                () -> verify(mockHttpResponse, never()).setStatus(HttpServletResponse.SC_OK),
                () -> verify(mockHttpResponse, never()).setContentType("application/json")
        );

        // Проверка типа и сообщения исключения
        assertTrue(thrown.getCause() instanceof ElementNotFoundException);
        assertEquals(ERROR_MESSAGE_NOT_FOUND.formatted(1L), thrown.getCause().getMessage());
    }

    /**
     * Негативный тест для метода doGet, проверяет обработку запроса с некорректным ID продукта.
     * Ожидается ServletException без изменения HTTP статуса и контента.
     *
     * @param path некорректный путь для ID продукта
     */
    @ParameterizedTest
    @ValueSource(strings = {"", "/abc", "/null", "/one", "/"})
    @DisplayName("Запрос с некорректным ID продукта")
    public void doGetWithIncorrectIdParams(String path) {
        // Настройка мока HttpServletRequest для возврата некорректного пути запроса
        setupMockRequestPath(path);

        // Вызов тестируемого метода и проверка выброса исключения
        assertThrows(ServletException.class,
                () -> servlet.doGet(mockHttpRequest, mockHttpResponse));

        // Проверка, что статус и тип контента не изменились
        verify(mockHttpResponse, never()).setStatus(HttpServletResponse.SC_OK);
        verify(mockHttpResponse, never()).setContentType("application/json");
    }

    /**
     * Позитивный тест для метода doPost, проверяет успешное создание нового продукта с корректным JSON.
     * Проверяет корректность ответа с данными созданного продукта в формате JSON.
     */
    @Test
    @DisplayName("Запрос на создание нового продукта")
    public void doPostWithCorrectJson() throws IOException, ServletException {
        // Настройка входного потока с корректными данными JSON для обновления
        setupMockRequestInputStream(PRODUCT_AS_STRING);
        // Настройка моков HttpServletResponse
        when(mockHttpResponse.getWriter()).thenReturn(PRINT_WRITER);
        when(service.save(any(ProductCreateDto.class))).thenReturn(PRODUCT_DTO_RESPONSE);
        // Вызов тестируемого метода
        servlet.doPost(mockHttpRequest, mockHttpResponse);
        // Получение ожидаемого результата в формате JSON
        String expected = new Gson().toJson(PRODUCT_DTO_RESPONSE);
        // Проверка результатов
        verifyResponse(expected);
    }

    /**
     * Негативный тест для метода doPost, проверяет что выбрасывается исключение при создании нового продукта с некорректным JSON.
     * Проверяет, что возвращается ServletException с типом JsonSyntaxException.
     */
    @Test
    @DisplayName("Запрос на создание нового продукта с некорректным JSON")
    public void doPostWhenJsonDataIncorrect() throws IOException {
        // Настройка моков HttpServletRequest
        setupMockRequestInputStream("invalid json");

        // Вызов тестируемого метода и проверка выброса исключения
        ServletException thrown = assertThrows(ServletException.class,
                () -> servlet.doPost(mockHttpRequest, mockHttpResponse));

        // Проверка типа исключения
        assertTrue(thrown.getCause().getCause() instanceof JsonSyntaxException);
    }

    /**
     * Позитивный тест для метода doPut, проверяет обновление существующего продукта.
     */
    @Test
    @DisplayName("Запрос на обновление существующего продукта")
    public void doPutWhenProductIsExist() throws IOException, ServletException {

        // Настройка моков HttpServletRequest
        when(mockHttpResponse.getWriter()).thenReturn(PRINT_WRITER);
        setupMockRequestInputStream(PRODUCT_AS_STRING);

        // Настройка поведения мока: вернуть обновленный продукт при запросе по ID
        when(service.updateByEntity(PRODUCT_DTO_RESPONSE)).thenReturn(PRODUCT_DTO_RESPONSE);

        // Вызов тестируемого метода
        servlet.doPut(mockHttpRequest, mockHttpResponse);

        // Получение ожидаемого результата в формате JSON
        String expected = new Gson().toJson(PRODUCT_DTO_RESPONSE);
        // Проверка результатов
        verifyResponse(expected);
    }

    /**
     * Негативный тест для метода doPut, проверяет обновление несуществующего продукта.
     * Выбрасывает исключение ServletException с типом ElementNotFoundException.
     */
    @Test
    @DisplayName("Запрос на обновление, если продукта не существует")
    public void doPutWhenProductIsNotExist() throws IOException {
        // Настройка поведения мока: выбросить исключение при запросе продукта по ID
        when(service.updateByEntity(PRODUCT_DTO_RESPONSE))
                .thenThrow(new ElementNotFoundException(ERROR_MESSAGE_NOT_FOUND.formatted(MOCK_PRODUCT.getId())));

        // Настройка моков HttpServletRequest
        setupMockRequestInputStream(PRODUCT_AS_STRING);

        // Вызов тестируемого метода и проверка выброса исключения
        ServletException thrown = assertThrows(ServletException.class,
                () -> servlet.doPut(mockHttpRequest, mockHttpResponse));

        // Проверка результатов
        assertAll(
                () -> verify(mockHttpResponse, never()).setStatus(HttpServletResponse.SC_OK),
                () -> verify(mockHttpResponse, never()).setContentType("application/json")
        );

        // Проверка типа и сообщения исключения
        assertTrue(thrown.getCause() instanceof ElementNotFoundException);
        assertEquals(ERROR_MESSAGE_NOT_FOUND.formatted(MOCK_PRODUCT.getId()), thrown.getCause().getMessage());
    }

    /**
     * Позитивный тест для метода doDelete, проверяет успешное удаление продукта по ID.
     * Проверяет корректность ответа с данными удаленного продукта в формате JSON.
     * Проверяет, что статус ответа - 200 OK.
     */
    @Test
    @DisplayName("Запрос на удаление существующего продукта")
    public void doDeleteWhenProductExist() throws IOException, ServletException {
        // Настройка моков
        setupMockRequestPath("/1");
        when(mockHttpResponse.getWriter()).thenReturn(PRINT_WRITER);
        when(service.deleteById(1L)).thenReturn(PRODUCT_DTO_RESPONSE);

        // Вызов тестируемого метода
        servlet.doDelete(mockHttpRequest, mockHttpResponse);

        // Получение ожидаемого результата в формате JSON
        String expected = new Gson().toJson(PRODUCT_DTO_RESPONSE);
        // Проверка результатов
        verifyResponse(expected);
    }

    /**
     * Негативный тест для метода doDelete, проверяет выброс исключения при удалении несуществующего продукта.
     * Проверяет выброс исключения ServletException с типом ElementNotFoundException.
     */
    @Test
    @DisplayName("Запрос на удаление продукта, который не существует")
    public void doDeleteWhenProductIsNotExist() {
        // Настройка моков
        setupMockRequestPath("/1");
        when(service.deleteById(1L))
                .thenThrow(new ElementNotFoundException(ERROR_MESSAGE_NOT_FOUND.formatted(1L)));

        // Вызов тестируемого метода и проверка выброса исключения
        ServletException thrown = assertThrows(ServletException.class,
                () -> servlet.doDelete(mockHttpRequest, mockHttpResponse));

        // Проверка результатов
        assertAll(
                () -> verify(mockHttpResponse, never()).setStatus(HttpServletResponse.SC_OK),
                () -> verify(mockHttpResponse, never()).setContentType("application/json")
        );

        // Проверка типа и сообщения исключения
        assertTrue(thrown.getCause() instanceof ElementNotFoundException);
        assertEquals(ERROR_MESSAGE_NOT_FOUND.formatted(1L), thrown.getCause().getMessage());
    }

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
                System.out.println(e.getMessage());
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
