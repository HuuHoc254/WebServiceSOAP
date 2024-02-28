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
    @Procedure(name = "allocate_inventory")
    @Modifying
    void allocateInventory(
              @Param("productId") Integer productId
            , @Param("quantity") Integer quantity );

    @Modifying
    @Query(value = " into allocation(product_id,quantity,allocation_date)"
                 + "value(:productId,:quantity,current_date())", nativeQuery = true)
    void createAllocation(
              @Param("productId") Integer productId
            , @Param("quantity") Integer quantity );
}
