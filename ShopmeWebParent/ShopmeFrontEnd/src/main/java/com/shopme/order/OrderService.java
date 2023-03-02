package com.shopme.order;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderDetail;
import com.shopme.common.entity.OrderTrack;
import com.shopme.common.entity.Product;
import com.shopme.common.enums.OrderStatus;
import com.shopme.common.enums.PaymentMethod;
import com.shopme.common.exception.OrderNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@AllArgsConstructor
public class OrderService {

    public static final int NUMBER_OF_ORDER_PER_PAGE = 5;

    private final OrderRepository orderRepository;

    // 1
    public Order saveOrder(CheckoutInfo checkoutInfo,
                           Customer customer,
                           Address address,
                           List<CartItem> cartItemList,
                           PaymentMethod paymentMethod) {
        Order newOrder = new Order();

        if(address == null) {
            newOrder.copyAddressFromCustomer(customer);
        } else {
            newOrder.copyAddressFromAddress(address);
        }

        newOrder.setOrderTime(new Date());
        newOrder.setDaysToDeliver(checkoutInfo.getDaysToDeliver());
        newOrder.setDeliveryDay(checkoutInfo.getDeliveryDate());

        newOrder.setProductCost(checkoutInfo.getProductCostTotal());
        newOrder.setShippingCost(checkoutInfo.getProductShippingCostTotal());
        newOrder.setSubtotal(checkoutInfo.getProductPriceTotal());
        newOrder.setTotal(checkoutInfo.getPaymentTotal());
        newOrder.setTax(0.0f);

        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setStatus(OrderStatus.NEW);
        newOrder.setCustomer(customer);

        // Convert cart item to order detail
        Set<OrderDetail> orderDetailSet = new HashSet<>();
        for(CartItem item : cartItemList) {
            Product product = item.getProduct();
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setProductCost(product.getCost());
            orderDetail.setUnitPrice(product.getDiscountPrice());
            orderDetail.setSubtotal(item.getSubtotal());
            orderDetail.setShippingCost(item.getShippingCost());
            orderDetail.setProduct(product);
            orderDetail.setOrder(newOrder);

            orderDetailSet.add(orderDetail);
        }
        newOrder.setOrderDetails(orderDetailSet);
        return orderRepository.save(newOrder);
    }

    // 2
    public Page<Order> getAllByCustomerByPage(String sortField,
                                              String sortDir,
                                              String keyword,
                                              int pageNum,
                                              int customerId) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_OF_ORDER_PER_PAGE, sort);
        if(keyword.equals("")) {
            return orderRepository.findAllByCustomer(customerId, pageable);
        }
        return orderRepository.findAllByCustomerByKeyword(customerId, keyword, pageable);
    }

    // 3
    public Order getOrderById(int orderId) throws OrderNotFoundException {
        try {
            Order order = orderRepository.findById(orderId).get();
            return order;
        } catch(NoSuchElementException ex) {
            throw new OrderNotFoundException("Could not find any order with ID " + orderId);
        }
    }

    // 4
    public void setOrderReturnRequest(OrderReturnRequest returnRequest) throws OrderNotFoundException {
        Order order = getOrderById(returnRequest.getOrderId());
        if(order.isReturnedRequested()) return;

        OrderTrack orderTrack = new OrderTrack();

        String note = "Reason: " + returnRequest.getReason();
        if(!returnRequest.getNote().equals("")) {
            note += ". ".concat(returnRequest.getNote());
        }
        orderTrack.setNote(note);
        orderTrack.setUpdatedTime(new Date());
        orderTrack.setStatus(OrderStatus.RETURNED_REQUESTED);
        orderTrack.setOrder(order);

        order.getOrderTracks().add(orderTrack);
        order.setStatus(OrderStatus.RETURNED_REQUESTED);
        orderRepository.save(order);
    }
}
