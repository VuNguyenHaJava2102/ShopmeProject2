package com.shopme.shoppingcart;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.security.CustomerUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    // 1
    @GetMapping("/cart")
    public String viewCart(@AuthenticationPrincipal CustomerUserDetails customerUserDetails,
                           Model model) {
        Customer loggedCustomer = customerUserDetails.getCustomer();

        List<CartItem> cartItemList = cartItemService.getAllCartItemsByCustomer(loggedCustomer);

        float estimatedTotal = 0.0f;
        for(CartItem item : cartItemList) {
            estimatedTotal += item.getSubtotal();
        }

        model.addAttribute("cartItems", cartItemList);
        model.addAttribute("estimatedTotal", estimatedTotal);

        return "cart/cart.html";
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateCartItemQuantity(@PathVariable("productId") int productId,
                                         @PathVariable("quantity") int quantity,
                                         @AuthenticationPrincipal CustomerUserDetails customerUserDetails) {
        Customer loggedCustomer = customerUserDetails.getCustomer();
        cartItemService.updateCartItemQuantity(quantity, loggedCustomer.getId(), productId);
        System.out.println("controller");
        return "redirect:/cart";
    }
}
