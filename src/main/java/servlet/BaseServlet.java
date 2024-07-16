package servlet;

import exception.HttpMediaTypeException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.Service;

import java.io.IOException;

import static servlet.util.ApiUtils.*;

public abstract class BaseServlet<T, C> extends HttpServlet {
    protected Service<T, C> service;

    protected abstract Class<T> getTypeT();

    protected abstract Class<C> getTypeC();

    /**
     *
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**
     *
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            String path = splitPathInfo(request);

            Long id = Long.parseLong(path);

            T dto = service.getById(id);

            sendJsonResponse(response, dto);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     *
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (isCorrectContentTypeForPost(request)) {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }
        try {
            C dtoCreate = convertJsonToDTO(request, getTypeC());
            T dto = service.save(dtoCreate);
            sendJsonResponse(response, dto);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     *
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (isCorrectContentTypeForPost(request)) {
            String errorMessage = "content type: %S not support for PUT method".formatted(request.getContentType());
            HttpMediaTypeException e = new HttpMediaTypeException(errorMessage);
            throw new ServletException(e);
        }
        try {
            T dto = convertJsonToDTO(request, getTypeT());
            T updateDto = service.updateByEntity(dto);
            sendJsonResponse(response, updateDto);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     *
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            String path = splitPathInfo(request);

            Long id = Long.parseLong(path);

            T dto = service.deleteById(id);

            sendJsonResponse(response, dto);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected void setService(Service<T, C> service) {
        this.service = service;
    }
}
