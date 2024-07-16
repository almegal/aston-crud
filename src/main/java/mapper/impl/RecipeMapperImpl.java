package mapper.impl;

import dto.recipe.RecipeCreateDto;
import dto.recipe.RecipeDto;
import entity.Product;
import entity.Recipe;
import mapper.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class RecipeMapperImpl implements Mapper<RecipeDto, RecipeCreateDto, Recipe> {

    @Override
    public Recipe fromResponseDtoToEntity(RecipeDto dto) {
        Recipe entity = new Recipe();
        entity.setName(dto.getName());
        entity.setId(dto.getId());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    @Override
    public Recipe fromResultSetToEntity(ResultSet resultSet) {
        try {
            Recipe recipe = null;
            Set<Product> products = new HashSet<>();
            while (resultSet.next()) {
                if (recipe == null) {
                    recipe = new Recipe();
                    recipe.setId(resultSet.getLong("recipe_id"));
                    recipe.setName(resultSet.getString("recipe_name"));
                    recipe.setDescription(resultSet.getString("recipe_description"));
                }
                Product product = new Product();
                product.setId(resultSet.getLong("product_id"));
                product.setName(resultSet.getString("product_name"));
                product.setDescription(resultSet.getString("product_description"));
                product.setPrice(resultSet.getInt("product_price"));
                products.add(product);
            }
            if (recipe != null) {
                recipe.setProducts(products);
                return recipe;
            }
            return recipe;

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage()); // Для логирования и отладки
        }
    }

    @Override
    public Recipe fromCreateDtoToEntity(RecipeCreateDto dto) {
        Recipe recipe = new Recipe();
        recipe.setName(dto.getName());
        recipe.setDescription(dto.getDescription());
        return recipe;
    }

    @Override
    public RecipeDto fromEntityToResponseDto(Recipe entity) {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setId(entity.getId());
        recipeDto.setName(entity.getName());
        recipeDto.setDescription(entity.getDescription());
        recipeDto.setProducts(entity.getProducts());
        return recipeDto;
    }
}
