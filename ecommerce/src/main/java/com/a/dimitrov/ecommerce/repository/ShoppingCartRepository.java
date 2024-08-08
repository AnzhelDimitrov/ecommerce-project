package com.a.dimitrov.ecommerce.repository;

import com.a.dimitrov.ecommerce.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    List<ShoppingCart> findByUserId(Long userId);

    List<ShoppingCart> findByProductId(Long productId);
}
