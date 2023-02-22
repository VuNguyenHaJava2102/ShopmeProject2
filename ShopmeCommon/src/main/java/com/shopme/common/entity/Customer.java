package com.shopme.common.entity;

import com.shopme.common.enums.AuthenticationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "customers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Email(message = "E-mail must be in well-formed!")
    @NotBlank(message = "E-mail can not be blank!")
    @Size(min = 8, max = 45, message = "The allowed length is 8-45 characters!")
    @Column(unique = true, nullable = false, length = 45)
    private String email;

    @NotBlank(message = "Password can not be blank!")
    @Size(min = 6, max = 64, message = "The allowed length is 6-64 characters!")
    @Column(nullable = false, length = 64)
    private String password;

    @NotBlank(message = "First name can not be blank!")
    @Size(min = 2, max = 45, message = "The allowed length is 2-45 characters!")
    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;

    @NotBlank(message = "Last name can not be blank!")
    @Size(min = 2, max = 45, message = "The allowed length is 2-45 characters!")
    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;

    @NotBlank(message = "Phone number can not be blank!")
    @Size(min = 7, max = 15, message = "The allowed length is 7-15 characters!")
    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @NotBlank(message = "Address line 1 can not be blank!")
    @Size(min = 3, max = 45, message = "The allowed length is 3-45 characters!")
    @Column(name = "address_line_1", nullable = false, length = 45)
    private String addressLine1;

    @Size(max = 45, message = "The maximum allowed length is 45 characters!")
    @Column(name = "address_line_2", length = 45)
    private String addressLine2;

    @Column(nullable = false, length = 45)
    private String city;

    @Size(max = 45, message = "Maximum allowed length is 45 characters!")
    @Column(length = 45)
    private String state;

    @NotBlank(message = "Postal code can not be blank!")
    @Size(min = 2, max = 10, message = "The allowed length is 2-10 characters!")
    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    private boolean enabled;

    @Column(name = "created_time")
    private Date createdTime;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type", length = 10)
    private AuthenticationType authenticationType;

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
        if(!city.isBlank()) {
            address += ", " + city;
        }

        if(!state.equals(city) && !state.isBlank()) {
            address += ", " + state;
        }
        address += ", " + country.getName();
        address += ". Postal code: " + (postalCode.isBlank() ? "Not available" : postalCode);
        address += ". Phone number: " + phoneNumber;

        return address;
    }
}
