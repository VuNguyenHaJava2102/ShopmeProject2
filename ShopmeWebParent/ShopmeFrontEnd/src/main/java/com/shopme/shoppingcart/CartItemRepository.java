package com.shopme.shoppingcart;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByCustomer(Customer customer);

    CartItem findByCustomerAndProduct(Customer customer, Product product);

    @Query("""
            UPDATE CartItem ci
            SET ci.quantity = :quantity
            WHERE ci.customer.id = :customerId AND ci.product.id = :productId
            """)
    @Modifying
    void updateQuantityCartItem(@Param("quantity") int quantity,
                                @Param("customerId") int customerId,
                                @Param("productId") int productId);

    @Query("""
            DELETE FROM CartItem ci
            WHERE ci.customer.id = :customerId AND ci.product.id = :productId
            """)
    @Modifying
    void deleteByCustomerAndProduct(@Param("customerId") int customerId,
                                    @Param("productId") int productId);

    void deleteByCustomerId(int customerId);
}
