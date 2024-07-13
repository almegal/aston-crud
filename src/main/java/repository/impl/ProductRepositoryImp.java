package repository.impl;

import config.ConfigUtil;
import db.UtilDB;
import entity.Product;
import exception.RepositoryException;
import mapper.impl.ProductMapperImpl;
import repository.CrudRepository;

import java.sql.*;
import java.util.Optional;

public class ProductRepositoryImp implements CrudRepository<Product> {

    final private ProductMapperImpl mapper = new ProductMapperImpl();
    final private UtilDB db;
    final private String ERROR_MESSAGE_DATA_BASE = ConfigUtil.getProperty("ERROR_MESSAGE_DATA_BASE");

    public ProductRepositoryImp(UtilDB db) {
        this.db = db;
    }

    @Override
    public Optional<Product> getById(Long id) throws RepositoryException {

        try (Connection conn = db.createConnection();
             Statement stm = conn.createStatement();
             ResultSet resultSet = stm.executeQuery("SELECT * from product where id=%d".formatted(id))) {
            Product product = mapper.toEntity(resultSet);
            return Optional.ofNullable(product);
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        }
    }

    @Override
    public Product save(Product newProduct) throws RepositoryException {
        try (Connection conn = db.createConnection();
             PreparedStatement stm = conn.prepareStatement("INSERT INTO product (name, price, description) VALUES (?, ?, ?)");
        ) {
            // Установка параметров
            stm.setString(1, newProduct.getName());
            stm.setDouble(2, newProduct.getPrice());
            stm.setString(3, newProduct.getDescription());

            // Выполнение запроса

            stm.executeUpdate();

        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        }
        return newProduct;
    }

    @Override
    public Product updateByEntity(Product updateProduct) throws RepositoryException {
        try (
                Connection conn = db.createConnection();
                PreparedStatement stm = conn.prepareStatement("UPDATE product SET name = ?, price = ?, description = ? WHERE id = ?");
        ) {
            stm.setString(1, updateProduct.getName());
            stm.setDouble(2, updateProduct.getPrice());
            stm.setString(3, updateProduct.getDescription());
            stm.setLong(4, updateProduct.getId());
            stm.executeUpdate();
            return updateProduct;
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        }
    }

    @Override
    public void deleteById(Long id) throws RepositoryException {
        try (Connection conn = db.createConnection()) {
            //
            PreparedStatement stm = conn.prepareStatement("DELETE FROM product WHERE id = ?");
            stm.setLong(1, id);
            stm.executeUpdate();
        } catch (SQLException ex) {
            throw new RepositoryException(ERROR_MESSAGE_DATA_BASE, ex);
        }
    }
}
