package com.shopme.admin.customer;

import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CustomerRestController {

    private final CustomerService customerService;

    @PostMapping("/customers/check-email")
    public String checkCustomerEmail(@Param("id") int id,
                                     @Param("email") String email) {
        return customerService.isEmailUnique(id, email) ? "Unique" : "Not unique";
    }
}
