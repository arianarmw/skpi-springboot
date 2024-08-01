package com.ariana.springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ariana.springboot.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    User findByRole(String role);

    @Override
    Optional<User> findById(Integer userId);

    // @Query("SELECT u FROM User u WHERE u.username = :username")

    Optional<User> findUserByUsername(@Param("username") String username);

}
