package mapper;

import java.sql.ResultSet;

public interface Mapper<T, C, E> {
    E fromResponseDtoToEntity(T dto);

    E fromResultSetToEntity(ResultSet resultSet);

    E fromCreateDtoToEntity(C dto);

    T fromEntityToResponseDto(E entity);
}
