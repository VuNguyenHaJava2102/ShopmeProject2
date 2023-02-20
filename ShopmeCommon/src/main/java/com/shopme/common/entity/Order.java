package com.shopme.common.entity;

import com.shopme.common.enums.OrderStatus;
import com.shopme.common.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 45)
    @NotBlank(message = "First name can't be blank!")
    @Size(min = 1, max = 45, message = "Allowed length is 1-45 characters!")
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 45)
    @NotBlank(message = "Last name can't be blank!")
    @Size(min = 1, max = 45, message = "Allowed length is 1-45 characters!")
    private String lastName;

    @Column(name = "phone_number", nullable = false, length = 15)
    @NotBlank(message = "Phone number can't be blank!")
    @Size(min = 1, max = 15, message = "Allowed length is 1-15 characters!")
    private String phoneNumber;

    @Column(name = "address_line_1", nullable = false, length = 64)
    @NotBlank(message = "Address line 1 can't be blank!")
    @Size(min = 1, max = 64, message = "Allowed length is 1-64 characters!")
    private String addressLine1;

    @Column(name = "address_line_2", length = 64)
    @Size(max = 64, message = "Maximum allowed length is 64 characters!")
    private String addressLine2;

    @Column(length = 45)
    @Size(max = 45, message = "Maximum allowed length is 45 characters!")
    private String city;

    @Column(length = 45)
    @Size(max = 45, message = "Maximum allowed length is 45 characters!")
    private String state;

    @Column(name = "postal_code", nullable = false, length = 10)
    @Size(max = 10, message = "Maximum allowed length is 10 characters!")
    private String postalCode;

    @Column(nullable = false, length = 45)
    private String country;

    private Date orderTime;
    private int daysToDeliver;
    private Date deliveryDay;

    private float productCost;
    private float shippingCost;
    private float subtotal;
    private float total;
    private float tax;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    public void copyAddressFromCustomer(Customer customer) {
        setFirstName(customer.getFirstName());
        setLastName(customer.getLastName());
        setPhoneNumber(customer.getPhoneNumber());
        setAddressLine1(customer.getAddressLine1());
        setAddressLine2(customer.getAddressLine2());
        setCity(customer.getCity());
        setState(customer.getState());
        setPostalCode(customer.getPostalCode());
        setCountry(customer.getCountry().getName());
    }

    @Transient
    public String getDestination() {
        return city + (state.equals(city) ? "" : ", " + state) + ", " + country;
    }
}
