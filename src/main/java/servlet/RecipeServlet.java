package servlet;

import dto.product.ProductCreateDto;
import dto.product.ProductDto;
import dto.recipe.RecipeCreateDto;
import dto.recipe.RecipeDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import service.Service;
import service.impl.ProductServiceImpl;
import service.impl.RecipeServiceImpl;

/**
 *
 */
@WebServlet(urlPatterns = {
        "/api/recipes/*",
        "/api/recipes/"
})
public class RecipeServlet extends BaseServlet<RecipeDto, RecipeCreateDto> {

    @Override
    protected Class<RecipeDto> getTypeT() {
        return RecipeDto.class;
    }

    @Override
    protected Class<RecipeCreateDto> getTypeC() {
        return RecipeCreateDto.class;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Service<RecipeDto, RecipeCreateDto> service = (RecipeServiceImpl) getServletContext().getAttribute("recipeService");
        setService(service);
    }
}
