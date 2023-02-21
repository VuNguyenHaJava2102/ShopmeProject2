package com.shopme.shoppingcart;

import com.shopme.address.AddressService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.security.CustomerUserDetails;
import com.shopme.shipping.ShippingRateService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;
    private final AddressService addressService;
    private final ShippingRateService shippingRateService;

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

        // Handle shipping rate
        Address defaultAddress = addressService.getDefaultAddressOfCustomer(loggedCustomer.getId());
        ShippingRate shippingRate = null;

        boolean isPrimaryAddressUsed = true; // primary address is customer's address
        if(defaultAddress != null) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
            isPrimaryAddressUsed = false;
        } else {
            shippingRate = shippingRateService.getShippingRateForCustomer(loggedCustomer);
        }

        model.addAttribute("cartItems", cartItemList);
        model.addAttribute("estimatedTotal", estimatedTotal);
        model.addAttribute("isPrimaryAddressUsed", isPrimaryAddressUsed);
        model.addAttribute("isShippingSupported", shippingRate != null);

        return "cart/cart.html";
    }

    // 2
    @GetMapping("/cart/update/{productId}/{quantity}")
    public String updateCartItemQuantity(@PathVariable("productId") int productId,
                                         @PathVariable("quantity") int quantity,
                                         @AuthenticationPrincipal CustomerUserDetails customerUserDetails) {
        Customer loggedCustomer = customerUserDetails.getCustomer();
        cartItemService.updateCartItemQuantity(quantity, productId, loggedCustomer);
        return "redirect:/cart";
    }

    // 3
    @GetMapping("/cart/delete/{productId}")
    public String deleteCartItem(@PathVariable("productId") int productId,
                                 @AuthenticationPrincipal CustomerUserDetails customerUserDetails,
                                 RedirectAttributes redirectAttributes) {
        Customer loggedCustomer = customerUserDetails.getCustomer();
        cartItemService.deleteCartItem(productId, loggedCustomer);
        redirectAttributes.addFlashAttribute("message", "Delete item successful!");
        return "redirect:/cart";
    }

}
