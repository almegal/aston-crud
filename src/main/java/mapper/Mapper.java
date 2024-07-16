package mapper;

import java.sql.ResultSet;

/**
 * Интерфейс для маппинга между различными представлениями данных.
 *
 * @param <T> Тип объекта Response DTO
 * @param <C> Тип объекта Create DTO
 * @param <E> Тип объекта Entity
 */
public interface Mapper<T, C, E> {

    /**
     * Преобразует объект Response DTO в объект Entity.
     *
     * @param dto объект Response DTO
     * @return объект Entity
     */
    E fromResponseDtoToEntity(T dto);

    /**
     * Преобразует объект ResultSet в объект Entity.
     *
     * @param resultSet объект ResultSet, полученный из базы данных
     * @return объект Entity
     */
    E fromResultSetToEntity(ResultSet resultSet);

    /**
     * Преобразует объект Create DTO в объект Entity.
     *
     * @param dto объект Create DTO
     * @return объект Entity
     */
    E fromCreateDtoToEntity(C dto);

    /**
     * Преобразует объект Entity в объект Response DTO.
     *
     * @param entity объект Entity
     * @return объект Response DTO
     */
    T fromEntityToResponseDto(E entity);
}
