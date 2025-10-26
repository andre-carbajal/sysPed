package net.andrecarbajal.sysped.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "staffs_audit")
@Getter
@Setter
public class StaffAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 8)
    private String dni;

    @Column(nullable = false)
    private String name;

    @Column(name = "rol_name")
    private String rolName;

    @Column(nullable = false)
    private String action;

    @Column(name = "when_event", nullable = false)
    private OffsetDateTime whenEvent;

    @Column(name = "performed_by")
    private String performedBy;
}
