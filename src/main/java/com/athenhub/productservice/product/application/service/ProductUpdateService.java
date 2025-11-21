package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.PRODUCT_NOT_FOUND;

import com.athenhub.productservice.product.application.dto.ProductBasicUpdateRequest;
import com.athenhub.productservice.product.application.dto.ProductResponse;
import com.athenhub.productservice.product.application.dto.ProductVariantUpdateRequest;
import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.application.mapper.VariantUpdateCommandMapper;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.ProductBasicUpdateCommand;
import com.athenhub.productservice.product.domain.dto.VariantUpdateSet;
import com.athenhub.productservice.product.domain.repository.ProductRepository;
import com.athenhub.productservice.product.domain.vo.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductUpdateService {

  private final ProductRepository productRepository;

  private final VariantUpdateCommandMapper variantCommandMapper;

  public ProductResponse updateBasicInfo(ProductBasicUpdateRequest request) {
    Product product =
        productRepository
            .findById(ProductId.of(request.productId()))
            .orElseThrow(() -> new ProductServiceException(PRODUCT_NOT_FOUND));

    ProductBasicUpdateCommand basicUpdateCommand = request.toBasicUpdateCommand();
    product.updateBasic(basicUpdateCommand);
    return new ProductResponse(request.productId());
  }

  public ProductResponse updateProductVariant(
      ProductVariantUpdateRequest request, String username) {
    Product product =
        productRepository
            .findById(ProductId.of(request.productId()))
            .orElseThrow(() -> new ProductServiceException(PRODUCT_NOT_FOUND));

    VariantUpdateSet updateSet = variantCommandMapper.toChangeSet(request, username);
    product.applyVariantUpdateSet(updateSet);

    return new ProductResponse(request.productId());
  }
}
