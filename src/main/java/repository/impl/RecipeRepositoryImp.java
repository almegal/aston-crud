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

/**
 * Класс RecipeRepositoryImp реализует интерфейс RecipeRepositorySave для сущности Recipe.
 * Обеспечивает выполнение CRUD операций с базой данных.
 */
public class RecipeRepositoryImp implements RecipeRepositorySave<Recipe> {

    final private Mapper<RecipeDto, RecipeCreateDto, Recipe> mapper = new RecipeMapperImpl();
    final private UtilDB db;
    final private String ERROR_MESSAGE_DATA_BASE = ConfigUtil.getProperty("ERROR_MESSAGE_DATA_BASE");

    /**
     * Конструктор для инициализации объекта репозитория с заданной утилитой базы данных.
     *
     * @param db объект UtilDB для управления соединениями с базой данных
     */
    public RecipeRepositoryImp(UtilDB db) {
        this.db = db;
    }

    /**
     * Получает рецепт по его идентификатору из базы данных.
     *
     * @param id идентификатор рецепта
     * @return Optional с объектом Recipe, если он найден, иначе пустой Optional
     * @throws RepositoryException если происходит ошибка при работе с базой данных
     */
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

        // Устанавливаем соединение и создаем подготовленный запрос
        try (Connection conn = db.createConnection();
             PreparedStatement stm = conn.prepareStatement(query)) {
            // Устанавливаем значение идентификатора в запрос
            stm.setLong(1, id);
            // Выполняем запрос и получаем результат
            ResultSet resultSet = stm.executeQuery();
            // Маппим результат в объект Recipe
            Recipe recipe = mapper.fromResultSetToEntity(resultSet);
            return Optional.ofNullable(recipe);
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        }
    }

    /**
     * Сохраняет новый рецепт в базе данных вместе с его продуктами.
     *
     * @param newRecipe объект Recipe для сохранения
     * @param productsId множество идентификаторов продуктов, связанных с рецептом
     * @return сохраненный объект Recipe
     * @throws RepositoryException если происходит ошибка при работе с базой данных
     * @throws SQLException если происходит ошибка при выполнении SQL запроса
     */
    @Override
    public Recipe save(Recipe newRecipe, Set<Long> productsId) throws RepositoryException, SQLException {
        Connection conn = null;
        // Устанавливаем соединение и выключаем авто-коммит для транзакции
        try {
            conn = db.createConnection();
            conn.setAutoCommit(false);

            // Сохраняем рецепт в базу данных
            saveRecipeToDB(conn, newRecipe);

            // Добавляем связь между рецептом и продуктами в таблицу many-to-many
            addRecipeProductsManyToMany(conn, newRecipe.getId(), productsId);

            // Коммитим транзакцию
            conn.commit();
            return newRecipe;
        } catch (SQLException ex) {
            // В случае ошибки откатываем транзакцию
            if (conn != null) {
                conn.rollback();
            }
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        } finally {
            // Включаем авто-коммит и закрываем соединение
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Обновляет существующий рецепт в базе данных.
     *
     * @param recipeUpdate объект Recipe с обновленными данными
     * @return обновленный объект Recipe
     * @throws RepositoryException если происходит ошибка при работе с базой данных
     */
    @Override
    public Recipe updateByEntity(Recipe recipeUpdate) throws RepositoryException {
        // Устанавливаем соединение и создаем подготовленный запрос
        try (Connection conn = db.createConnection();
             PreparedStatement stm = conn.prepareStatement("UPDATE recipe SET name = ?, description = ? WHERE id = ?")) {
            // Устанавливаем параметры запроса
            stm.setString(1, recipeUpdate.getName());
            stm.setString(2, recipeUpdate.getDescription());
            stm.setLong(3, recipeUpdate.getId());
            // Выполняем запрос на обновление
            stm.executeUpdate();
            return recipeUpdate;
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        }
    }

    /**
     * Удаляет рецепт по его идентификатору из базы данных.
     *
     * @param id идентификатор рецепта
     * @throws RepositoryException если происходит ошибка при работе с базой данных
     */
    @Override
    public void deleteById(Long id) throws RepositoryException {
        // Устанавливаем соединение и создаем подготовленный запрос
        try (Connection conn = db.createConnection();
             PreparedStatement stm = conn.prepareStatement("DELETE FROM recipe WHERE id = ?")) {
            // Устанавливаем значение идентификатора в запрос
            stm.setLong(1, id);
            // Выполняем запрос на удаление
            stm.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        }
    }

    /**
     * Сохраняет рецепт в базу данных.
     *
     * @param conn соединение с базой данных
     * @param recipe объект Recipe для сохранения
     * @throws SQLException если происходит ошибка при выполнении SQL запроса
     */
    private void saveRecipeToDB(Connection conn, Recipe recipe) throws SQLException {
        String query = "INSERT INTO recipe (name, description) VALUES (?, ?)";
        // Создаем подготовленный запрос
        try (PreparedStatement stm = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Устанавливаем параметры запроса
            stm.setString(1, recipe.getName());
            stm.setString(2, recipe.getDescription());
            // Выполняем запрос на вставку
            stm.executeUpdate();
            // Получаем сгенерированный ключ (идентификатор)
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                recipe.setId(rs.getLong(1));
            }
        }
    }

    /**
     * Добавляет связь между рецептом и продуктами в таблицу many-to-many.
     *
     * @param conn соединение с базой данных
     * @param recipeID идентификатор рецепта
     * @param productIds множество идентификаторов продуктов
     * @throws SQLException если происходит ошибка при выполнении SQL запроса
     */
    private void addRecipeProductsManyToMany(Connection conn, Long recipeID, Set<Long> productIds) throws SQLException {
        String query = "INSERT INTO recipe_product (recipe_id, product_id) VALUES (?,?)";
        // Создаем подготовленный запрос
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            // Устанавливаем параметры запроса для каждого продукта и добавляем в батч
            for (Long productId : productIds) {
                stm.setLong(1, recipeID);
                stm.setLong(2, productId);
                stm.addBatch();
            }
            // Выполняем батч запросов
            stm.executeBatch();
        }
    }
}
