package com.manufacturing.oee.production.order.repository;

import com.manufacturing.oee.production.order.entity.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link ProductionOrder} entity.
 */
@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {

    Optional<ProductionOrder> findByOrderNumber(String orderNumber);

    boolean existsByOrderNumber(String orderNumber);

    List<ProductionOrder> findByLineId(Long lineId);

    List<ProductionOrder> findByProductId(Long productId);

    List<ProductionOrder> findByStatus(String status);

    List<ProductionOrder> findByLineIdAndStatus(Long lineId, String status);

    @Query("SELECT po FROM ProductionOrder po WHERE po.scheduledStart >= :startDate AND po.scheduledEnd <= :endDate")
    List<ProductionOrder> findOrdersInWindow(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}
