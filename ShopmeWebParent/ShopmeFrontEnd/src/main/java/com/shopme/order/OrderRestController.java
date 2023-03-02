package com.shopme.order;

import com.shopme.common.exception.OrderNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class OrderRestController {

    private final OrderService orderService;

    @PostMapping("/orders/return")
    public ResponseEntity<?> returnItems(@RequestBody OrderReturnRequest returnRequest) {
        try {
            orderService.setOrderReturnRequest(returnRequest);
            return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);
        } catch(OrderNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
