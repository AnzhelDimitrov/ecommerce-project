package com.a.dimitrov.ecommerce.service;

import com.a.dimitrov.ecommerce.model.ShoppingCart;
import com.a.dimitrov.ecommerce.model.User;
import com.a.dimitrov.ecommerce.model.Product;
import com.a.dimitrov.ecommerce.repository.ShoppingCartRepository;
import com.a.dimitrov.ecommerce.repository.UserRepository;
import com.a.dimitrov.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public void addProductToCart(Long userId, Long productId, Integer quantity) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalUser.isEmpty() || optionalProduct.isEmpty()) {
            throw new IllegalArgumentException("User or Product not found");
        }

        User user = optionalUser.get();
        Product product = optionalProduct.get();

        List<ShoppingCart> existingCartItems = shoppingCartRepository.findByUserId(userId);

        boolean productFound = false;

        for (ShoppingCart item : existingCartItems) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                shoppingCartRepository.save(item);
                productFound = true;
                break;
            }
        }

        if (!productFound) {
            ShoppingCart newCartItem = new ShoppingCart();
            newCartItem.setUser(user);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);
            shoppingCartRepository.save(newCartItem);
        }
    }

    public void removeProductFromCart(Long userId, Long productId) {
        List<ShoppingCart> cartItems = shoppingCartRepository.findByUserId(userId);

        for (ShoppingCart item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                shoppingCartRepository.delete(item);
                break;
            }
        }
    }

    public double getTotalPrice(Long userId) {
        List<ShoppingCart> cartItems = shoppingCartRepository.findByUserId(userId);

        double totalPrice = 0.0;

        for (ShoppingCart item : cartItems) {
            double itemTotalPrice = item.getProduct().getPrice() * item.getQuantity();
            totalPrice += itemTotalPrice;
        }

        return totalPrice;
    }

    public List<ShoppingCart> getAllItemsInCart() {
        return shoppingCartRepository.findAll();
    }
}