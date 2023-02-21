package com.shopme.checkout;

import com.shopme.address.AddressService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.security.CustomerUserDetails;
import com.shopme.shipping.ShippingRateService;
import com.shopme.shoppingcart.CartItemService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class CheckoutController {

    private final AddressService addressService;
    private final ShippingRateService shippingRateService;
    private final CartItemService cartItemService;
    private final CheckoutService checkoutService;

    @GetMapping("/check-out")
    public String viewCheckOutPage(@AuthenticationPrincipal CustomerUserDetails customerUserDetails,
                                   Model model) {
        Customer customer = customerUserDetails.getCustomer();

        Address defaultAddress = addressService.getDefaultAddressOfCustomer(customer.getId());
        ShippingRate shippingRate = null;
        if(defaultAddress != null) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }
        List<CartItem> cartItemList = cartItemService.getAllCartItemsByCustomer(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItemList, shippingRate);

        System.out.println(checkoutInfo.toString());
        return "redirect:/";
    }
}
