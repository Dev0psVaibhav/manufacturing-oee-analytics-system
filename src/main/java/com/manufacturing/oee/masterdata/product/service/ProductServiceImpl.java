package com.manufacturing.oee.masterdata.product.service;

import com.manufacturing.oee.common.exception.BusinessException;
import com.manufacturing.oee.common.exception.ResourceNotFoundException;
import com.manufacturing.oee.masterdata.product.dto.ProductCreateRequest;
import com.manufacturing.oee.masterdata.product.dto.ProductResponse;
import com.manufacturing.oee.masterdata.product.dto.ProductUpdateRequest;
import com.manufacturing.oee.masterdata.product.entity.Product;
import com.manufacturing.oee.masterdata.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link ProductService} managing finished goods SKU catalog.
 */
@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        log.info("Creating product SKU with code: {}", request.productCode());

        if (productRepository.existsByProductCode(request.productCode())) {
            throw new BusinessException("DUPLICATE_PRODUCT_CODE", "Product with code '" + request.productCode() + "' already exists");
        }

        Product product = new Product(
                request.productCode(),
                request.productName(),
                request.productCategory(),
                request.unitOfMeasure(),
                request.targetPackSize(),
                request.isActive() != null ? request.isActive() : true
        );

        Product saved = productRepository.save(product);
        log.info("Product created with ID: {}", saved.getId());
        return ProductResponse.fromEntity(saved);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", String.valueOf(id)));
        return ProductResponse.fromEntity(product);
    }

    @Override
    public ProductResponse getProductByCode(String productCode) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productCode));
        return ProductResponse.fromEntity(product);
    }

    @Override
    public List<ProductResponse> getAllProducts(Boolean activeOnly) {
        List<Product> products = Boolean.TRUE.equals(activeOnly)
                ? productRepository.findByIsActiveTrue()
                : productRepository.findAll();
        return products.stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        log.info("Updating product ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", String.valueOf(id)));

        product.setProductName(request.productName());
        product.setProductCategory(request.productCategory());
        if (request.unitOfMeasure() != null) {
            product.setUnitOfMeasure(request.unitOfMeasure());
        }
        if (request.targetPackSize() != null) {
            product.setTargetPackSize(request.targetPackSize());
        }
        if (request.isActive() != null) {
            product.setIsActive(request.isActive());
        }

        Product updated = productRepository.save(product);
        return ProductResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deactivateProduct(Long id) {
        log.info("Deactivating product ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", String.valueOf(id)));

        product.setIsActive(false);
        productRepository.save(product);
        log.info("Product ID: {} deactivated successfully", id);
    }
}
