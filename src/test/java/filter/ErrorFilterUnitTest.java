package filter;

import com.google.gson.JsonSyntaxException;
import exception.ElementNotFoundException;
import exception.HttpBadRequestException;
import exception.HttpMediaTypeException;
import exception.ServiceException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для фильтра ErrorFilter.
 * Тестирует корректность обработки исключений и установку соответствующих кодов статуса HTTP.
 */
@ExtendWith(MockitoExtension.class)
public class ErrorFilterUnitTest {
    private StringWriter STRING_WRITER;
    private PrintWriter PRINT_WRITER;

    @Mock
    private HttpServletRequest mockHttpRequest;
    @Mock
    private HttpServletResponse mockHttpResponse;
    @Mock
    private FilterChain mockFilterChain;
    @InjectMocks
    private ErrorFilter errorFilter;

    /**
     * Метод для предоставления аргументов для параметризованных тестов.
     * Возвращает поток аргументов, каждый из которых содержит исключение и ожидаемый код статуса HTTP.
     *
     * @return Stream аргументов
     */
    public static Stream<Arguments> argsForFilterHandleExceptionTest() {
        return Stream.of(
                Arguments.of(new ElementNotFoundException(""), HttpServletResponse.SC_NOT_FOUND),
                Arguments.of(new HttpMediaTypeException(""), HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE),
                Arguments.of(new ServiceException(""), HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
                Arguments.of(new JsonSyntaxException(""), HttpServletResponse.SC_BAD_REQUEST),
                Arguments.of(new HttpBadRequestException(""), HttpServletResponse.SC_BAD_REQUEST)
        );
    }

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
     * Проверяет корректность ответа фильтра.
     * Проверяет статус, тип контента, кодировку и содержимое ответа.
     *
     * @param statusCode ожидаемый код статуса HTTP
     * @param expected   ожидаемое содержимое ответа
     * @throws IOException если возникает ошибка ввода/вывода
     */
    private void verifyResponse(int statusCode, String expected) throws IOException {
        String actual = STRING_WRITER.toString();
        mockHttpResponse.getWriter().flush();
        verify(mockHttpResponse, times(1)).setStatus(statusCode);
        verify(mockHttpResponse, times(1)).setContentType("application/json");
        verify(mockHttpResponse, times(1)).setCharacterEncoding("UTF-8");
        assertEquals(expected, actual);
    }

    /**
     * Преобразует сообщение об ошибке в формат JSON.
     *
     * @param e сообщение об ошибке
     * @return строка в формате JSON
     */
    private String convertToJsonFormatError(String e) {
        return "{\"error\": \"%s\"}".formatted(e);
    }

    /**
     * Параметризованный тест, который проверяет корректность обработки различных исключений фильтром.
     * Тестирует установку соответствующих кодов статуса HTTP и формирование корректного JSON ответа.
     *
     * @param exception  исключение, которое должно быть обработано фильтром
     * @param statusCode ожидаемый код статуса HTTP
     * @throws ServletException если возникает ошибка сервлета
     * @throws IOException      если возникает ошибка ввода/вывода
     */
    @ParameterizedTest
    @MethodSource("argsForFilterHandleExceptionTest")
    @DisplayName("Проверка корректности обработки исключений фильтром")
    public void filterHandleExceptionCorrect(Throwable exception, int statusCode) throws ServletException, IOException {
        // Настройка мока: выбросить ServletException с указанным исключением
        doThrow(new ServletException(exception)).when(mockFilterChain).doFilter(mockHttpRequest, mockHttpResponse);
        // Настройка мока HttpServletResponse для возврата PrintWriter
        when(mockHttpResponse.getWriter()).thenReturn(PRINT_WRITER);

        // Вызов тестируемого метода
        errorFilter.doFilter(mockHttpRequest, mockHttpResponse, mockFilterChain);

        // Ожидаемый результат в формате JSON
        String expected = convertToJsonFormatError(exception.getMessage());

        // Проверка результатов
        verifyResponse(statusCode, expected);
    }
}
