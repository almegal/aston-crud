package service.impl;

import config.ConfigUtil;
import dto.recipe.RecipeCreateDto;
import dto.recipe.RecipeDto;
import entity.Recipe;
import exception.ElementNotFoundException;
import exception.RepositoryException;
import exception.ServiceException;
import mapper.Mapper;
import mapper.impl.RecipeMapperImpl;
import repository.RecipeRepositorySave;
import repository.impl.RecipeRepositoryImp;
import service.Service;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Реализация интерфейса Service для работы с рецептами.
 */
public class RecipeServiceImpl implements Service<RecipeDto, RecipeCreateDto> {
    private final RecipeRepositorySave<Recipe> repository;
    private final String ERROR_MESSAGE_NOT_FOUND = ConfigUtil.getProperty("ERROR_MESSAGE_NOT_FOUND");
    private final Mapper<RecipeDto, RecipeCreateDto, Recipe> mapper = new RecipeMapperImpl();

    /**
     * Конструктор с параметром, инициализирующий репозиторий рецептов.
     *
     * @param repository репозиторий рецептов для взаимодействия с базой данных или другим источником данных
     */
    public RecipeServiceImpl(RecipeRepositoryImp repository) {
        this.repository = repository;
    }

    /**
     * Возвращает RecipeDto по заданному идентификатору рецепта.
     *
     * @param id идентификатор рецепта
     * @return DTO рецепта
     * @throws ElementNotFoundException если рецепт с заданным id не найден
     * @throws ServiceException         если произошла ошибка на уровне сервиса
     */
    @Override
    public RecipeDto getById(Long id) throws ElementNotFoundException, ServiceException {
        try {
            // Получаем Optional Рецептов
            Optional<Recipe> recipeOptional = repository.getById(id);
            // Извлекаем значение
            // Если его нет выбраосить исключение
            Recipe recipe = recipeOptional.orElseThrow(() -> {
                String msg = ERROR_MESSAGE_NOT_FOUND.formatted(id);
                return new ElementNotFoundException(msg);
            });
            // Маппим в dto
            return mapper.fromEntityToResponseDto(recipe);
        } catch (RepositoryException ex) {
            throw new ServiceException(ex.getMessage(), ex);
        }
    }

    /**
     * Сохраняет новый рецепт.
     *
     * @param dto DTO нового рецепта
     * @return сохраненный DTO рецепта
     * @throws ServiceException если произошла ошибка на уровне сервиса
     */
    @Override
    public RecipeDto save(RecipeCreateDto dto) throws ServiceException {
        try {
            // Маппим в сущность
            Recipe recipeToSave = mapper.fromCreateDtoToEntity(dto);
            // Сохраняем и получаем результат
            Recipe recipe = repository.save(recipeToSave, dto.getProducts());
            // Возращаем сохраненное значение со всеми продуктами
            return getById(recipe.getId());
        } catch (SQLException | RepositoryException ex) {
            throw new ServiceException(ex.getMessage(), ex);
        }
    }

    /**
     * Обновляет информацию о рецепте по новому DTO.
     *
     * @param newEntity новый DTO рецепта
     * @return обновленный DTO рецепта
     * @throws ElementNotFoundException если рецепт с заданным id не найден
     * @throws ServiceException         если произошла ошибка на уровне сервиса
     */
    @Override
    public RecipeDto updateByEntity(RecipeDto newEntity) throws ServiceException {
        try {
            // Проверяем, существует ли рецепт с данным id
            // Сохраним в переменную для возрата этого объекта
            RecipeDto recipeToResponse = getById(newEntity.getId());
            // Маппип в сущность
            Recipe recipeToUpdate = mapper.fromResponseDtoToEntity(newEntity);
            // Производим обновление
            repository.updateByEntity(recipeToUpdate);
            // Устанавливаем обновленные значение в нашу полученую сущность
            recipeToResponse.setName(recipeToUpdate.getName());
            recipeToResponse.setDescription(recipeToUpdate.getDescription());
            // Возращаем
            return recipeToResponse;
        } catch (RepositoryException ex) {
            throw new ServiceException(ex.getMessage(), ex);
        }
    }

    /**
     * Удаляет рецепт по заданному идентификатору.
     *
     * @param id идентификатор рецепта для удаления
     * @return удаленный DTO рецепта
     * @throws ElementNotFoundException если рецепт с заданным id не найден
     * @throws ServiceException         если произошла ошибка на уровне сервиса
     */
    @Override
    public RecipeDto deleteById(Long id) throws ServiceException {
        try {
            RecipeDto recipe = getById(id); // Получаем рецепт для возврата его DTO
            repository.deleteById(id); // Удаляем рецепт из репозитория
            return recipe;
        } catch (RepositoryException ex) {
            throw new ServiceException(ex.getMessage(), ex);
        }
    }
}
