package com.shopme.shoppingcart;

import com.shopme.common.entity.Customer;
import com.shopme.security.CustomerUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CartItemRestController {

    private final CartItemService cartItemService;

    // 1
    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable("productId") int productId,
                                   @PathVariable("quantity") int quantity,
                                   @AuthenticationPrincipal CustomerUserDetails customerUserDetails) {
        Customer loggedCustomer = customerUserDetails.getCustomer();
        if(loggedCustomer == null) {
            return "You must log in to add this product!";
        }

        try {
            int updatedQuantity = cartItemService.addProductToCartItem(productId, quantity, loggedCustomer);
            return updatedQuantity + " item(s) were added to shopping cart!";
        } catch(CartItemException ex) {
            return ex.getMessage();
        }
    }


}
