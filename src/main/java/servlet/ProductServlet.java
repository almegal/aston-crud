package servlet;

import dto.product.ProductCreateDto;
import dto.product.ProductDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import service.Service;
import service.impl.ProductServiceImpl;

@WebServlet(urlPatterns = {
        "/api/products/*",
        "/api/products/"
})
public class ProductServlet extends BaseServlet<ProductDto, ProductCreateDto> {

    @Override
    protected Class<ProductDto> getTypeT() {
        return ProductDto.class;
    }

    @Override
    protected Class<ProductCreateDto> getTypeC() {
        return ProductCreateDto.class;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Service<ProductDto, ProductCreateDto> service = (ProductServiceImpl) getServletContext().getAttribute("productService");
        setService(service);
    }

}
