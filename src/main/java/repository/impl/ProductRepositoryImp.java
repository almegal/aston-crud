package repository.impl;

import config.ConfigUtil;
import db.UtilDB;
import dto.product.ProductCreateDto;
import dto.product.ProductDto;
import entity.Product;
import exception.RepositoryException;
import mapper.Mapper;
import mapper.impl.ProductMapperImpl;
import repository.CrudRepository;

import java.sql.*;
import java.util.Optional;

/**
 * Класс ProductRepositoryImp реализует интерфейс CrudRepository для сущности Product.
 * Обеспечивает выполнение CRUD операций с базой данных.
 */
public class ProductRepositoryImp implements CrudRepository<Product> {

    final private Mapper<ProductDto, ProductCreateDto, Product> mapper = new ProductMapperImpl();
    final private UtilDB db;
    final private String ERROR_MESSAGE_DATA_BASE = ConfigUtil.getProperty("ERROR_MESSAGE_DATA_BASE");

    /**
     * Конструктор для инициализации объекта репозитория с заданной утилитой базы данных.
     *
     * @param db объект UtilDB для управления соединениями с базой данных
     */
    public ProductRepositoryImp(UtilDB db) {
        this.db = db;
    }

    /**
     * Получает продукт по его идентификатору из базы данных.
     *
     * @param id идентификатор продукта
     * @return Optional с объектом Product, если он найден, иначе пустой Optional
     * @throws RepositoryException если происходит ошибка при работе с базой данных
     */
    @Override
    public Optional<Product> getById(Long id) throws RepositoryException {
        // Устанавливаем соединение и создаем подготовленный запрос
        try (Connection conn = db.createConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT * from product where id=?")) {
            // Устанавливаем значение идентификатора в запрос
            stm.setLong(1, id);
            // Выполняем запрос и получаем результат
            ResultSet resultSet = stm.executeQuery();
            // Если результат найден, маппим его в объект Product
            if (resultSet.next()) {
                Product product = mapper.fromResultSetToEntity(resultSet);
                return Optional.ofNullable(product);
            }
            // Если результат не найден, возвращаем пустой Optional
            return Optional.empty();
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        }
    }

    /**
     * Сохраняет новый продукт в базе данных.
     *
     * @param newProduct объект Product для сохранения
     * @return сохраненный объект Product
     * @throws RepositoryException если происходит ошибка при работе с базой данных
     */
    @Override
    public Product save(Product newProduct) throws RepositoryException {
        // Устанавливаем соединение и создаем подготовленный запрос
        try (Connection conn = db.createConnection();
             PreparedStatement stm = conn.prepareStatement(
                     "INSERT INTO product (name, price, description) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            // Устанавливаем параметры запроса
            stm.setString(1, newProduct.getName());
            stm.setInt(2, newProduct.getPrice());
            stm.setString(3, newProduct.getDescription());
            // Выполняем запрос на вставку
            stm.executeUpdate();
            // Получаем сгенерированный ключ (идентификатор)
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                newProduct.setId(rs.getLong(1));
            }
            return newProduct;
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        }
    }

    /**
     * Обновляет существующий продукт в базе данных.
     *
     * @param updateProduct объект Product с обновленными данными
     * @return обновленный объект Product
     * @throws RepositoryException если происходит ошибка при работе с базой данных
     */
    @Override
    public Product updateByEntity(Product updateProduct) throws RepositoryException {
        // Устанавливаем соединение и создаем подготовленный запрос
        try (Connection conn = db.createConnection();
             PreparedStatement stm = conn.prepareStatement("UPDATE product SET name = ?, price = ?, description = ? WHERE id = ?")) {
            // Устанавливаем параметры запроса
            stm.setString(1, updateProduct.getName());
            stm.setDouble(2, updateProduct.getPrice());
            stm.setString(3, updateProduct.getDescription());
            stm.setLong(4, updateProduct.getId());
            // Выполняем запрос на обновление
            stm.executeUpdate();
            return updateProduct;
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        }
    }

    /**
     * Удаляет продукт по его идентификатору из базы данных.
     *
     * @param id идентификатор продукта
     * @throws RepositoryException если происходит ошибка при работе с базой данных
     */
    @Override
    public void deleteById(Long id) throws RepositoryException {
        // Устанавливаем соединение и создаем подготовленный запрос
        try (Connection conn = db.createConnection();
             PreparedStatement stm = conn.prepareStatement("DELETE FROM product WHERE id = ?")) {
            // Устанавливаем значение идентификатора в запрос
            stm.setLong(1, id);
            // Выполняем запрос на удаление
            stm.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        }
    }
}
