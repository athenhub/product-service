package com.athenhub.productservice.product.infrastructure;

import com.athenhub.productservice.product.domain.service.PermissionPolicy;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProductPermissionPolicy implements PermissionPolicy {
  @Override
  public boolean isCreateAllowed(UUID requestId, HubId hubId, VendorId vendorId) {
    return true;
  }

  @Override
  public boolean isUpdateAllowed(UUID requestId, HubId hubId, VendorId vendorId) {
    return true;
  }

  @Override
  public boolean isDeleteAllowed(UUID requestId, HubId hubId) {
    return true;
  }
}
