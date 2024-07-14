package listener;

import db.UtilDB;
import db.UtilDBimpl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import repository.impl.ProductRepositoryImp;
import service.impl.ProductServiceImpl;

/**
 * Класс ListenerContext реализует интерфейс ServletContextListener и используется для инициализации
 * объектов базы данных и сервисов при запуске приложения.
 */
@WebListener
public class ListenerContext implements ServletContextListener {

    /**
     * Метод вызывается при инициализации контекста сервлета.
     * Создает и настраивает необходимые компоненты и сохраняет их в контексте сервлета.
     *
     * @param sce событие инициализации контекста сервлета
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Создание экземпляра базы данных
        UtilDB db = UtilDBimpl.getInstance();
        // Создание экземпляра репозитория продукта с использованием базы данных
        ProductRepositoryImp repositoryImp = new ProductRepositoryImp(db);
        // Создание экземпляра сервиса продукта с использованием репозитория
        ProductServiceImpl service = new ProductServiceImpl(repositoryImp);
        // Получение контекста сервлета
        ServletContext ctx = sce.getServletContext();
        // Сохранение сервиса продукта в контексте сервлета для использования в других компонентах
        ctx.setAttribute("productService", service);
    }

    /**
     * Метод вызывается при уничтожении контекста сервлета.
     * В данном случае не выполняет никаких действий, но может быть расширен для выполнения необходимых операций очистки.
     *
     * @param sce событие уничтожения контекста сервлета
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
