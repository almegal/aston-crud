package entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Product {

    private long id;
    private String name;
    private String description;
    private Integer price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && name.equalsIgnoreCase(product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "{"
                + "\"id\": " + id
                + ", \"name\": \"" + name + "\""
                + ", \"description\": \"" + description + "\""
                + ", \"price\": " + price
                + "}";
    }
}
