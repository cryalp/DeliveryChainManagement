package com.cry.DeliveryChain.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cry.DeliveryChain.Entity.Product;

@Repository
public interface ProductRepo extends CrudRepository<Product, Long> {
    @Query("FROM Product")
    List<Product> findAll();

    @Query("FROM Product WHERE Header = :Header")
    Product findByHeader(String Header);

    @Query("FROM Product WHERE UniqueId = :UniqueId")
    Product findByUniqueId(UUID UniqueId);
}