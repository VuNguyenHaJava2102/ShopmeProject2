package com.shopme.admin.order;

import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.Setting;
import com.shopme.common.exception.OrderNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1
    @GetMapping("/orders")
    public String viewFirstOrderPage() {
        return "redirect:/orders/page/1?sortField=orderTime&sortDir=asc&keyword=";
//        return viewOrderByPage("orderTime", "asc", " ", 1, model);
    }

    // 1.1
    @GetMapping("/orders/page/{pageNum}")
    public String viewOrderByPage(@RequestParam("sortField") String sortField,
                                  @RequestParam("sortDir") String sortDir,
                                  @RequestParam("keyword") String keyword,
                                  @PathVariable("pageNum") int pageNum,
                                  Model model,
                                  @AuthenticationPrincipal ShopmeUserDetails loggedUser) {
        String rolesString = loggedUser.getAuthorities().toString();
        boolean isShipper = rolesString.contains("Shipper") && !rolesString.contains("Admin") && !rolesString.contains("Salesperson");
        Page<Order> orderPage = null;

        if(isShipper) {
            orderPage = orderService.getOrdersByKeywordAndPage(sortField, sortDir, "for-shipper".concat(keyword), pageNum);
        } else {
            orderPage = orderService.getOrdersByKeywordAndPage(sortField, sortDir, keyword, pageNum);
        }

        List<Order> orderList = orderPage.getContent();
        List<Setting> currencySettingList = orderService.getAllCurrencySetting();

        long totalElements = orderPage.getTotalElements();
        long totalPages = orderPage.getTotalPages();

        long startCount = (pageNum - 1) * OrderService.NUMBER_OF_ORDER_PER_PAGE + 1;
        long endCount = startCount + OrderService.NUMBER_OF_ORDER_PER_PAGE - 1;
        if(endCount > totalElements) {
            endCount = totalElements;
        }

        for(Setting setting : currencySettingList) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }

        model.addAttribute("orders", orderList);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");


        if(isShipper) {
            return "order/orders-for-shipper.html";
        }
        return "order/orders.html";
    }

    // 2
    @GetMapping("/orders/details/{id}")
    public String viewDetailOrder(@PathVariable("id") int id,
                                  RedirectAttributes redirectAttributes,
                                  Model model,
                                  @AuthenticationPrincipal ShopmeUserDetails loggedUser) {
        String rolesString = loggedUser.getAuthorities().toString();
        boolean isShipper = rolesString.contains("Shipper") && !rolesString.contains("Admin") && !rolesString.contains("Salesperson");

        try {
            Order order = orderService.getOrderById(id);
            List<Setting> currencySettingList = orderService.getAllCurrencySetting();

            model.addAttribute("order", order);
            for(Setting setting : currencySettingList) {
                model.addAttribute(setting.getKey(), setting.getValue());
            }

            if(isShipper) {
                return "order/order-details-dialog-for-shipper.html";
            }
            return "order/order-details-dialog.html";
        } catch(OrderNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/orders";
        }
    }

    // 3
    @GetMapping("/orders/edit/{id}")
    public String viewEditOrderForm(@PathVariable("id") int id,
                                    Model model,
                                    RedirectAttributes ra) {
        try {
            Order order = orderService.getOrderById(id);
            List<Country> countryList = orderService.getAllCountries();

            model.addAttribute("order", order);
            model.addAttribute("pageTitle", "Edit Order ID(" + id + ")");
            model.addAttribute("countries", countryList);

            return "order/order-form.html";
        } catch(OrderNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/orders";
        }
    }
}
