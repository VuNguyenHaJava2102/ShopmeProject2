package com.shopme.shoppingcart;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.product.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    // 1
    public int addProductToCartItem(int productId,
                                    int quantity,
                                    Customer customer) throws CartItemException {
        int updatedQuantity = quantity;
        Product product = productRepository.findById(productId).get();

        // Find CartItem by product and customer to check it null or not
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);

        if(cartItem != null) { // => Product already exists in cart, we just update quantity
            updatedQuantity = cartItem.getQuantity() + quantity;

            if(updatedQuantity > 5) {
                throw new CartItemException("Maximum allowed quantity of one product in cart is 5, you are having " + quantity + " item(s) of this product!");
            }
        } else { // => Product doesn't exist in cart, let add it
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCustomer(customer);
        }
        cartItem.setQuantity(updatedQuantity);

        cartItemRepository.save(cartItem);
        return updatedQuantity;
    }

    // 2
    public List<CartItem> getAllCartItemsByCustomer(Customer customer) {
        return cartItemRepository.findByCustomer(customer);
    }

    // 3
    public void updateCartItemQuantity(int quantity,
                                       int productId,
                                       Customer customer) {
        Product product = productRepository.findById(productId).get();
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }
}
