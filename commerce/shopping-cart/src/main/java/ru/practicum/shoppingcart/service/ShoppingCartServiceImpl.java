package ru.practicum.shoppingcart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ChangeProductQuantityRequest;
import ru.practicum.contract.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.practicum.contract.interactionapi.exception.fiegnclient.BadRequestException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.InternalServerErrorException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.NotAuthorizedException;
import ru.practicum.contract.interactionapi.exception.fiegnclient.NotFoundException;
import ru.practicum.contract.interactionapi.feignclient.WarehouseClient;
import ru.practicum.shoppingcart.exception.CartStateDeactivateException;
import ru.practicum.shoppingcart.exception.NoProductsInShoppingCartException;
import ru.practicum.shoppingcart.exception.NotAuthorizedUserException;
import ru.practicum.shoppingcart.exception.NotFoundUserException;
import ru.practicum.shoppingcart.mapper.ShoppingCartMapper;
import ru.practicum.shoppingcart.model.CartState;
import ru.practicum.shoppingcart.model.ShoppingCart;
import ru.practicum.shoppingcart.repository.ShoppingCartRepository;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WarehouseClient warehouseClient;

    @Transactional(readOnly = true)
    @Override
    public ShoppingCartDto getShoppingCart(String userName) {
        /*
    Можно было бы использовать аннотацию @NotBlank в параметрах метода. Но так как у нас в спецификации к ТЗ говорится
    о том, что необходимо проверить вручную и вызвать своё исключение, делаем именно так.
     */
        log.info("Получен запрос на получение корзины пользователя пользователем с именем: [ {} ]", userName);
        checkUserName(userName);
        ShoppingCart shoppingCart = findShoppingCartByUserName(userName);
        return shoppingCartMapper.toShoppingCartDtoFromShoppingCart(shoppingCart);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(Map<UUID, Long> products, String userName) {
        log.info("Получен запрос на добавление товаров в корзину от пользователя с именем: [ {} ]. Товары: {}",
                userName, products);
        checkUserName(userName);
        return findExistingShoppingCartByUserName(userName, products);
    }

    @Override
    public void deactivateCurrentShoppingCart(String userName) {
        log.info("Получен запрос на деактивацию корзины от пользователя с именем: [ {} ]", userName);
        checkUserName(userName);
        ShoppingCart shoppingCart = findShoppingCartByUserName(userName);
        shoppingCart.setCartState(CartState.DEACTIVATE);
        ShoppingCart deactiveShoppingCart = shoppingCartRepository.save(shoppingCart);
        log.debug("Деактивированная корзина: {}", deactiveShoppingCart);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String userName, List<UUID> productId) {
        log.info("Получен запрос на удаление продуктов из корзины пользователя: [ {} ], id продуктов: {}",
                userName, productId);
        checkUserName(userName);
        ShoppingCart shoppingCart = findShoppingCartByUserName(userName);
        checkCartState(shoppingCart);
        ShoppingCart updateShoppingCart = removeProductFromShoppingCart(shoppingCart, productId);
        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toShoppingCartDtoFromShoppingCart(
                shoppingCartRepository.save(updateShoppingCart));
        log.debug("Возвращенный объект пользователю: {}", shoppingCartDto);
        return shoppingCartDto;
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String userName, ChangeProductQuantityRequest quantityRequest) {
        log.info("Получен запрос на изменение количество товаров в корзине от пользователя : [ {} ]. " +
                "Измененное количество: {}", userName, quantityRequest);

        checkUserName(userName);
        ShoppingCart shoppingCart = findShoppingCartByUserName(userName);
        checkCartState(shoppingCart);
        checkProductIdFromShoppingCart(shoppingCart.getProducts(), List.of(quantityRequest.getProductId()));

        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toShoppingCartDtoFromShoppingCart(shoppingCart);
        shoppingCartDto.getProducts().put(quantityRequest.getProductId(), quantityRequest.getNewQuantity());
        increaseQuantity(shoppingCartDto);

        shoppingCart.getProducts().put(quantityRequest.getProductId(), quantityRequest.getNewQuantity());
        shoppingCartRepository.save(shoppingCart);
        ShoppingCartDto shoppingCartDtoUpdate = shoppingCartMapper.toShoppingCartDtoFromShoppingCart(shoppingCart);
        log.debug("Количество продуктов обновлено. {}", shoppingCartDto);
        return shoppingCartDto;
    }

    private ShoppingCartDto findExistingShoppingCartByUserName(String userName, Map<UUID, Long> products) {
        ShoppingCart shoppingCart;
        Optional<ShoppingCart> shoppingCartOptional = shoppingCartRepository.findByUserName(userName);
        if (shoppingCartOptional.isPresent()) {
            log.debug("Найдена корзина покупателя: {}", shoppingCartOptional);
            shoppingCart = shoppingCartOptional.get();
            checkCartState(shoppingCart);
            ShoppingCartDto shoppingCartDto = shoppingCartMapper.toShoppingCartDtoFromShoppingCart(shoppingCart);
            ShoppingCartDto updateQuantity = shoppingCartMapper.toUpdateQuantity(shoppingCartDto, products);
            increaseQuantity(updateQuantity);
            shoppingCart.setProducts(updateQuantity.getProducts());
            shoppingCartRepository.save(shoppingCart);
            ShoppingCartDto updateShoppingCart = shoppingCartMapper.toShoppingCartDtoFromShoppingCart(shoppingCart);
            log.debug("Обновленная корзина покупателя: {}", updateShoppingCart);
            return updateShoppingCart;
        } else {
            log.debug("Корзина покупателя [ {} ] не найдена. Создается новая", userName);
            shoppingCart = shoppingCartRepository.save(ShoppingCart.builder()
                    .userName(userName)
                    .products(products)
                    .build());
            ShoppingCartDto shoppingCartDto = shoppingCartMapper.toShoppingCartDtoFromShoppingCart(shoppingCart);
            increaseQuantity(shoppingCartDto);
            log.debug("Обновленная корзина: {}", shoppingCartDto);
            return shoppingCartDto;
        }
    }

    private ShoppingCart findShoppingCartByUserName(String userName) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserName(userName).orElseThrow(() -> {
            log.error("Корзина пользователя с именем: [ {} ] отсутствует", userName);
            return new NotFoundUserException("У пользователя с именем : [ " + userName + " ] " +
                    "отсутствует корзина товаров");
        });
        log.debug("Полученная корзина: {}", shoppingCart);
        return shoppingCart;
    }

    private void checkUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            log.error("Отсутствует имя пользователя");
            throw new NotAuthorizedUserException("Отсутствует имя пользователя");
        }
    }

    private ShoppingCartDto increaseQuantity(ShoppingCartDto shoppingCartDto) {
        log.info("Начинается проверка количества продуктов в корзине покупателя и на складе");
        try {
            warehouseClient.checkProductQuantityEnoughForShoppingCart(shoppingCartDto);
            log.debug("Добавленная корзина: {}", shoppingCartDto);
            return shoppingCartDto;
        } catch (NotFoundUserException e) {
            throw new NotFoundException(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InternalServerErrorException e) {
            throw new InternalServerErrorException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private ShoppingCart removeProductFromShoppingCart(ShoppingCart shoppingCart, List<UUID> productId) {
        log.info("Начинается процесс удаление продуктов из корзины");
        checkProductIdFromShoppingCart(shoppingCart.getProducts(), productId);
        productId.forEach(shoppingCart.getProducts()::remove);
        log.debug("Обновленная корзина после удаления продуктов: {}", shoppingCart);
        return shoppingCart;
    }

    private void checkProductIdFromShoppingCart(Map<UUID, Long> shoppingCart, List<UUID> productId) {
        log.info("Начинается проверка наличия продуктов в корзине покупателя, которые планируются удалить");
        List<String> message = new ArrayList<>();
        for (UUID product : productId) {
            if (!shoppingCart.containsKey(product)) {
                log.error("Продукт с id: [ {} ] не найден в корзине покупателя", product);
                message.add("Продукт с id: [ " + product + " ] не найден в корзине покупателя");
            }
        }

        if (!message.isEmpty()) {
            throw new NoProductsInShoppingCartException(String.join("\n", message));
        }
        log.debug("Все продукты найдены в корзине покупателя");
    }

    private void checkCartState(ShoppingCart shoppingCart) {
        if (shoppingCart.getCartState().equals(CartState.DEACTIVATE)) {
            log.error("Корзина недоступна для изменений: {}", shoppingCart.getCartState());
            throw new CartStateDeactivateException("Корзина недоступна для изменений: " + shoppingCart.getCartState());
        }
    }
}
