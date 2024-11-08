package com.example.inventorymanagementsystem.repository;

import com.example.inventorymanagementsystem.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

//    @Query("SELECT r FROM Role r WHERE r.rName=?1")
    Optional<Role> findRoleByName(String name);


}
