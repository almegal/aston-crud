package mapper.impl;

import dto.product.ProductCreateDto;
import dto.product.ProductDto;
import entity.Product;
import mapper.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс ProductMapperImpl реализует интерфейс Mapper и предназначен для преобразования объектов
 * между сущностью Product и DTO ProductDTO, а также для маппинга данных из ResultSet.
 */
public class ProductMapperImpl implements Mapper<ProductDto, ProductCreateDto, Product> {

    @Override
    public Product fromResponseDtoToEntity(ProductDto dto) {
        Product entity = new Product();
        entity.setName(dto.getName());
        entity.setId(dto.getId());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    @Override
    public Product fromResultSetToEntity(ResultSet resultSet) {
        try {
            Product product = new Product();
            product.setName(resultSet.getString("name"));
            product.setId(resultSet.getInt("id"));
            product.setPrice(resultSet.getInt("price"));
            product.setDescription(resultSet.getString("description"));
            return product;
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Product fromCreateDtoToEntity(ProductCreateDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        return product;
    }

    @Override
    public ProductDto fromEntityToResponseDto(Product entity) {
        ProductDto productDTO = new ProductDto();
        productDTO.setId(entity.getId());
        productDTO.setName(entity.getName());
        productDTO.setPrice(entity.getPrice());
        productDTO.setDescription(entity.getDescription());
        return productDTO;
    }
}
