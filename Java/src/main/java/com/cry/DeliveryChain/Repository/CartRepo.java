package com.cry.DeliveryChain.Repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.cry.DeliveryChain.Entity.Cart;

public interface CartRepo extends CrudRepository<Cart, Long> {
    @Query("FROM Cart")
    List<Cart> findAll();

    @Query("FROM Cart WHERE BuyerId = :BuyerId")
    List<Cart> findAllByBuyerId(Integer BuyerId);

    @Query("FROM Cart WHERE BuyerId = :BuyerId AND ProductId = :ProductId")
    Cart findByBuyerIdAndProductId(Integer BuyerId, Integer ProductId);

    @Query("FROM Cart WHERE UniqueId = :UniqueId")
    Cart findByUniqueId(UUID UniqueId);
}
