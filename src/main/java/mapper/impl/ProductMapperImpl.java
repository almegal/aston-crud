package mapper.impl;

import dto.ProductDTO;
import entity.Product;
import mapper.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductMapperImpl implements Mapper<ProductDTO, Product> {
    @Override
    public ProductDTO toDTO(Product entity) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(entity.getId());
        productDTO.setName(entity.getName());
        productDTO.setPrice(entity.getPrice());
        productDTO.setDescription(entity.getDescription());
        return productDTO;
    }

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
            e.printStackTrace();
        }
        return productDTO;
    }

    @Override
    public Product toEntity(ProductDTO dto) {
        Product entity = new Product();
        entity.setName(dto.getName());
        entity.setId(dto.getId());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
        return entity;
    }

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
            e.printStackTrace();
        }
        return entity;
    }
}
