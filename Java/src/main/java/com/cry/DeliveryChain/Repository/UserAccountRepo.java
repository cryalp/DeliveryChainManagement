package com.cry.DeliveryChain.Repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.cry.DeliveryChain.Entity.UserAccount;

public interface UserAccountRepo extends CrudRepository<UserAccount, Long> {
    @Query("FROM UserAccount")
    List<UserAccount> findAll();

    @Query("FROM UserAccount WHERE Username = :Username")
    UserAccount findByUsername(String Username);

    @Query("FROM UserAccount WHERE Email = :Email")
    UserAccount findByEmail(String Email);

    @Query("FROM UserAccount WHERE Username = :Username OR Email = :Email")
    UserAccount findByUsernameOrEmail(String Username, String Email);

    @Query("FROM UserAccount WHERE UniqueId = :UniqueId")
    UserAccount findByUniqueId(UUID UniqueId);
}
