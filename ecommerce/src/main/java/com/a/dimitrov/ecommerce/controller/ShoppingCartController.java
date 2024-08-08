package com.a.dimitrov.ecommerce.controller;

import com.a.dimitrov.ecommerce.model.ShoppingCart;
import com.a.dimitrov.ecommerce.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public ResponseEntity<Void> addProductToCart(@RequestParam Long userId, @RequestParam Long productId, @RequestParam Integer quantity) {
        shoppingCartService.addProductToCart(userId, productId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeProductFromCart(@RequestParam Long userId, @RequestParam Long productId) {
        shoppingCartService.removeProductFromCart(userId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/totalPrice/{userId}")
    public ResponseEntity<Double> getTotalPrice(@PathVariable Long userId) {
        double totalPrice = shoppingCartService.getTotalPrice(userId);
        return ResponseEntity.ok(totalPrice);
    }

    @GetMapping("/items")
    public ResponseEntity<List<ShoppingCart>> getAllItemsInCart() {
        List<ShoppingCart> items = shoppingCartService.getAllItemsInCart();
        return ResponseEntity.ok(items);
    }
}