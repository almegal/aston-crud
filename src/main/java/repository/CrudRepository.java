package repository;

import exception.RepositoryException;

import java.util.Optional;

/**
 * Интерфейс CrudRepository определяет основные операции CRUD для сущностей типа T.
 *
 * @param <T> тип сущности
 */
public interface CrudRepository<T> {

    /**
     * Возвращает сущность по её идентификатору.
     *
     * @param id идентификатор сущности
     * @return Optional, содержащий сущность, если она существует, или пустой Optional, если нет
     * @throws RepositoryException если произошла ошибка при доступе к репозиторию
     */
    Optional<T> getById(Long id) throws RepositoryException;

    /**
     * Сохраняет новую сущность в репозитории.
     *
     * @param e сущность для сохранения
     * @return сохраненная сущность
     * @throws RepositoryException если произошла ошибка при сохранении сущности
     */
    T save(T e) throws RepositoryException;

    /**
     * Обновляет существующую сущность в репозитории.
     *
     * @param e сущность для обновления
     * @return обновленная сущность
     * @throws RepositoryException если произошла ошибка при обновлении сущности
     */
    T updateByEntity(T e) throws RepositoryException;

    /**
     * Удаляет сущность по её идентификатору.
     *
     * @param id идентификатор сущности
     * @throws RepositoryException если произошла ошибка при удалении сущности
     */
    void deleteById(Long id) throws RepositoryException;
}
