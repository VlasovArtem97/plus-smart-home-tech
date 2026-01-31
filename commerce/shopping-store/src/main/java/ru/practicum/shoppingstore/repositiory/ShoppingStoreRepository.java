package ru.practicum.shoppingstore.repositiory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.contract.interactionapi.dto.shoppingstore.ProductCategory;
import ru.practicum.shoppingstore.model.Product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShoppingStoreRepository extends JpaRepository<Product, Long> {

    Page<Product> findByProductCategory(ProductCategory category, PageRequest pageRequest);

    Optional<Product> findByProductId(UUID uuid);

    List<Product> findByProductIdIn(Collection<UUID> uuids);
}
