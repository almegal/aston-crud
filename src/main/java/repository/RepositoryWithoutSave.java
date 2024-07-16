package repository;

import exception.RepositoryException;

import java.util.Optional;

public interface RepositoryWithoutSave<T> {
    Optional<T> getById(Long id) throws RepositoryException;

    T updateByEntity(T updateProduct) throws RepositoryException;

    void deleteById(Long id) throws RepositoryException;
}
