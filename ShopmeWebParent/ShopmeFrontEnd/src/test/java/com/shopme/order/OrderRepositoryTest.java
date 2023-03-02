package com.shopme.order;

import com.shopme.common.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testGetOrdersById() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = orderRepository.findAllByCustomerByKeyword(1, "Western", pageable);
        List<Order> orderList = orderPage.getContent();

        orderList.forEach(o -> System.err.println(o.getId()));
    }
}
