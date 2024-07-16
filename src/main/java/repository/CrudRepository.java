package repository;

import exception.RepositoryException;

public interface CrudRepository<T> extends RepositoryWithoutSave<T> {
    /**
     * Сохраняет новую сущность в репозитории.
     *
     * @param e сущность для сохранения
     * @return сохраненная сущность
     * @throws RepositoryException если произошла ошибка при сохранении сущности
     */
    T save(T e) throws RepositoryException;

}
