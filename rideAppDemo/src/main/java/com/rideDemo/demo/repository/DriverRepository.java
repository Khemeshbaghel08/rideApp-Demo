package com.rideDemo.demo.repository;

import com.rideDemo.demo.entity.Driver;
import com.rideDemo.demo.enums.DriverStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {
    List<Driver> findByStatus(DriverStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           SELECT d FROM Driver d
           WHERE d.status = 'AVAILABLE'
           ORDER BY d.createdAt ASC
           """)
    List<Driver> findAvailableDriversForUpdate(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Driver d WHERE d.id = :id")
    Optional<Driver> findByIdForUpdate(@Param("id") String id);
}
