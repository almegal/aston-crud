package config;

import dto.product.ProductCreateDto;
import dto.product.ProductDto;
import entity.Product;
import mapper.impl.ProductMapperImpl;

public class MockProps {
    final static public Product MOCK_PRODUCT = new Product();
    final static public ProductCreateDto PRODUCT_DTO_CREATE = new ProductCreateDto();


    static {
        MOCK_PRODUCT.setId(1L);
        MOCK_PRODUCT.setName("Огурец");
        MOCK_PRODUCT.setDescription("Жадина говядина соленый огурец");
        MOCK_PRODUCT.setPrice(500);

        PRODUCT_DTO_CREATE.setName(MOCK_PRODUCT.getName());
        PRODUCT_DTO_CREATE.setDescription(MOCK_PRODUCT.getDescription());
        PRODUCT_DTO_CREATE.setPrice(MOCK_PRODUCT.getPrice());
    }
    final static public String PRODUCT_AS_STRING = MOCK_PRODUCT.toString();
    final static public ProductDto PRODUCT_DTO_RESPONSE = new ProductMapperImpl().fromEntityToResponseDto(MOCK_PRODUCT);
    final static public String ERROR_MESSAGE_DATA_BASE = ConfigUtil.getProperty("ERROR_MESSAGE_DATA_BASE");
    final static public String ERROR_MESSAGE_NOT_FOUND = ConfigUtil.getProperty("ERROR_MESSAGE_NOT_FOUND");

}
