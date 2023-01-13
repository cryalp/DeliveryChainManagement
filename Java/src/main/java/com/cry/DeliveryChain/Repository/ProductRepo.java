package com.cry.DeliveryChain.Repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.cry.DeliveryChain.Entity.Product;

public interface ProductRepo extends CrudRepository<Product, Long> {
    @Query("FROM Product")
    List<Product> findAll();

    @Query("FROM Product WHERE UserAccountId = :UserAccountId")
    List<Product> findAllByUserAccountId(Integer UserAccountId);

    @Query("FROM Product WHERE UserAccountId = (SELECT Id FROM UserAccount WHERE UniqueId = :SupplierUniqueId)")
    List<Product> findAllBySupplierUniqueId(UUID SupplierUniqueId);

    @Query("FROM Product WHERE IsActive = :IsActive")
    List<Product> findAllByIsActive(Boolean IsActive);

    @Query("FROM Product WHERE Quantity < :Quantity")
    List<Product> findAllByLessQuantity(Integer Quantity);

    @Query("FROM Product WHERE IsActive = :IsActive AND Quantity >= :Quantity")
    List<Product> findAllByIsActiveAndQuantity(Boolean IsActive, Integer Quantity);

    @Query("FROM Product WHERE Header = :Header")
    Product findByHeader(String Header);

    @Query("FROM Product WHERE UniqueId = :UniqueId")
    Product findByUniqueId(UUID UniqueId);
}
