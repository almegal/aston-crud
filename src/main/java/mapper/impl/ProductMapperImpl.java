package mapper.impl;

import dto.ProductDTO;
import entity.Product;
import mapper.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс ProductMapperImpl реализует интерфейс Mapper и предназначен для преобразования объектов
 * между сущностью Product и DTO ProductDTO, а также для маппинга данных из ResultSet.
 */
public class ProductMapperImpl implements Mapper<ProductDTO, Product> {

    /**
     * Преобразует сущность Product в DTO ProductDTO.
     *
     * @param entity сущность Product
     * @return DTO ProductDTO
     */
    @Override
    public ProductDTO toDTO(Product entity) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(entity.getId());
        productDTO.setName(entity.getName());
        productDTO.setPrice(entity.getPrice());
        productDTO.setDescription(entity.getDescription());
        return productDTO;
    }

    /**
     * Преобразует данные из ResultSet в DTO ProductDTO.
     *
     * @param resultSet объект ResultSet с данными
     * @return DTO ProductDTO или null, если данных нет
     */
    @Override
    public ProductDTO toDto(ResultSet resultSet) {
        ProductDTO productDTO = new ProductDTO();
        try {
            if (resultSet.next()) {
                productDTO.setName(resultSet.getString("name"));
                productDTO.setId(resultSet.getInt("id"));
                productDTO.setPrice(resultSet.getInt("price"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Для логирования и отладки
        }
        return productDTO;
    }

    /**
     * Преобразует DTO ProductDTO в сущность Product.
     *
     * @param dto DTO ProductDTO
     * @return сущность Product
     */
    @Override
    public Product toEntity(ProductDTO dto) {
        Product entity = new Product();
        entity.setName(dto.getName());
        entity.setId(dto.getId());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    /**
     * Преобразует данные из ResultSet в сущность Product.
     *
     * @param resultSet объект ResultSet с данными
     * @return сущность Product или null, если данных нет
     */
    @Override
    public Product toEntity(ResultSet resultSet) {
        Product entity = new Product();
        try {
            if (resultSet.next()) {
                entity.setName(resultSet.getString("name"));
                entity.setId(resultSet.getInt("id"));
                entity.setPrice(resultSet.getInt("price"));
                entity.setDescription(resultSet.getString("description"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Для логирования и отладки
        }
        return entity;
    }
}
