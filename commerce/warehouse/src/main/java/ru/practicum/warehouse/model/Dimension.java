package ru.practicum.warehouse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Dimension {

    @Column(nullable = false)
    private double depth;

    @Column(nullable = false)
    private double height;

    @Column(nullable = false)
    private double width;

    public double getVolume() {
        return this.depth * this.height * this.width;
    }
}
