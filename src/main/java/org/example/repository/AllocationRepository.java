package org.example.repository;

import org.example.entity.AllocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AllocationRepository extends JpaRepository<AllocationEntity, Integer> {
    @Procedure(name = "AllocateInventory")
    @Modifying
    void AllocateInventory(
              @Param("productId") Integer productId
            , @Param("quantity") Integer quantity );

}
