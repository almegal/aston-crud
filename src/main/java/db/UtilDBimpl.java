package db;

import config.ConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Реализация интерфейса UtilDB для управления соединением с базой данных.
 * Используется шаблон Singleton для обеспечения единственного экземпляра класса.
 */
public class UtilDBimpl implements UtilDB {
    private static volatile UtilDBimpl _instance;

    // Загрузка драйвера PostgreSQL в статическом блоке
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private Connection connection;

    // Приватный конструктор для предотвращения создания экземпляра вне класса
    private UtilDBimpl() {
        try {
            this.connection = createConnection();
        } catch (SQLException e) {
            // Логирование ошибки
            System.out.println("Ошибка при создании соединения с базой данных: " + e.getMessage());
        }
    }

    /**
     * Получение единственного экземпляра UtilDBimpl (Singleton).
     * Синхронизированный метод для обеспечения потокобезопасности.
     *
     * @return единственный экземпляр UtilDBimpl
     */
    public static synchronized UtilDB getInstance() {
        if (_instance != null) {
            return _instance;
        }

        _instance = new UtilDBimpl();
        return _instance;
    }

    /**
     * Закрытие соединения с базой данных.
     */
    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }

    /**
     * Создание нового соединения с базой данных.
     * Если соединение уже существует и открыто, возвращается существующее соединение.
     *
     * @return текущее соединение с базой данных
     * @throws SQLException если произошла ошибка при создании соединения
     */
    @Override
    public Connection createConnection() throws SQLException {
        // Проверяем, существует ли соединение и не закрыто ли оно
        boolean canOpenConnection = this.connection == null || this.connection.isClosed();
        if (canOpenConnection) {
            String url = ConfigUtil.getProperty("data_base_url");
            String user = ConfigUtil.getProperty("user_db");
            String password = ConfigUtil.getProperty("password_db");
            this.connection = DriverManager.getConnection(url, user, password);
        }
        return this.connection;
    }

}
