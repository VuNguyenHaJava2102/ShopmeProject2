package com.shopme.checkout;

import com.shopme.address.AddressService;
import com.shopme.common.classes.CurrencySettingBag;
import com.shopme.common.classes.EmailSettingBag;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.enums.PaymentMethod;
import com.shopme.order.OrderService;
import com.shopme.security.CustomerUserDetails;
import com.shopme.setting.SettingService;
import com.shopme.shipping.ShippingRateService;
import com.shopme.shoppingcart.CartItemService;
import com.shopme.utility.MailHelper;
import com.shopme.utility.Util;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@AllArgsConstructor
public class CheckoutController {

    private final AddressService addressService;
    private final ShippingRateService shippingRateService;
    private final CartItemService cartItemService;
    private final CheckoutService checkoutService;
    private final OrderService orderService;
    private final SettingService settingService;

    // 1
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
        // System.err.println(checkoutInfo);

        return "checkout/check-out.html";
    }

    // 2
    @GetMapping("/place-order-cod")
    public String placeOrder(@AuthenticationPrincipal CustomerUserDetails customerUserDetails)
            throws MessagingException, UnsupportedEncodingException {
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
        sendConfirmationEmail(newOrder);

        return "checkout/order-completed.html";
    }

    // 2.1
    private void sendConfirmationEmail(Order order)
            throws MessagingException, UnsupportedEncodingException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedOrderTime = sdf.format(order.getOrderTime());
        String orderIdStr = String.valueOf(order.getId());

        EmailSettingBag emailSettingBag = settingService.getEmailSettingBag();
        CurrencySettingBag currencySettingBag = settingService.getCurrencySettingBag();

        JavaMailSenderImpl mailSender = MailHelper.prepareMailSender(emailSettingBag);
        mailSender.setDefaultEncoding("utf-8");
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        String toEmailAddress = order.getCustomer().getEmail();
        String subject = emailSettingBag.getOrderConfirmationSubject();
        String content = emailSettingBag.getOrderConfirmationContent();

        subject = subject.replace("[[orderId]]", orderIdStr);
        content = content.replace("[[fullName]]", order.getFullName());
        content = content.replace("[[orderId]]", orderIdStr);
        content = content.replace("[[orderTime]]", formattedOrderTime);
        content = content.replace("[[shippingAddress]]", order.getAddress());
        content = content.replace("[[total]]", Util.formatCurrency(currencySettingBag, order.getTotal()));
        content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());

        mimeMessageHelper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
        mimeMessageHelper.setTo(toEmailAddress);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, false);

        mailSender.send(mimeMessage);
    }
}
