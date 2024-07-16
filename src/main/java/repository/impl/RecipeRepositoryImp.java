package repository.impl;

import config.ConfigUtil;
import db.UtilDB;
import dto.recipe.RecipeCreateDto;
import dto.recipe.RecipeDto;
import entity.Recipe;
import exception.RepositoryException;
import mapper.Mapper;
import mapper.impl.RecipeMapperImpl;
import repository.RecipeRepositorySave;

import java.sql.*;
import java.util.Optional;
import java.util.Set;

public class RecipeRepositoryImp implements RecipeRepositorySave<Recipe> {

    final private Mapper<RecipeDto, RecipeCreateDto, Recipe> mapper = new RecipeMapperImpl();
    final private UtilDB db;
    final private String ERROR_MESSAGE_DATA_BASE = ConfigUtil.getProperty("ERROR_MESSAGE_DATA_BASE");


    public RecipeRepositoryImp(UtilDB db) {
        this.db = db;
    }


    @Override
    public Optional<Recipe> getById(Long id) throws RepositoryException {
        String query = """
                SELECT\s
                    r.id AS recipe_id,
                    r.name AS recipe_name,
                    r.description AS recipe_description,
                    p.id AS product_id,
                    p.name AS product_name,
                    p.description AS product_description,
                    p.price AS product_price
                FROM\s
                    recipe r
                LEFT JOIN\s
                    recipe_product rp ON r.id = rp.recipe_id
                LEFT JOIN\s
                    product p ON rp.product_id = p.id
                WHERE\s
                    r.id = ?""";
        try (Connection conn = db.createConnection();
             PreparedStatement stm = conn.prepareStatement(query)
        ) {
            stm.setLong(1, id);
            ResultSet resultSet = stm.executeQuery();
            Recipe recipe = mapper.fromResultSetToEntity(resultSet);
            return Optional.ofNullable(recipe);
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        }
    }

    @Override
    public Recipe save(Recipe newRecipe, Set<Long> productsId) throws RepositoryException, SQLException {
        Connection conn = null;
        try {
            conn = db.createConnection();
            conn.setAutoCommit(false);

            // Установка параметров
            saveRecipeToDB(conn, newRecipe);
            //
            addRecipeProductsManyToMany(conn, newRecipe.getId(), productsId);

            conn.commit();
            return newRecipe;
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public Recipe updateByEntity(Recipe recipeUpdate) throws RepositoryException {
        try (Connection conn = db.createConnection();
             PreparedStatement stm = conn.prepareStatement("UPDATE recipe SET name = ?, description = ? WHERE id = ?")) {
            // Установка параметров
            stm.setString(1, recipeUpdate.getName());
            stm.setString(2, recipeUpdate.getDescription());
            stm.setLong(3, recipeUpdate.getId());
            // Выполнение запроса
            stm.executeUpdate();
            return recipeUpdate;
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        }
    }

    @Override
    public void deleteById(Long id) throws RepositoryException {
        try (Connection conn = db.createConnection();
             PreparedStatement stm = conn.prepareStatement("DELETE FROM recipe WHERE id = ?")) {
            stm.setLong(1, id);
            stm.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        }
    }


    private void saveRecipeToDB(Connection conn, Recipe recipe) throws SQLException {
        String query = "INSERT INTO recipe (name, description) VALUES (?, ?) ";
        try (
                PreparedStatement stm = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, recipe.getName());
            stm.setString(2, recipe.getDescription());
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                recipe.setId(rs.getLong(1));
            }
        }
    }

    private void addRecipeProductsManyToMany(Connection conn, Long recipeID, Set<Long> productIds) throws SQLException {
        String query = "INSERT INTO recipe_product (recipe_id, product_id) VALUES (?,?)";
        try (
                PreparedStatement stm = conn.prepareStatement(query)) {
            for (Long productId : productIds) {
                stm.setLong(1, recipeID);
                stm.setLong(2, productId);
                stm.addBatch();
            }
            stm.executeBatch();
        }
    }
}
