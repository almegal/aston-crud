package repository;

import exception.RepositoryException;

import java.sql.SQLException;
import java.util.Set;

public interface RecipeRepositorySave<T> extends RepositoryWithoutSave<T> {
    T save(T recipe, Set<Long> productIds) throws RepositoryException, SQLException;
}
