package com.manufacturing.oee.masterdata.product.repository;

import com.manufacturing.oee.masterdata.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Product} entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductCode(String productCode);

    boolean existsByProductCode(String productCode);

    List<Product> findByIsActiveTrue();

    List<Product> findByProductCategory(String productCategory);
}
