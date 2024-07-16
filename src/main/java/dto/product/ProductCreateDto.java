package dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ProductCreateDto {
    private String name;
    private Integer price;
    private String description;
}
