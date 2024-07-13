package db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Интерфейс UtilDB определяет методы для работы с соединениями к базе данных.
 */
public interface UtilDB {

    /**
     * Создает и возвращает новое соединение с базой данных.
     *
     * @return объект Connection, представляющий соединение с базой данных
     * @throws SQLException если произошла ошибка при создании соединения
     */
    Connection createConnection() throws SQLException;

    /**
     * Закрывает текущее соединение с базой данных.
     */
    void closeConnection();
}
