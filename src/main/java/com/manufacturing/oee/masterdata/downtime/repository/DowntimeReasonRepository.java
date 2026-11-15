package com.manufacturing.oee.masterdata.downtime.repository;

import com.manufacturing.oee.masterdata.downtime.entity.DowntimeReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link DowntimeReason} entity.
 */
@Repository
public interface DowntimeReasonRepository extends JpaRepository<DowntimeReason, Long> {

    Optional<DowntimeReason> findByReasonCode(String reasonCode);

    boolean existsByReasonCode(String reasonCode);

    List<DowntimeReason> findByCategory(String category);

    List<DowntimeReason> findByIsActiveTrue();
}
