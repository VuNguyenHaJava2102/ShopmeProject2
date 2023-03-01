package com.shopme.admin.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class OrderRestController {

    private final OrderService orderService;

    @PostMapping("/orders-shipper/{id}/update-status/{status}")
    public Response updateOrderStatus(@PathVariable("id") int orderId,
                                      @PathVariable("status") String status) {
        orderService.updateOrderStatus(orderId, status);
        return new Response(orderId, status);
    }
}

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class Response {
    private int orderId;
    private String status;
}
