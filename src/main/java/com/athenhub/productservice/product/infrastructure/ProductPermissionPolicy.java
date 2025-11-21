package com.athenhub.productservice.product.infrastructure;

import com.athenhub.productservice.product.domain.service.PermissionPolicy;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProductPermissionPolicy implements PermissionPolicy {
  @Override
  public boolean isCreateDenied(UUID requestId, HubId hubId, VendorId vendorId) {
    return false;
  }

  @Override
  public boolean isUpdateDenied(UUID requestId, HubId hubId, VendorId vendorId) {
    return false;
  }

  @Override
  public boolean isDeleteDenied(UUID requestId, HubId hubId) {
    return false;
  }
}
