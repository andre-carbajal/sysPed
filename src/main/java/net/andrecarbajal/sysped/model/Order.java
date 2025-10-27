package net.andrecarbajal.sysped.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@jakarta.persistence.Table(name="orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="table_id", nullable=false)
    private Table table;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(nullable = false)
    private LocalDateTime dateandtimeOrder = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderStatus status = OrderStatus.PENDIENTE;

    @Column(nullable = false)
    private BigDecimal priceTotal;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetails> details = new ArrayList<>();

    public void addOrderDetail(OrderDetails detail) {
        details.add(detail);
        detail.setOrder(this);
    }

    public void removeOrderDetail(OrderDetails detail) {
        details.remove(detail);
        detail.setOrder(null);
    }
}
