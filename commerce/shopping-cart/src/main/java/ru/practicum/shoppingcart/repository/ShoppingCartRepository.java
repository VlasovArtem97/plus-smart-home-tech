package ru.practicum.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shoppingcart.model.ShoppingCart;

import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    Optional<ShoppingCart> findByUserName(String userName);
}
