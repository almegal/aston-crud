package servlet;

import dto.product.ProductCreateDto;
import dto.product.ProductDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import service.Service;
import service.impl.ProductServiceImpl;

/**
 * Сервлет для обработки HTTP-запросов, связанных с продуктами.
 * Наследует базовые методы для работы с сущностями.
 */
@WebServlet(urlPatterns = {
        "/api/products/*",
        "/api/products/"
})
public class ProductServlet extends BaseServlet<ProductDto, ProductCreateDto> {

    /**
     * Возвращает класс типа T (ProductDto) для десериализации JSON.
     *
     * @return класс ProductDto
     */
    @Override
    protected Class<ProductDto> getTypeT() {
        return ProductDto.class;
    }

    /**
     * Возвращает класс типа C (ProductCreateDto) для десериализации JSON.
     *
     * @return класс ProductCreateDto
     */
    @Override
    protected Class<ProductCreateDto> getTypeC() {
        return ProductCreateDto.class;
    }

    /**
     * Инициализирует сервлет и устанавливает сервис для работы с продуктами.
     *
     * @param config конфигурация сервлета
     * @throws ServletException если произошла ошибка при инициализации
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // Получает сервис из контекста сервлета
        Service<ProductDto, ProductCreateDto> service = (ProductServiceImpl) getServletContext().getAttribute("productService");
        // Устанавливает сервис для базового сервлета
        setService(service);
    }
}
