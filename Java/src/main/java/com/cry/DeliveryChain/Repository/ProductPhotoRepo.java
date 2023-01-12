package com.cry.DeliveryChain.Repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.cry.DeliveryChain.Entity.ProductPhoto;

public interface ProductPhotoRepo extends CrudRepository<ProductPhoto, Long> {
    @Query("FROM ProductPhoto")
    List<ProductPhoto> findAll();

    @Query("FROM ProductPhoto WHERE ProductId = (SELECT Id FROM Product WHERE UniqueId = :ProductUniqueId)")
    List<ProductPhoto> findAllByProductUniqueId(UUID ProductUniqueId);

    @Query("FROM ProductPhoto WHERE UniqueId = :UniqueId")
    ProductPhoto findByUniqueId(UUID UniqueId);
}
