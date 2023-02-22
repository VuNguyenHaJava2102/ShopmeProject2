package com.shopme.checkout;

import com.shopme.address.AddressService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.enums.PaymentMethod;
import com.shopme.order.OrderService;
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
    private final OrderService orderService;

    @GetMapping("/check-out")
    public String viewCheckOutPage(@AuthenticationPrincipal CustomerUserDetails customerUserDetails,
                                   Model model) {
        Customer customer = customerUserDetails.getCustomer();

        Address defaultAddress = addressService.getDefaultAddressOfCustomer(customer.getId());
        ShippingRate shippingRate = null;
        if(defaultAddress != null) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
            model.addAttribute("address", defaultAddress.getAddress());
        } else {
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
            model.addAttribute("address", customer.getAddress());
        }

        if(shippingRate == null) {
            return "redirect:/cart";
        }

        List<CartItem> cartItemList = cartItemService.getAllCartItemsByCustomer(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItemList, shippingRate);

        model.addAttribute("cartItems", cartItemList);
        model.addAttribute("checkoutInfo", checkoutInfo);
//        System.err.println(checkoutInfo);

        return "checkout/check-out.html";
    }

    @GetMapping("/place-order-cod")
    public String placeOrder(@AuthenticationPrincipal CustomerUserDetails customerUserDetails) {
        Customer loggedCustomer = customerUserDetails.getCustomer();
        Address defaultAddress = addressService.getDefaultAddressOfCustomer(loggedCustomer.getId());
        List<CartItem> cartItemList = cartItemService.getAllCartItemsByCustomer(loggedCustomer);
        if(cartItemList.size() == 0) {
            return "redirect:/";
        }

        ShippingRate shippingRate = null;
        if(defaultAddress != null) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            shippingRate = shippingRateService.getShippingRateForCustomer(loggedCustomer);
        }

        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItemList, shippingRate);
        Order newOrder = orderService.saveOrder(checkoutInfo, loggedCustomer, defaultAddress, cartItemList, PaymentMethod.COD);
        if(newOrder.getId() != null) {
            cartItemService.deleteByCustomerId(loggedCustomer.getId());
        }

        return "checkout/order-completed.html";
    }
}
