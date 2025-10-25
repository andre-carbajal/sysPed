package net.andrecarbajal.sysped.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@jakarta.persistence.Table(name = "tables")
@Getter
@Setter
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TableStatus status = TableStatus.DISPONIBLE;

    public boolean isOperativa() {
        return this.status != TableStatus.FUERA_DE_SERVICIO;
    }
}
