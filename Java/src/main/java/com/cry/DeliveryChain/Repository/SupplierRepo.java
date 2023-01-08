package com.cry.DeliveryChain.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cry.DeliveryChain.Entity.Supplier;

@Repository
public interface SupplierRepo extends CrudRepository<Supplier, Long> {
    @Query("FROM Supplier")
    List<Supplier> findAll();

    @Query("FROM Supplier WHERE Name = :Name")
    Supplier findByName(String Name);

    @Query("FROM Supplier WHERE UniqueId = :UniqueId")
    Supplier findByUniqueId(UUID UniqueId);
}