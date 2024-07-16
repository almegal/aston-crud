package service;

import exception.ElementNotFoundException;
import exception.ServiceException;

/**
 * Интерфейс ProductService определяет основные методы для работы с продуктами.
 *
 * @param <T> тип сущности
 */
public interface Service<T, C> {

    /**
     * Возвращает сущность по её идентификатору.
     *
     * @param id идентификатор сущности
     * @return сущность, если она существует
     * @throws ElementNotFoundException если сущность не найдена
     * @throws ServiceException         если произошла ошибка в сервисе
     */
    T getById(Long id);

    /**
     * Сохраняет новую сущность.
     *
     * @param entity сущность для сохранения
     * @return сохраненная сущность
     * @throws ServiceException если произошла ошибка в сервисе
     */
    T save(C dto);

    /**
     * Обновляет существующую сущность.
     *
     * @param newEntity новая сущность для обновления
     * @return обновленная сущность
     * @throws ElementNotFoundException если сущность не найдена
     * @throws ServiceException         если произошла ошибка в сервисе
     */
    T updateByEntity(T newEntity);

    /**
     * Удаляет сущность по её идентификатору.
     *
     * @param id идентификатор сущности
     * @return удаленная сущность
     * @throws ElementNotFoundException если сущность не найдена
     * @throws ServiceException         если произошла ошибка в сервисе
     */
    T deleteById(Long id);
}
