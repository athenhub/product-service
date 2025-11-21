package com.athenhub.productservice.product.application.service.policy;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductUpdatePermissionPolicy {

  public boolean isNotAllowed(String username, UUID hubId, UUID vendorId) {
    return false;
  }
}
