package com.example.userservice.repository;

import com.example.userservice.jpa.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    /**
     * User Id 로 찾기
     * @param userId
     * @return
     */
    UserEntity findByUserId(String userId);

    UserEntity findByEmail(String username);
}
