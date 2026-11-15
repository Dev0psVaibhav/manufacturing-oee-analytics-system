package com.manufacturing.oee.masterdata.product.service;

import com.manufacturing.oee.masterdata.product.dto.ProductCreateRequest;
import com.manufacturing.oee.masterdata.product.dto.ProductResponse;
import com.manufacturing.oee.masterdata.product.dto.ProductUpdateRequest;

import java.util.List;

/**
 * Service interface for Product SKU master data operations.
 */
public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request);

    ProductResponse getProductById(Long id);

    ProductResponse getProductByCode(String productCode);

    List<ProductResponse> getAllProducts(Boolean activeOnly);

    ProductResponse updateProduct(Long id, ProductUpdateRequest request);

    void deactivateProduct(Long id);
}
