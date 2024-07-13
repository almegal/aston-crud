package dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {
    private long id;
    private String name;
    private Integer price;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDTO that = (ProductDTO) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(price, that.price) && Objects.equals(description, that.description);
    }

}
