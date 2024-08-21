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

        ShoppingCart cart = shoppingCartRepository.findByUserId(userId).orElseGet(() -> {
            ShoppingCart newCart = new ShoppingCart();
            newCart.setUser(user);
            return shoppingCartRepository.save(newCart);
        });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        ShoppingCartProducts cartProduct = cart.getProducts().stream()
                .filter(p -> p.getProduct().getId().equals(productId))
                .findFirst()
                .orElseGet(() -> new ShoppingCartProducts(cart, product, quantity));

        cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
        cart.addProduct(cartProduct);

        shoppingCartRepository.save(cart);
    }

    public void removeProductFromCart(Long userId, Long productId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        cart.getProducts().removeIf(cartProduct -> cartProduct.getProduct().equals(product));

        shoppingCartRepository.save(cart);
    }

    public double getTotalPrice(Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        return cart.getProducts().stream()
                .mapToDouble(cartProduct -> cartProduct.getProduct().getPrice() * cartProduct.getQuantity())
                .sum();
    }

    public List<ShoppingCartProducts> getProductsInCart(Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found"));

        return cart.getProducts();
    }
}
