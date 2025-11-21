package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.PRODUCT_CREATE_PERMISSION_DENIED;

import com.athenhub.productservice.product.application.dto.ProductRegisterRequest;
import com.athenhub.productservice.product.application.dto.ProductResponse;
import com.athenhub.productservice.product.application.exception.ProductException;
import com.athenhub.productservice.product.domain.service.ProductCreatePermissionPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductApplicationService {

  private final RegisterProductService registerProductService;
  private final ProductCreatePermissionPolicy productCreatePermissionPolicy;

  public ProductResponse register(ProductRegisterRequest request, String username) {
    if (productCreatePermissionPolicy.isNotAllowed(username, request.hubId(), request.vendorId())) {
      throw new ProductException(PRODUCT_CREATE_PERMISSION_DENIED);
    }
    return registerProductService.register(request);
  }
}
