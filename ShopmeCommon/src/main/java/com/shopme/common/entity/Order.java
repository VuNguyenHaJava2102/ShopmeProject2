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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
    @OrderBy("quantity")
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @OrderBy("updatedTime ASC")
    private List<OrderTrack> orderTracks = new ArrayList<>();

    // Constructor
    public Order(Integer id, Date orderTime, float productCost, float subtotal, float total) {
        this.id = id;
        this.orderTime = orderTime;
        this.productCost = productCost;
        this.subtotal = subtotal;
        this.total = total;
    }

    // Transient
    @Transient
    public String getAddress() {
        String address = getFullName() + ", Address 1: " + addressLine1;
        if(!addressLine2.isBlank()) {
            address += ", Address 2: " + addressLine2;
        }
        if(!city.isBlank()) {
            address += ", " + city;
        }

        if(!state.equals(city) && !state.isBlank()) {
            address += ", " + state;
        }
        address += ", " + country;
        address += ". Postal code: " + (postalCode.isBlank() ? "Not available" : postalCode);
        address += ". Phone number: " + phoneNumber;

        return address;
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Transient
    public String getDestination() {
        String destination = city;
        if(!state.isBlank()) {
            destination += ", " + state;
        }
        destination += ", " + country;
        return destination;
    }

    @Transient
    public String formatDeliveryDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(deliveryDay);
    }

    @Transient
    public String getRecipientAddress() {
        String address = addressLine1;
        if(!addressLine2.isBlank()) {
            address += ", Address 2: " + addressLine2;
        }
        if(!city.isBlank()) {
            address += ", " + city;
        }

        if(!state.equals(city) && !state.isBlank()) {
            address += ", " + state;
        }
        address += ", " + country;
        address += ". Postal code: " + (postalCode.isBlank() ? "Not available" : postalCode);

        return address;
    }

    @Transient
    public boolean isOrderCod() {
        return paymentMethod.equals(PaymentMethod.COD);
    }

    @Transient
    public boolean isProcessing() {
        return hasStatus(OrderStatus.PROCESSING);
    }

    @Transient
    public boolean isPicked() {
        return hasStatus(OrderStatus.PICKED);
    }

    @Transient
    public boolean isShipping() {
        return hasStatus(OrderStatus.SHIPPING);
    }

    @Transient
    public boolean isDelivered() {
        return hasStatus(OrderStatus.DELIVERED);
    }

    @Transient
    public boolean isReturnedRequested() {
        return hasStatus(OrderStatus.RETURN_REQUESTED);
    }

    @Transient
    public boolean isReturned() {
        return hasStatus(OrderStatus.RETURNED);
    }

    // Utility method
    public boolean hasStatus(OrderStatus status) {
        for(OrderTrack track : this.orderTracks) {
            if(track.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }

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

    public void copyAddressFromAddress(Address address) {
        setFirstName(address.getFirstName());
        setLastName(address.getLastName());
        setPhoneNumber(address.getPhoneNumber());
        setAddressLine1(address.getAddressLine1());
        setAddressLine2(address.getAddressLine2());
        setCity(address.getCity());
        setState(address.getState());
        setPostalCode(address.getPostalCode());
        setCountry(address.getCountry().getName());
    }
}
