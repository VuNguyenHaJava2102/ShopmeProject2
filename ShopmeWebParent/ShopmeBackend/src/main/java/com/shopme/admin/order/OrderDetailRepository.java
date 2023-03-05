package com.shopme.admin.order;

import com.shopme.common.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    @Query("""
            SELECT NEW com.shopme.common.entity.OrderDetail(od.product.category.name, od.id, od.quantity, od.productCost, od.subtotal, od.shippingCost)
            FROM OrderDetail od
            WHERE od.order.orderTime BETWEEN :startTime AND :endTime
            ORDER BY od.order.orderTime
            """)
    List<OrderDetail> findWithCategoryAndTimeBetween(@Param("startTime") Date startTime,
                                                     @Param("endTime") Date endTime);

    @Query("""
            SELECT NEW com.shopme.common.entity.OrderDetail(od.id, od.product.name, od.quantity, od.productCost, od.subtotal, od.shippingCost)
            FROM OrderDetail od
            WHERE od.order.orderTime BETWEEN :startTime AND :endTime
            ORDER BY od.order.orderTime
            """)
    List<OrderDetail> findWithProductAndTimeBetween(@Param("startTime") Date startTime,
                                                    @Param("endTime") Date endTime);
}
