package db;

import config.ConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UtilDBimpl implements UtilDB {
    private static UtilDBimpl _instance;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private Connection connection;

    private UtilDBimpl() {
        try {
            this.connection = createConnection();
        } catch (SQLException e) {
            //for logging
            System.out.println("R)");
        }
    }

    // не синхронизорован по факту, переделать
    public static synchronized UtilDB getInstance() {
        if (_instance != null) {
            return _instance;
        }

        _instance = new UtilDBimpl();
        return _instance;
    }


    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

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
