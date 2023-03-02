package com.shopme.common.entity;

import com.shopme.common.IdBasedEntity;
import com.shopme.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "order_track")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderTrack extends IdBasedEntity {

    @Column(length = 300)
    private String notes;

    private Date updatedTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 45)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
