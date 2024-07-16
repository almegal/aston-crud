package service;

import exception.ElementNotFoundException;
import exception.ServiceException;

/**
 * Интерфейс Service определяет основные методы для работы с сущностями.
 *
 * @param <T> тип сущности
 * @param <C> тип DTO для создания сущности
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
    T getById(Long id) throws ElementNotFoundException, ServiceException;

    /**
     * Сохраняет новую сущность.
     *
     * @param dto объект DTO для создания новой сущности
     * @return сохраненная сущность
     * @throws ServiceException если произошла ошибка в сервисе
     */
    T save(C dto) throws ServiceException;

    /**
     * Обновляет существующую сущность.
     *
     * @param newEntity новая сущность для обновления
     * @return обновленная сущность
     * @throws ElementNotFoundException если сущность не найдена
     * @throws ServiceException         если произошла ошибка в сервисе
     */
    T updateByEntity(T newEntity) throws ElementNotFoundException, ServiceException;

    /**
     * Удаляет сущность по её идентификатору.
     *
     * @param id идентификатор сущности
     * @return удаленная сущность
     * @throws ElementNotFoundException если сущность не найдена
     * @throws ServiceException         если произошла ошибка в сервисе
     */
    T deleteById(Long id) throws ElementNotFoundException, ServiceException;
}
