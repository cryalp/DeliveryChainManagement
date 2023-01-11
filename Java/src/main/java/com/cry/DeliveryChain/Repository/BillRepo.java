package com.cry.DeliveryChain.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cry.DeliveryChain.Entity.Bill;

public interface BillRepo extends CrudRepository<Bill, Long> {
    @Query("FROM Bill")
    List<Bill> findAll();

    @Query("FROM Bill WHERE BuyerId = :BuyerId")
    List<Bill> findAllByBuyerId(Integer BuyerId);

    @Query("FROM Bill WHERE BuyerId = :BuyerId AND IsApproved = :IsApproved")
    List<Bill> findAllByBuyerIdAndIsApproved(Integer BuyerId, Boolean IsApproved);

    @Query("FROM Bill WHERE UniqueId = :UniqueId")
    Bill findByUniqueId(UUID UniqueId);
}
