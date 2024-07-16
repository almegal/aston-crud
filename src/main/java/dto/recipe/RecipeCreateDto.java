package dto.recipe;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class RecipeCreateDto {
    private String name;
    private String description;
    private Set<Long> products;
}
