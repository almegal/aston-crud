package filter;

import com.google.gson.JsonSyntaxException;
import exception.ElementNotFoundException;
import exception.HttpBadRequestException;
import exception.HttpMediaTypeException;
import exception.ServiceException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.BadRequestException;

import java.io.IOException;

@WebFilter("/*")  // Аннотация для указания, что этот фильтр применяется ко всем запросам
public class ErrorFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Инициализация фильтра (можно оставить пустым)
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        // Деструктор фильтра (можно оставить пустым)
        Filter.super.destroy();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        try {
            // Передача запроса и ответа следующему элементу в цепочке фильтров
            filterChain.doFilter(httpRequest, httpResponse);
        } catch (ServletException error) {
            // Перехват ServletException и обработка его причины
            Throwable cause = error.getCause();
            if (cause instanceof ElementNotFoundException) {
                // Обработка исключения ElementNotFoundException
                handleError(httpResponse, HttpServletResponse.SC_NOT_FOUND, cause.getMessage());

            } else if (cause instanceof ServiceException) {
                // Обработка исключения ServiceException
                handleError(httpResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, cause.getMessage());

            } else if (cause instanceof JsonSyntaxException
                    || cause instanceof IOException
                    || cause instanceof HttpBadRequestException) {
                handleError(httpResponse, HttpServletResponse.SC_BAD_REQUEST, cause.getMessage());
            } else if (cause instanceof HttpMediaTypeException) {

                handleError(httpResponse, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, cause.getMessage());
            } else {
                // Обработка всех остальных исключений
                handleError(httpResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, cause.getMessage());
            }
        }
    }

    /**
     * Обрабатывает ошибки, устанавливая соответствующий HTTP-статус и сообщение об ошибке в формате JSON.
     *
     * @param response   HTTP-ответ
     * @param statusCode HTTP-статус код ошибки
     * @param message    Сообщение об ошибке
     * @throws IOException Если произошла ошибка при записи ответа
     */
    private void handleError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
        response.getWriter().flush();
    }
}
