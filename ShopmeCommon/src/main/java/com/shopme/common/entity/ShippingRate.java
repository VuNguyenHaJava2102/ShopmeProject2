package com.shopme.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Entity
@Table(name = "shipping_rates")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShippingRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @Positive(message = "Shipping rate must be greater than 0!")
    private float rate;

    @Column(nullable = false)
    @Positive(message = "Days to deliver must be greater than 0!")
    private int days;

    @Column(name = "cod_supported", nullable = false)
    private boolean codSupported;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(nullable = false, length = 45)
    @Size(min = 1, max = 45, message = "Allowable length is 1-45 characters!")
    private String state;

    @Override
    public String toString() {
        return "ShippingRate{" +
                "id=" + id +
                ", rate=" + rate +
                ", days=" + days +
                ", codSupported=" + codSupported +
                ", country=" + country.getName() +
                ", state='" + state + '\'' +
                '}';
    }
}
