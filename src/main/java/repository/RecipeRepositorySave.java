package repository;

import exception.RepositoryException;

import java.sql.SQLException;
import java.util.Set;

/**
 * Интерфейс RecipeRepositorySave определяет методы для сохранения рецептов в базу данных.
 *
 * @param <T> тип сущности, с которой работает репозиторий
 */
public interface RecipeRepositorySave<T> extends RepositoryWithoutSave<T> {

    /**
     * Сохраняет новый рецепт в базе данных вместе с его продуктами.
     *
     * @param recipe     объект рецепта для сохранения
     * @param productIds множество идентификаторов продуктов, связанных с рецептом
     * @return сохраненный объект рецепта
     * @throws RepositoryException если происходит ошибка при работе с базой данных
     * @throws SQLException        если происходит ошибка при выполнении SQL запроса
     */
    T save(T recipe, Set<Long> productIds) throws RepositoryException, SQLException;
}
