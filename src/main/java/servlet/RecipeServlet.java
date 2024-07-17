package servlet;

import dto.recipe.RecipeCreateDto;
import dto.recipe.RecipeDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import service.Service;
import service.impl.RecipeServiceImpl;

/**
 * Сервлет для обработки HTTP-запросов, связанных с рецептами.
 * Наследует базовые методы для работы с сущностями.
 */
@WebServlet(urlPatterns = {
        "/api/recipes/*",
        "/api/recipes/"
})
public class RecipeServlet extends BaseServlet<RecipeDto, RecipeCreateDto> {

    /**
     * Возвращает класс типа T (RecipeDto) для десериализации JSON.
     *
     * @return класс RecipeDto
     */
    @Override
    protected Class<RecipeDto> getTypeT() {
        return RecipeDto.class;
    }

    /**
     * Возвращает класс типа C (RecipeCreateDto) для десериализации JSON.
     *
     * @return класс RecipeCreateDto
     */
    @Override
    protected Class<RecipeCreateDto> getTypeC() {
        return RecipeCreateDto.class;
    }

    /**
     * Инициализирует сервлет и устанавливает сервис для работы с рецептами.
     *
     * @param config конфигурация сервлета
     * @throws ServletException если произошла ошибка при инициализации
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // Получает сервис из контекста сервлета
        Service<RecipeDto, RecipeCreateDto> service = (RecipeServiceImpl) getServletContext().getAttribute("recipeService");
        // Устанавливает сервис для базового сервлета
        setService(service);
    }
}
