package dto.recipe;

import entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class RecipeDto {
    private long id;
    private String name;
    private String description;
    private Set<Product> products;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeDto recipeDto = (RecipeDto) o;
        return id == recipeDto.id && Objects.equals(name, recipeDto.name) && Objects.equals(description, recipeDto.description) && Objects.equals(products, recipeDto.products);
    }
}
