package com.cry.DeliveryChain.Repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.cry.DeliveryChain.Entity.BillProduct;

public interface BillProductRepo extends CrudRepository<BillProduct, Long> {
    @Query("FROM BillProduct")
    List<BillProduct> findAll();
    
    @Query("FROM BillProduct WHERE SupplierId = :SupplierId")
    List<BillProduct> findAllBySupplierId(Integer SupplierId);
    
    @Query("FROM BillProduct WHERE SupplierId = (SELECT Id FROM UserAccount WHERE UniqueId = :SupplierUniqueId)")
    List<BillProduct> findAllBySupplierUniqueId(UUID SupplierUniqueId);

    @Query("FROM BillProduct WHERE BillId = :BillId")
    List<BillProduct> findAllByBillId(Integer BillId);
    
    @Query("FROM BillProduct WHERE BillId = (SELECT Id FROM Bill WHERE UniqueId = :BillUniqueId)")
    List<BillProduct> findAllByBillUniqueId(UUID BillUniqueId);

    @Query("FROM BillProduct WHERE ProductId = :ProductId")
    List<BillProduct> findAllByProductId(Integer ProductId);

    @Query("FROM BillProduct WHERE ProductId = (SELECT Id FROM Product WHERE UniqueId = :ProductUniqueId)")
    List<BillProduct> findAllByProductUniqueId(UUID ProductUniqueId);
}
