package com.shopme.shoppingcart;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testSaveCartItem() {
        Customer customer1 = entityManager.find(Customer.class, 1);
        Product product1 = entityManager.find(Product.class, 1);

        CartItem cartItem = new CartItem(customer1, product1, 3);
        cartItemRepository.save(cartItem);
    }

    @Test
    public void testGetAllByCustomer() {
        Customer customer1 = entityManager.find(Customer.class, 1);
        List<CartItem> cartItemList = cartItemRepository.findByCustomer(customer1);

        System.err.println(cartItemList.size());
    }

    @Test
    public void testGetAllByCustomerAndProduct() {
        Customer customer1 = entityManager.find(Customer.class, 1);
        Product product1 = entityManager.find(Product.class, 1);

        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer1, product1);

        System.err.println(cartItem);
    }

    @Test
    public void testUpdateQuantityCartItem() {
        cartItemRepository.updateQuantityCartItem(3, 1, 91);
        CartItem cartItem = entityManager.find(CartItem.class, 3);
        System.err.println(cartItem);
    }

    @Test
    public void testDeleteByCustomerAndProduct() {
        cartItemRepository.deleteByCustomerAndProduct(1, 1);
    }
}
