package com.shopme.admin.order;

import com.shopme.common.entity.OrderDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderDetailRepositoryTest {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Test
    public void testFindWithCategoryAndTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormatter.parse("2021-08-01");
        Date endDate = dateFormatter.parse("2021-08-31");

        List<OrderDetail> orderDetailList = orderDetailRepository.findWithCategoryAndTimeBetween(startDate, endDate);
        System.err.println("Result");
        orderDetailList.forEach(od -> {
            System.out.printf("%-30s | %d | %d | %-15.2f | %-15.2f | %-15.2f\n",
                    od.getProduct().getCategory().getName(), od.getId(), od.getQuantity(), od.getProductCost(), od.getSubtotal(), od.getShippingCost());
//            System.out.println(od.getProduct().getAlias());
        });
    }

    @Test
    public void testFindWithProductAndTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormatter.parse("2021-08-01");
        Date endDate = dateFormatter.parse("2021-08-31");

        List<OrderDetail> orderDetailList = orderDetailRepository.findWithProductAndTimeBetween(startDate, endDate);
        System.err.println("Result");
        orderDetailList.forEach(od -> {
            System.out.printf("%-60s | %d | %d | %-15.2f | %-15.2f | %-15.2f\n",
                    od.getProduct().getShorterName(), od.getId(), od.getQuantity(), od.getProductCost(), od.getSubtotal(), od.getShippingCost());
        });
    }
}
