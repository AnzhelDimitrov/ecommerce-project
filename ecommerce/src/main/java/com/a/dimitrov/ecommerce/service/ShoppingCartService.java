package com.a.dimitrov.ecommerce.service;

import com.a.dimitrov.ecommerce.model.Product;
import com.a.dimitrov.ecommerce.model.ShoppingCart;
import com.a.dimitrov.ecommerce.model.ShoppingCartProducts;
import com.a.dimitrov.ecommerce.model.User;
import com.a.dimitrov.ecommerce.repository.ShoppingCartRepository;
import com.a.dimitrov.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    public void addProductToCart(Long userId, Long productId, Integer quantity) {
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUser(user);
                    return shoppingCartRepository.save(newCart);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        boolean found = false;
        for (ShoppingCartProducts cartProduct : cart.getProducts()) {
            if (cartProduct.getProduct().getId().equals(productId)) {
                cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            ShoppingCartProducts newCartProduct = new ShoppingCartProducts(cart, product, quantity);
            cart.addProduct(newCartProduct);
        }

        shoppingCartRepository.save(cart);
    }

    public void removeProductFromCart(Long userId, Long productId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        ShoppingCartProducts cartProductToRemove = null;
        for (ShoppingCartProducts cartProduct : cart.getProducts()) {
            if (cartProduct.getProduct().equals(product)) {
                if (cartProduct.getQuantity() > 1) {
                    cartProduct.setQuantity(cartProduct.getQuantity() - 1);
                } else {
                    cartProductToRemove = cartProduct;
                }
                break;
            }
        }

        if (cartProductToRemove != null) {
            cart.getProducts().remove(cartProductToRemove);
        }

        shoppingCartRepository.save(cart);
    }

    public double getTotalPrice(Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        double totalPrice = 0.0;
        for (ShoppingCartProducts cartProduct : cart.getProducts()) {
            totalPrice += cartProduct.getProduct().getPrice() * cartProduct.getQuantity();
        }

        return totalPrice;
    }

    public List<ShoppingCartProducts> getProductsInCart(Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        return cart.getProducts();
    }
}
