package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter("/*")
public class FilterAPI implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String apiReq = new String();
        try {
            apiReq = getPathRequest(servletRequest);
        } catch (Exception e) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (apiReq.equalsIgnoreCase("product")) {
            RequestDispatcher requestDispatcher = httpServletRequest.getRequestDispatcher("/product/*");
            requestDispatcher.forward(servletRequest, servletResponse);
        } else if (apiReq.equalsIgnoreCase("recipe")) {
            RequestDispatcher requestDispatcher = httpServletRequest.getRequestDispatcher("/recipe/*");
            requestDispatcher.forward(servletRequest, servletResponse);
        }

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private String getPathRequest(ServletRequest servletRequest) {
        HttpServletRequest httpReq = (HttpServletRequest) servletRequest;
        String pathInfo = httpReq.getPathInfo();
        return pathInfo.split("/")[1];
    }
}
