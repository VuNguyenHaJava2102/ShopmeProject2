package com.shopme.admin.shippingrate;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ShippingRateRestController {

    private final ShippingRateService shippingRateService;

    @PostMapping("/shipping-rates/check-country-state")
    public String checkCountryAndState(@RequestParam("id") String id,
                                       @RequestParam("countryId") String countryId,
                                       @RequestParam("state") String state) {
        return shippingRateService.checkCountryAndState(Integer.valueOf(id), Integer.valueOf(countryId), state);
    }
}
