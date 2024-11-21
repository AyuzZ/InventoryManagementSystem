package com.example.inventorymanagementsystem.repository;

import com.example.inventorymanagementsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findUserByUsername(String username);

    @Query("SELECT u.uid FROM User u WHERE u.username=?1")
    Optional<Integer> getUserId(String username);


}
