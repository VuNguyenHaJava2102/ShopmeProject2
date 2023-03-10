package com.shopme.admin.order;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderDetail;
import com.shopme.common.entity.Product;
import com.shopme.common.enums.OrderStatus;
import com.shopme.common.enums.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateOrder() {
        Customer customer = entityManager.find(Customer.class, 5);
        Product product = entityManager.find(Product.class, 5);

        Order mainOrder = new Order();

        mainOrder.copyAddressFromCustomer(customer);
        mainOrder.setOrderTime(new Date());
        mainOrder.setDaysToDeliver(2);
        mainOrder.setDeliveryDay(new Date());

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setQuantity(3);
        orderDetail1.setProductCost(product.getCost());
        orderDetail1.setUnitPrice(product.getPrice());
        orderDetail1.setSubtotal(product.getPrice() * 3);
        orderDetail1.setShippingCost(10);
        orderDetail1.setProduct(product);
        orderDetail1.setOrder(mainOrder);

        mainOrder.setProductCost(product.getCost());
        mainOrder.setShippingCost(10);
        mainOrder.setSubtotal(orderDetail1.getSubtotal());
        mainOrder.setTotal(orderDetail1.getSubtotal());
        mainOrder.setTax(0);

        mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        mainOrder.setStatus(OrderStatus.PICKED);
        mainOrder.setCustomer(customer);
        mainOrder.getOrderDetails().add(orderDetail1);

        orderRepository.save(mainOrder);
    }

    @Test
    public void testFindByOrderTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormatter.parse("2022-2-26");
        Date endDate = dateFormatter.parse("2022-12-2");

        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(endDate);
        endDateCalendar.add(Calendar.DATE, 1);
        endDate = endDateCalendar.getTime();

        List<Order> orderList = orderRepository.findByOrderTimeBetween(startDate, endDate);
        System.err.println("Result");
        orderList.forEach(o -> {
            System.out.printf("%-5s | %s | %-15.2f | %-15.2f | %-15.2f\n", o.getId(), o.getOrderTime(), o.getProductCost(), o.getSubtotal(), o.getTotal());
        });
    }
}
