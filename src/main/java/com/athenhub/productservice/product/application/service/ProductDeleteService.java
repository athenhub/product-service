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
@Transactional
@RequiredArgsConstructor
public class ProductDeleteService {

  private final ProductRepository productRepository;

  public void delete(UUID productId, String username) {
    Product product =
        productRepository
            .findById(ProductId.of(productId))
            .orElseThrow(() -> new ProductServiceException(PRODUCT_NOT_FOUND));

    product.delete(username);
  }
}
