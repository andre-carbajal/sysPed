package net.andrecarbajal.sysped.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@jakarta.persistence.Table(name="order_details")
@Getter
@Setter
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="order_id", nullable=false)
    private Order order;

    @ManyToOne
    @JoinColumn(name="plate_id", nullable=false)
    private Plate plate;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double priceUnit;

    @Column(length = 255)
    private String notes;
}
