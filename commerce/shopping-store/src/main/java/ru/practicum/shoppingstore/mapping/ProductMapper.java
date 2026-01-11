package ru.practicum.shoppingstore.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.contract.interactionapi.dto.shoppingstore.ProductDto;
import ru.practicum.shoppingstore.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toProductDto(Product product);

    Product toProduct(ProductDto productDto);

    @Mapping(target = "productId", ignore = true)
    Product toUpdateProduct(@MappingTarget Product product, ProductDto productDto);
}
