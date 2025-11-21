package com.athenhub.productservice.product.application.service.policy;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProductCreatePermissionPolicy {
  public boolean isNotAllowed(String username, UUID hubId, UUID vendorId) {
    return false;
  }
}
