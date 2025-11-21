package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.PRODUCT_NOT_FOUND;

import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.repository.ProductRepository;
import com.athenhub.productservice.product.domain.vo.ProductId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService {

  private final ProductRepository productRepository;

  public Product getProduct(UUID productId) {
    return productRepository
        .findById(ProductId.of(productId))
        .orElseThrow(() -> new ProductServiceException(PRODUCT_NOT_FOUND));
  }
}
