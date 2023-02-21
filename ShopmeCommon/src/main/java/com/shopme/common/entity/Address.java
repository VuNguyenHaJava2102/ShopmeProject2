package com.shopme.common.entity;

import com.shopme.common.IdBasedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Address extends IdBasedEntity {

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

    @Column(nullable = false, length = 45)
    @Size(max = 45, message = "Maximum allowed length is 45 characters!")
    private String city;

    @Column(length = 45)
    @Size(max = 45, message = "Maximum allowed length is 45 characters!")
    private String state;

    @Column(name = "postal_code", nullable = false, length = 10)
    @Size(max = 10, message = "Maximum allowed length is 10 characters!")
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(name = "default_address")
    private boolean defaultForShipping;

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", customer=" + customer.getFullName() +
                ", country=" + country.getName() +
                ", defaultForShipping=" + defaultForShipping +
                '}';
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Transient
    public String getAddress() {
        String address = getFullName() + ", Address 1: " + addressLine1;
        if(!addressLine2.isBlank()) {
            address += ", Address 2: " + addressLine2;
        }
        if(!addressLine2.isBlank()) {
            address += ", " + city;
        }
        if(!city.equals(state) && !state.isBlank()) {
            address += ", " + state;
        }
        address += ", " + country.getName();
        address += ". Postal code: " + (postalCode.isBlank() ? "Not available" : postalCode);
        address += ". Phone number: " + phoneNumber;

        return address;
    }
}
