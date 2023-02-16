package com.shopme.shoppingcart;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.security.CustomerUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

        model.addAttribute("cartItems", cartItemList);

        return "cart/cart.html";
    }
}
