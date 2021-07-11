package com.saokanneh.auth.io.repositories;

import com.saokanneh.auth.io.entity.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
    UserEntity findUserByEmail(String email);
    UserEntity findByUserId(String id);
    UserEntity findUserByEmailVerificationToken(String token);
}
