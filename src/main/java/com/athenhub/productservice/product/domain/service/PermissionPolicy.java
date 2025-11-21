package com.athenhub.productservice.product.domain.service;

import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.UUID;

public interface PermissionPolicy {

  boolean isCreateDenied(UUID requestId, HubId hubId, VendorId vendorId);

  boolean isUpdateDenied(UUID requestId, HubId hubId, VendorId vendorId);

  boolean isDeleteDenied(UUID requestId, HubId hubId);
}
