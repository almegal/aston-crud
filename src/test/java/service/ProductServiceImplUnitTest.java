package service;

import db.UtilDB;
import db.UtilDBimpl;
import dto.product.ProductDto;
import entity.Product;
import exception.ElementNotFoundException;
import exception.RepositoryException;
import exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.impl.ProductRepositoryImp;
import service.impl.ProductServiceImpl;

import java.util.Optional;
import java.util.stream.Stream;

import static config.MockProps.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для ProductServiceImpl.
 * Этот класс тестирует методы ProductServiceImpl, чтобы убедиться, что они
 * корректно работают в различных сценариях.
 *
 * <p>Тестируемые сценарии:
 * <ul>
 *     <li>Получение продукта по ID, когда продукт существует</li>
 *     <li>Получение продукта по ID, когда продукт не существует</li>
 *     <li>Получение продукта по ID, когда возникает ошибка базы данных</li>
 *     <li>Сохранение продукта, проверка сохраненных данных</li>
 *     <li>Сохранение продукта, когда возникает ошибка базы данных</li>
 *     <li>Обновление продукта, когда продукт существует</li>
 *     <li>Обновление продукта, когда продукт не существует</li>
 *     <li>Обновление продукта, когда возникает ошибка базы данных</li>
 *     <li>Удаление продукта, когда продукт существует</li>
 *     <li>Удаление продукта, когда продукт не существует</li>
 *     <li>Удаление продукта, когда возникает ошибка базы данных</li>
 * </ul>
 * </p>
 */

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplUnitTest {
    @Mock
    private final UtilDB utilDB = UtilDBimpl.getInstance();

    @Mock
    private final ProductRepositoryImp repositoryImp = new ProductRepositoryImp(utilDB);

    @InjectMocks
    private ProductServiceImpl service;

    /**
     * Тестирует метод getById, чтобы убедиться, что он возвращает правильный ProductDTO
     * при наличии продукта с заданным ID в репозитории.
     */
    @Test
    @DisplayName("Получение продукта по ID, когда продукт существует")
    public void shouldReturnProductIfIdExist() throws RepositoryException {
        // Мокирование репозитория для возврата MOCK_PRODUCT при вызове getById с ID 1
        when(repositoryImp.getById(1L))
                .thenReturn(Optional.of(MOCK_PRODUCT));

        // Вызов метода и получение актуального результата
        ProductDto actual = service.getById(1L);

        // Проверка поведения метода и возвращаемого результата
        verify(repositoryImp, times(1)).getById(1L);

        // Проверка, что актуальные значения соответствуют ожидаемым
        assertEquals(PRODUCT_DTO_RESPONSE, actual);
    }

    /**
     * Тестирует метод getById, чтобы убедиться, что он выбрасывает исключение
     * при отсутствии продукта с заданным ID в репозитории.
     */
    @ParameterizedTest
    @ValueSource(longs = {1,3,4,5,6,7,10,330,34,2431453, 5435})
    @DisplayName("Получение продукта по ID, когда продукт не существует")
    public void shouldThrowExceptionIfIdNotExist(Long id) throws RepositoryException {
        // Мокирование репозитория для возврата пустого Optional при вызове getById с ID 1
        when(repositoryImp.getById(id))
                .thenReturn(Optional.empty());

        // Подготовка ожидаемого результата
        String expected = ERROR_MESSAGE_NOT_FOUND.formatted(id);

        // Ожидание выброса исключения и проверка его сообщения
        ElementNotFoundException thrown = assertThrows(ElementNotFoundException.class,
                () -> service.getById(id));
        assertEquals(expected, thrown.getMessage());
    }

    /**
     * Тестирует метод getById, чтобы убедиться, что он выбрасывает исключение
     * при ошибке доступа к базе данных.
     */
    @Test
    @DisplayName("Получение продукта по ID, когда возникает ошибка базы данных")
    public void shouldThrowExceptionOnDatabaseError() throws RepositoryException {
        // Мокирование репозитория для выброса исключения при вызове getById с любым ID
        when(repositoryImp.getById(anyLong()))
                .thenThrow(new RepositoryException(ERROR_MESSAGE_DATA_BASE));

        // Ожидание выброса исключения и проверка его сообщения
        ServiceException thrown = assertThrows(ServiceException.class,
                () -> service.getById(1L));
        assertEquals(ERROR_MESSAGE_DATA_BASE, thrown.getMessage());
    }

    /**
     * Тестирует метод save, чтобы убедиться, что он возвращает сохраненный ProductDTO.
     */
    @Test
    @DisplayName("Сохранение продукта, проверка сохраненных данных")
    public void shouldReturnSavedProduct() throws RepositoryException {
        // Мокирование репозитория для возврата сохраненного продукта
        when(repositoryImp.save(any(Product.class)))
                .thenReturn(MOCK_PRODUCT);

        // Вызов метода и получение актуального результата
        ProductDto actualProduct = service.save(PRODUCT_DTO_CREATE);

        // Проверка, что сохраненный продукт соответствует ожидаемому результату
        assertEquals(PRODUCT_DTO_RESPONSE, actualProduct);
    }


    /**
     * Тестирует метод save, чтобы убедиться, что он выбрасывает исключение
     * при ошибке доступа к базе данных.
     */
    @Test
    @DisplayName("Сохранение продукта, когда возникает ошибка базы данных")
    public void shouldThrowExceptionOnDatabaseErrorWhenSaving() throws RepositoryException {
        // Мокирование репозитория для выброса исключения при вызове save
        when(repositoryImp.save(any(Product.class)))
                .thenThrow(new RepositoryException(ERROR_MESSAGE_DATA_BASE));

        // Ожидание выброса исключения и проверка его сообщения
        ServiceException thrown = assertThrows(ServiceException.class,
                () -> service.save(PRODUCT_DTO_CREATE));
        assertEquals(ERROR_MESSAGE_DATA_BASE, thrown.getMessage());
    }

    /**
     * Тестирует метод updateByEntity, чтобы убедиться, что он обновляет и возвращает ProductDTO,
     * когда продукт с заданным ID существует.
     */
    @Test
    @DisplayName("Обновление продукта, когда продукт существует")
    public void shouldUpdateProductIfExist() throws RepositoryException {
        Long id = MOCK_PRODUCT.getId();

        // Мокирование репозитория для возврата MOCK_PRODUCT при вызове getById с заданным ID
        when(repositoryImp.getById(id))
                .thenReturn(Optional.of(MOCK_PRODUCT));

        // Мокирование репозитория для возврата обновленного продукта
        when(repositoryImp.updateByEntity(MOCK_PRODUCT))
                .thenReturn(MOCK_PRODUCT);

        // Вызов метода и получение актуального результата
        ProductDto actual = service.updateByEntity(PRODUCT_DTO_RESPONSE);

        // Проверка, что обновленный продукт соответствует ожидаемому результату
        assertEquals(PRODUCT_DTO_RESPONSE, actual);
    }

    /**
     * Тестирует метод updateByEntity, чтобы убедиться, что он выбрасывает исключение,
     * когда продукт с заданным ID не существует.
     */
    @Test
    @DisplayName("Обновление продукта, когда продукт не существует")
    public void shouldThrowExceptionIfProductNotExistWhenUpdate() throws RepositoryException {
        Long id = MOCK_PRODUCT.getId();

        // Мокирование репозитория для возврата пустого Optional при вызове getById с заданным ID
        when(repositoryImp.getById(id))
                .thenReturn(Optional.empty());

        // Ожидание выброса исключения и проверка его сообщения
        ElementNotFoundException thrown = assertThrows(ElementNotFoundException.class,
                () -> service.updateByEntity(PRODUCT_DTO_RESPONSE));
        String expected = ERROR_MESSAGE_NOT_FOUND.formatted(id);
        assertEquals(expected, thrown.getMessage());
    }

    /**
     * Тестирует метод updateByEntity, чтобы убедиться, что он выбрасывает исключение,
     * при ошибке доступа к базе данных во время обновления.
     */
    @Test
    @DisplayName("Обновление продукта, когда возникает ошибка базы данных")
    public void shouldThrowExceptionIfDBErrorOccurWhenUpdate() throws RepositoryException {
        Long id = MOCK_PRODUCT.getId();

        // Мокирование репозитория для возврата MOCK_PRODUCT при вызове getById с заданным ID
        when(repositoryImp.getById(id))
                .thenReturn(Optional.of(MOCK_PRODUCT));

        // Мокирование репозитория для выброса исключения при вызове updateByEntity
        when(repositoryImp.updateByEntity(MOCK_PRODUCT))
                .thenThrow(new RepositoryException(ERROR_MESSAGE_DATA_BASE));

        // Ожидание выброса исключения и проверка его сообщения
        ServiceException thrown = assertThrows(ServiceException.class,
                () -> service.updateByEntity(PRODUCT_DTO_RESPONSE));
        assertEquals(ERROR_MESSAGE_DATA_BASE, thrown.getMessage());
    }

    /**
     * Тестирует метод deleteById, чтобы убедиться, что он удаляет продукт и возвращает```java
     * его ProductDTO, когда продукт с заданным ID существует.
     */
    @Test
    @DisplayName("Удаление продукта, когда продукт существует")
    public void shouldDeleteProduct() throws RepositoryException {
        Long id = MOCK_PRODUCT.getId();

        // Мокирование репозитория для возврата MOCK_PRODUCT при вызове getById с заданным ID
        when(repositoryImp.getById(id))
                .thenReturn(Optional.of(MOCK_PRODUCT));

        // Мокирование репозитория для выполнения deleteById без выброса исключений
        doNothing().when(repositoryImp).deleteById(id);

        // Вызов метода и получение актуального результата
        ProductDto actual = service.deleteById(id);

        // Проверка, что удаленный продукт соответствует ожидаемому результату
        assertEquals(PRODUCT_DTO_RESPONSE, actual);
    }

    /**
     * Тестирует метод deleteById, чтобы убедиться, что он выбрасывает исключение,
     * когда продукт с заданным ID не существует.
     */
    @Test
    @DisplayName("Удаление продукта, когда продукт не существует")
    public void shouldThrowExceptionIfProductNotExistWhenDelete() throws RepositoryException {
        Long id = MOCK_PRODUCT.getId();

        // Мокирование репозитория для возврата пустого Optional при вызове getById с заданным ID
        when(repositoryImp.getById(id))
                .thenReturn(Optional.empty());

        // Подготовка ожидаемого текста ошибки
        String expected = ERROR_MESSAGE_NOT_FOUND.formatted(id);

        // Ожидание выброса исключения и проверка его сообщения
        ElementNotFoundException thrown = assertThrows(ElementNotFoundException.class,
                () -> service.deleteById(id));
        assertEquals(expected, thrown.getMessage());
    }

    /**
     * Тестирует метод deleteById, чтобы убедиться, что он выбрасывает исключение,
     * при ошибке доступа к базе данных во время удаления.
     */
    @Test
    @DisplayName("Удаление продукта, когда возникает ошибка базы данных")
    public void shouldThrowExceptionIfDBErrorOccurWhenDelete() throws RepositoryException {
        Long id = MOCK_PRODUCT.getId();

        // Мокирование репозитория для возврата MOCK_PRODUCT при вызове getById с заданным ID
        when(repositoryImp.getById(id))
                .thenReturn(Optional.of(MOCK_PRODUCT));

        // Мокирование репозитория для выброса исключения при вызове deleteById
        doThrow(new RepositoryException(ERROR_MESSAGE_DATA_BASE))
                .when(repositoryImp).deleteById(id);

        // Ожидание выброса исключения и проверка его сообщения
        ServiceException thrown = assertThrows(ServiceException.class,
                () -> service.deleteById(id));
        assertEquals(ERROR_MESSAGE_DATA_BASE, thrown.getMessage());
    }
}
