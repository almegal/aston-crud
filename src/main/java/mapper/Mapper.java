package mapper;

import java.sql.ResultSet;

public interface Mapper<T, D> {
    T toDTO(D dto);

    T toDto(ResultSet resultSet);

    D toEntity(T entity);

    D toEntity(ResultSet resultSet);
}
