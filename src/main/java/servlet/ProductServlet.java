package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ProductDTO;
import exception.HttpMediaTypeException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.impl.ProductServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;

import static servlet.util.ApiUtils.*;

@WebServlet(urlPatterns = {
        "/api/products/*",
        "/api/products/"
})
public class ProductServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().serializeNulls().create();
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
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (isCorrectContentTypeForPost(request)) {
            String errorMessage = "content type: %S not support for PUT method".formatted(request.getContentType());
            HttpMediaTypeException e = new HttpMediaTypeException(errorMessage);
            throw new ServletException(e);
        }
        ProductDTO productDTO = convertJsonToProductDTO(request);
        try {
            productDTO = service.updateByEntity(productDTO);
            sendJsonResponse(response, productDTO);
        } catch (Exception e) {
            throw new ServletException(e);
        }


    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ProductDTO productDTO;
        try {
            String path = splitPathInfo(request);

            Long id = Long.parseLong(path);

            productDTO = service.deleteById(id);

            sendJsonResponse(response, productDTO);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void sendJsonResponse(HttpServletResponse response, ProductDTO productDTO) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print(gson.toJson(productDTO));
        writer.flush();
    }

}
