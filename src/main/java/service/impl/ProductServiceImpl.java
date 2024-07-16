package service.impl;

import config.ConfigUtil;
import dto.product.ProductCreateDto;
import dto.product.ProductDto;
import entity.Product;
import exception.ElementNotFoundException;
import exception.RepositoryException;
import exception.ServiceException;
import mapper.Mapper;
import mapper.impl.ProductMapperImpl;
import repository.CrudRepository;
import repository.impl.ProductRepositoryImp;
import service.Service;

import java.util.Optional;

/**
 * Реализация интерфейса ProductService для работы с продуктами.
 */
public class ProductServiceImpl implements Service<ProductDto, ProductCreateDto> {
    private final CrudRepository<Product> repositoryImp;
    private final Mapper<ProductDto, ProductCreateDto, Product> mapper = new ProductMapperImpl();
    private final String ERROR_MESSAGE_NOT_FOUND = ConfigUtil.getProperty("ERROR_MESSAGE_NOT_FOUND");

    /**
     * Конструктор с параметром, инициализирующий репозиторий продуктов.
     *
     * @param repositoryImp репозиторий продуктов для взаимодействия с базой данных или другим источником данных
     */
    public ProductServiceImpl(ProductRepositoryImp repositoryImp) {
        this.repositoryImp = repositoryImp;
    }

    /**
     * Возвращает ProductDTO по заданному идентификатору.
     *
     * @param id идентификатор продукта
     * @return DTO продукта
     * @throws ElementNotFoundException если продукт с заданным id не найден
     * @throws ServiceException         если произошла ошибка на уровне сервиса
     */
    @Override
    public ProductDto getById(Long id) {
        try {
            Optional<Product> productOptional = repositoryImp.getById(id);
            // выкидывает исключение если Optional пустой
            Product product = productOptional.orElseThrow(() -> {
                String msg = ERROR_MESSAGE_NOT_FOUND.formatted(id);
                return new ElementNotFoundException(msg);
            });
            // возращает дто
            return mapper.fromEntityToResponseDto(product);
        } catch (RepositoryException ex) {
            throw new ServiceException(ex.getMessage(), ex);
        }
    }

    /**
     * Сохраняет новый продукт.
     *
     * @param dto DTO нового продукта
     * @return сохраненный DTO продукта
     * @throws ServiceException если произошла ошибка на уровне сервиса
     */
    @Override
    public ProductDto save(ProductCreateDto dto) {
        try {
            Product product = mapper.fromCreateDtoToEntity(dto);
            Product result = repositoryImp.save(product);
            // возращает дто
            return mapper.fromEntityToResponseDto(result);
        } catch (RepositoryException ex) {
            throw new ServiceException(ex.getMessage(), ex);
        }
    }

    /**
     * Обновляет информацию о продукте по новому DTO.
     *
     * @param newEntity новый DTO продукта
     * @return обновленный DTO продукта
     * @throws ElementNotFoundException если продукт с заданным id не найден
     * @throws ServiceException         если произошла ошибка на уровне сервиса
     */
    @Override
    public ProductDto updateByEntity(ProductDto newEntity) {
        try {
            Product newProduct = mapper.fromResponseDtoToEntity(newEntity);
            getById(newEntity.getId());
            Product updatedProduct = repositoryImp.updateByEntity(newProduct);
            // возращает дто
            return mapper.fromEntityToResponseDto(updatedProduct);
        } catch (RepositoryException ex) {
            throw new ServiceException(ex.getMessage(), ex);
        }
    }

    /**
     * Удаляет продукт по заданному идентификатору.
     *
     * @param id идентификатор продукта для удаления
     * @return удаленный DTO продукта
     * @throws ElementNotFoundException если продукт с заданным id не найден
     * @throws ServiceException         если произошла ошибка на уровне сервиса
     */
    @Override
    public ProductDto deleteById(Long id) {
        try {
            ProductDto productDTO = getById(id);
            repositoryImp.deleteById(id);
            // возращает дто 
            return productDTO;
        } catch (RepositoryException ex) {
            throw new ServiceException(ex.getMessage(), ex);
        }
    }
}
