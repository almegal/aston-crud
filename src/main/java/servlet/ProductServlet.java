package servlet;

import dto.ProductDTO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.impl.ProductServiceImpl;

import java.io.IOException;

import static servlet.util.ApiUtils.*;

@WebServlet(urlPatterns = {
        "/api/products/*",
        "/api/products/"
})
public class ProductServlet extends HttpServlet {
    private ProductServiceImpl service;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        service = (ProductServiceImpl) getServletContext().getAttribute("productService");
    }

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

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (isCorrectContentTypeForPost(request)) {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }
        ProductDTO productDTO = convertJsonToProductDTO(request);
        try {
            service.updateByEntity(productDTO);
            sendJsonResponse(response, productDTO);
        } catch (Exception e) {
            throw new ServletException(e);
        }


    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        Long id = Long.parseLong(request.getPathInfo().split("/")[1]);
        ProductDTO productDTO;
        try {
            productDTO = service.deleteById(id);
            sendJsonResponse(response, productDTO);
        } catch (Exception e) {
            throw new ServletException(e);
        }

    }


}
