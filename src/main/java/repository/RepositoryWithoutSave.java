package repository;

import exception.RepositoryException;

import java.util.Optional;

/**
 * Интерфейс RepositoryWithoutSave определяет основные методы CRUD операций без метода сохранения.
 *
 * @param <T> тип сущности, с которой работает репозиторий
 */
public interface RepositoryWithoutSave<T> {

    /**
     * Получает объект по его идентификатору из базы данных.
     *
     * @param id идентификатор объекта
     * @return Optional с объектом, если он найден, иначе пустой Optional
     * @throws RepositoryException если происходит ошибка при работе с базой данных
     */
    Optional<T> getById(Long id) throws RepositoryException;

    /**
     * Обновляет существующий объект в базе данных.
     *
     * @param updateEntity объект с обновленными данными
     * @return обновленный объект
     * @throws RepositoryException если происходит ошибка при работе с базой данных
     */
    T updateByEntity(T updateEntity) throws RepositoryException;

    /**
     * Удаляет объект по его идентификатору из базы данных.
     *
     * @param id идентификатор объекта
     * @throws RepositoryException если происходит ошибка при работе с базой данных
     */
    void deleteById(Long id) throws RepositoryException;
}
