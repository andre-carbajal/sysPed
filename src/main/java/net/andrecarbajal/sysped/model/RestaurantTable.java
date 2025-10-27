package net.andrecarbajal.sysped.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tables")
@Getter
@Setter
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TableStatus status = TableStatus.DISPONIBLE;
}
