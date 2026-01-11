package ru.practicum.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.warehouse.model.WarehouseProduct;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<WarehouseProduct, Long> {

    List<WarehouseProduct> findByProductIdIn(Collection<UUID> productId);

    boolean existsByProductId(UUID productId);
}
