package com.shopme.order;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.Setting;
import com.shopme.common.exception.OrderNotFoundException;
import com.shopme.security.CustomerUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1
    @GetMapping("/orders")
    public String viewFirstOrderPage() {
        return "redirect:/orders/page/1?sortField=orderTime&sortDir=desc&orderKeyword=";
    }

    // 1.1
    @GetMapping("/orders/page/{pageNum}")
    public String viewOrdersByPage(@RequestParam("sortField") String sortField,
                                   @RequestParam("sortDir") String sortDir,
                                   @RequestParam("orderKeyword") String orderKeyword,
                                   @PathVariable("pageNum") int pageNum,
                                   @AuthenticationPrincipal CustomerUserDetails customerUserDetails,
                                   Model model) {
        Customer loggedCustomer = customerUserDetails.getCustomer();

        Page<Order> orderPage = orderService.getAllByCustomerByPage(sortField, sortDir, orderKeyword, pageNum, loggedCustomer.getId());

        List<Order> orderList = orderPage.getContent();
        long totalElements = orderPage.getTotalElements();
        long totalPages = orderPage.getTotalPages();

        long startCount = (pageNum - 1) * OrderService.NUMBER_OF_ORDER_PER_PAGE + 1;
        long endCount = startCount + OrderService.NUMBER_OF_ORDER_PER_PAGE - 1;
        if(endCount > totalElements) {
            endCount = totalElements;
        }

        model.addAttribute("orders", orderList);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("orderKeyword", orderKeyword);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "order/orders.html";
    }

    // 2
    @GetMapping("/orders/details/{id}")
    public String viewOrderDetailDialog(@PathVariable("id") int id,
                                        Model model) {
        try {
            Order order = orderService.getOrderById(id);
            model.addAttribute("order", order);
            return "order/order-details-dialog.html";
        } catch(OrderNotFoundException ex) {
            return "redirect:/orders";
        }
    }
}
