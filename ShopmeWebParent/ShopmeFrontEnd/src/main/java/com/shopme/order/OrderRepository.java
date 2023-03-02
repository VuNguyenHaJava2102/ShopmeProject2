package com.shopme.order;

import com.shopme.common.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("""
            SELECT o FROM Order o
            WHERE o.customer.id = :customerId
            """)
    Page<Order> findAllByCustomer(@Param("customerId") int customerId,
                                  Pageable pageable);

//    @Query(value = """
//            SELECT * from ORDERS o
//            JOIN order_details od ON o.id = od.order_id
//            JOIN products p ON od.product_id = p.id
//            WHERE o.customer_id = :customerId
//            AND (p.name LIKE %:keyword% OR o.status LIKE %:keyword%)
//            """, nativeQuery = true)
//    Page<Order> findAllByCustomerByKeyword(@Param("customerId") int customerId,
//                                           @Param("keyword") String keyword,
//                                           Pageable pageable);

    @Query("""
            SELECT DISTINCT o FROM Order o
            JOIN o.orderDetails od
            JOIN od.product p
            WHERE o.customer.id = :customerId
            AND (p.name LIKE %:keyword% OR o.status LIKE %:keyword%)
            """)
    Page<Order> findAllByCustomerByKeyword(@Param("customerId") int customerId,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);
}
