package config;

import dto.ProductDTO;
import entity.Product;
import mapper.impl.ProductMapperImpl;

public class MockProps {
    final static public Product MOCK_PRODUCT = new Product();

    static {
        MOCK_PRODUCT.setId(1L);
        MOCK_PRODUCT.setName("Огурец");
        MOCK_PRODUCT.setDescription("Жадина говядина соленый огурец");
        MOCK_PRODUCT.setPrice(500);
    }

    final static public String PRODUCT_AS_STRING = MOCK_PRODUCT.toString();
    final static public ProductDTO PRODUCT_DTO_TO_SAVE = new ProductMapperImpl().toDTO(MOCK_PRODUCT);
    final static public String ERROR_MESSAGE_DATA_BASE = ConfigUtil.getProperty("ERROR_MESSAGE_DATA_BASE");
    final static public String ERROR_MESSAGE_NOT_FOUND = ConfigUtil.getProperty("ERROR_MESSAGE_NOT_FOUND");

}
