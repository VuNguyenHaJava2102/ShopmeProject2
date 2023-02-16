package com.shopme.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "E-mail should not be blank")
    @Email(message = "E-mail must be in well-formed")
    @Size(min = 8, max = 128, message = "E-mail must have between 8-128 characters")
    @Column(unique = true, nullable = false, length = 128)
    private String email;

    @NotBlank(message = "Password should not be blank")
    @Size(min = 8, max = 64, message = "Password must have between 8-60 characters")
//    @Pattern(regexp = "[a-zA-Z0-9]+", message = "Password can only consist of characters and digits")
    @Column(nullable = false, length = 60)
    private String password;

    @NotBlank(message = "First name should not be blank")
    @Size(min = 2, max = 45, message = "First name must have between 2-45 characters")
    @Pattern(regexp = "[a-zA-Z\\s]+", message = "First name can only consist of characters")
    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;

    @NotBlank(message = "Last name should not be blank")
    @Size(min = 2, max = 45, message = "Last name must have between 2-45 characters")
    @Pattern(regexp = "[a-zA-Z\\s]+", message = "Last name can only consist of characters")
    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;

    @Column(length = 64)
    private String photo;
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Transient
    private String confirmPassword;

    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", photo='" + photo + '\'' +
                ", enabled=" + enabled +
                ", roles=" + roles +
                '}';
    }

    @Transient
    public String getPhotoPath() {
        if(this.photo == null)
            return "/images/default-user.png";
        return "/user-photos/" + this.id + "/" + this.photo;
    }

    @Transient
    public String getFormattedId() {
        String finalId = "SM";
        for(int i = 1; i <= (5 - String.valueOf(this.id).length()); ++i) {
            finalId += "0";
        }
        return finalId + String.valueOf(this.id);
    }
}
