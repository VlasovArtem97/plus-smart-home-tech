package ru.practicum.contract.interactionapi.dto.shoppingstore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Pageable {

    private int from;
    private int size;
    private String sort;

    public static PageRequest of(int from, int size, String sort) {
        int page = from / size;

        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size);
        }

        Sort.Order order = parseSortOrder(sort);
        if (order == null) {
            return PageRequest.of(page, size);
        } else {
            return PageRequest.of(page, size, Sort.by(order));
        }
    }

    private static Sort.Order parseSortOrder(String param) {
        if (param == null || param.trim().isEmpty()) {
            return null;
        }

        String[] params = param.split(",");

        if (params.length < 1 || params.length > 2) {
            return null;
        }

        String name = params[0].trim();
        if (name.isEmpty()) {
            return null;
        }

        if (params.length > 1) {
            String sortDirection = params[1].trim().toLowerCase();
            switch (sortDirection) {
                case "asc" -> {
                    return Sort.Order.asc(name);
                }
                case "desc" -> {
                    return Sort.Order.desc(name);
                }
                default -> {
                    return null;
                }
            }
        } else {
            return Sort.Order.asc(name);
        }
    }
}
