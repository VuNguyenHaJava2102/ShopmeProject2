package com.shopme.admin.order;

import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderTrack;
import com.shopme.common.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderTrackRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testAddOrderTrack() {
        Order order = orderRepository.findById(16).get();

        OrderTrack track = new OrderTrack();
        track.setStatus(OrderStatus.PACKAGED);
        track.setNotes(track.getStatus().getDefaultDescription());
        track.setUpdatedTime(new Date());
        track.setOrder(order);

        order.getOrderTracks().add(track);
        orderRepository.save(order);
    }
}
