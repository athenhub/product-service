package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.*;

import com.athenhub.productservice.product.application.dto.ProductBasicUpdateRequest;
import com.athenhub.productservice.product.application.dto.ProductRegisterRequest;
import com.athenhub.productservice.product.application.dto.ProductResponse;
import com.athenhub.productservice.product.application.dto.ProductVariantUpdateRequest;
import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.application.service.policy.ProductCreatePermissionPolicy;
import com.athenhub.productservice.product.application.service.policy.ProductUpdatePermissionPolicy;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.repository.ProductRepository;
import com.athenhub.productservice.product.domain.vo.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductApplicationService {

  private final RegisterProductService registerProductService;
  private final UpdateProductService updateProductService;

  private final ProductCreatePermissionPolicy productCreatePermissionPolicy;
  private final ProductUpdatePermissionPolicy productUpdatePermissionPolicy;
  private final ProductRepository productRepository;

  public ProductResponse register(ProductRegisterRequest request, String username) {
    if (productCreatePermissionPolicy.isNotAllowed(username, request.hubId(), request.vendorId())) {
      throw new ProductServiceException(PRODUCT_CREATE_PERMISSION_DENIED);
    }
    return registerProductService.register(request);
  }

  public ProductResponse updateBasicInfo(ProductBasicUpdateRequest request, String username) {
    Product product =
        productRepository
            .findById(ProductId.of(request.productId()))
            .orElseThrow(() -> new ProductServiceException(PRODUCT_NOT_FOUND));

    if (productUpdatePermissionPolicy.isNotAllowed(
        username, product.getHubId().toUuid(), product.getVendorId().toUuid())) {
      throw new ProductServiceException(PRODUCT_UPDATE_PERMISSION_DENIED);
    }
    return updateProductService.updateBasicInfo(request);
  }

  public ProductResponse updateVariants(ProductVariantUpdateRequest request, String username) {
    Product product =
        productRepository
            .findById(ProductId.of(request.productId()))
            .orElseThrow(() -> new ProductServiceException(PRODUCT_NOT_FOUND));

    if (productUpdatePermissionPolicy.isNotAllowed(
        username, product.getHubId().toUuid(), product.getVendorId().toUuid())) {
      throw new ProductServiceException(PRODUCT_UPDATE_PERMISSION_DENIED);
    }
    return updateProductService.updateProductVariant(request, username);
  }
}
