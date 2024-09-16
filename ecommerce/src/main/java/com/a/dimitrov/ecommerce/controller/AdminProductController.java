package com.a.dimitrov.ecommerce.controller;

import com.a.dimitrov.ecommerce.model.Product;
import com.a.dimitrov.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> addNewProduct(@RequestBody Product product) {
        Product createdProduct = productService.addNewProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Optional<Product> existingProductOptional = productService.getProductById(id);

        if (!existingProductOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Product existingProduct = existingProductOptional.get();

        if (productDetails.getName() != null) {
            existingProduct.setName(productDetails.getName());
        }

        if (productDetails.getDescription() != null) {
            existingProduct.setDescription(productDetails.getDescription());
        }

        if (productDetails.getPrice() != null) {
            existingProduct.setPrice(productDetails.getPrice());
        }

        if (productDetails.getStock() != null) {
            existingProduct.setStock(productDetails.getStock());
        }

        Product updatedProduct = productService.updateProduct(existingProduct);

        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}