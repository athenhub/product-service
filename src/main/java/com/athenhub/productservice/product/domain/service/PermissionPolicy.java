package com.athenhub.productservice.product.domain.service;

import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.UUID;

public interface PermissionPolicy {

  boolean isCreateAllowed(UUID requestId, HubId hubId, VendorId vendorId);

  boolean isUpdateAllowed(UUID requestId, HubId hubId, VendorId vendorId);

  boolean isDeleteAllowed(UUID requestId, HubId hubId);
}
