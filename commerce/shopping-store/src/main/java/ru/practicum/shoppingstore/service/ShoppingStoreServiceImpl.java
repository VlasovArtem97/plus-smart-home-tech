package ru.practicum.shoppingstore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.contract.interactionapi.dto.shoppingstore.*;
import ru.practicum.shoppingstore.exception.ProductNotFoundException;
import ru.practicum.shoppingstore.mapping.ProductMapper;
import ru.practicum.shoppingstore.model.Product;
import ru.practicum.shoppingstore.repositiory.ShoppingStoreRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ShoppingStoreRepository shoppingStoreRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.info("Получен запрос на получение продуктов категории: {}, с пагинацией: {}", category, pageable);
        PageRequest page = Pageable.of(pageable.getFrom(), pageable.getSize(), pageable.getSort());
        Page<ProductDto> products = shoppingStoreRepository.findByProductCategory(category, page)
                .map(productMapper::toProductDto);
        log.debug("Список полученных продуктов: {}", products);
        return products;
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        log.info("Получен запрос на добавление товара: {}", productDto);
        Product product = productMapper.toProduct(productDto);
        shoppingStoreRepository.save(product);
        ProductDto productSave = productMapper.toProductDto(product);
        log.debug("Сохраненный продукт: {}", productSave);
        return productSave;
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("Получен запрос на обновление данных по товару: {}", productDto);
        UUID id = productDto.getProductId();
        Product product = findProductById(id);
        Product updateProduct = productMapper.toUpdateProduct(product, productDto);
        shoppingStoreRepository.save(updateProduct);
        log.debug("Обновленный продукт: {}", updateProduct);
        return productMapper.toProductDto(updateProduct);
    }

    @Override
    public boolean removeProductFromStore(UUID id) {
        log.info("Получен запрос на удаление товара из витрины магазина с id: {}", id);
        Product product = findProductById(id);
        product.setProductState(ProductState.DEACTIVATE);
        shoppingStoreRepository.save(product);
        log.debug("Товар с id: {} успешно удален из витрины магазина", id);
        return true;
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        log.info("Получен запрос на установление количества товара на витрине магазина. id товара: {}, количество: {}",
                request.getProductId(), request.getQuantityState());
        Product product = findProductById(request.getProductId());
        product.setQuantityState(request.getQuantityState());
        shoppingStoreRepository.save(product);
        log.debug("Установлено новое значение: {}", request.getQuantityState());
        return true;
    }

    @Transactional(readOnly = true)
    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("Получен запрос на поиск товара по id: {}", productId);
        Product product = findProductById(productId);
        return productMapper.toProductDto(product);
    }

    @Transactional(readOnly = true)
    private Product findProductById(UUID productId) {
        Product product = shoppingStoreRepository.findByProductId(productId).orElseThrow(() -> {
            log.error("По id: {} продукт не найден", productId);
            return new ProductNotFoundException("Ошибка, товар по идентификатору в БД не найден");
        });
        log.debug("По id: {} найден продукт: {}", productId, product);
        return product;
    }
}
