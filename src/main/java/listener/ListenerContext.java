package listener;

import db.UtilDB;
import db.UtilDBimpl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import repository.impl.ProductRepositoryImp;
import service.impl.ProductServiceImpl;

@WebListener
public class ListenerContext implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        UtilDB db = UtilDBimpl.getInstance();
        ProductRepositoryImp repositoryImp = new ProductRepositoryImp(db);
        ProductServiceImpl service = new ProductServiceImpl(repositoryImp);
        ServletContext ctx = sce.getServletContext();
        ctx.setAttribute("productService", service);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
