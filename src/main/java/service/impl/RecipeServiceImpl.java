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

public class RecipeServiceImpl implements Service<RecipeDto, RecipeCreateDto> {
    private final RecipeRepositorySave<Recipe> repository;
    private final String ERROR_MESSAGE_NOT_FOUND = ConfigUtil.getProperty("ERROR_MESSAGE_NOT_FOUND");
    private final Mapper<RecipeDto, RecipeCreateDto, Recipe> mapper = new RecipeMapperImpl();

    public RecipeServiceImpl(RecipeRepositoryImp repository) {
        this.repository = repository;
    }

    @Override
    public RecipeDto getById(Long id) {
        try {
            Optional<Recipe> recipeOptional = repository.getById(id);
            Recipe recipe = recipeOptional.orElseThrow(() -> {
                String msg = ERROR_MESSAGE_NOT_FOUND.formatted(id);
                return new ElementNotFoundException(msg);
            });
            return mapper.fromEntityToResponseDto(recipe);
        } catch (RepositoryException ex) {
            throw new ServiceException(ex.getMessage(), ex);
        }
    }

    @Override
    public RecipeDto save(RecipeCreateDto dto) {
        try {
            Recipe recipeToSave = mapper.fromCreateDtoToEntity(dto);
            Recipe recipe = repository.save(recipeToSave, dto.getProducts());
            return getById(recipe.getId());
        } catch (SQLException | RepositoryException ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    @Override
    public RecipeDto updateByEntity(RecipeDto newEntity) {
        try {
            getById(newEntity.getId());
            Recipe recipeToUpdate = mapper.fromResponseDtoToEntity(newEntity);
            repository.updateByEntity(recipeToUpdate);
            return getById(newEntity.getId());
        } catch (RepositoryException ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    @Override
    public RecipeDto deleteById(Long id) {
        try {
            RecipeDto recipe = getById(id);
            repository.deleteById(id);
            return recipe;
        } catch (RepositoryException ex) {
            throw new ServiceException(ex.getMessage());
        }
    }
}
