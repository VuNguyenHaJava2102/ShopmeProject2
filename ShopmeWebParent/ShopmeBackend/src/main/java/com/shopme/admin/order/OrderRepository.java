package com.shopme.admin.order;

import com.shopme.common.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("""
            SELECT o FROM Order o
            WHERE CONCAT(o.customer.firstName, ' ', o.customer.lastName, ' ', o.firstName, ' ', o.lastName, ' ', o.phoneNumber, ' ', o.addressLine1,
            ' ', o.addressLine2, ' ', o.country, ' ', o.state, ' ', o.city, ' ', o.postalCode, ' ', o.paymentMethod, ' ', o.status) LIKE %:keyword%
            """)
    Page<Order> findByKeywordAndPage(@Param("keyword") String keyword,
                                     Pageable pageable);

    @Query("""
            SELECT o FROM Order o
            WHERE o.id = :id
            """)
    Page<Order> searchForShipperById(@Param("id") int id,
                                     Pageable pageable);

    @Query("""
            SELECT o FROM Order o
            WHERE CONCAT(o.firstName, ' ', o.lastName) LIKE %:keyword%
            """)
    Page<Order> searchForShipperByName(@Param("keyword") String keyword,
                                       Pageable pageable);

    @Query("""
            SELECT NEW com.shopme.common.entity.Order(o.id, o.orderTime, o.productCost, o.subtotal, o.total)
            FROM Order o
            WHERE o.orderTime BETWEEN :startTime AND :endTime
            ORDER BY o.orderTime
            """)
    List<Order> findByOrderTimeBetween(@Param("startTime") Date startTime,
                                       @Param("endTime") Date endTime);
}
