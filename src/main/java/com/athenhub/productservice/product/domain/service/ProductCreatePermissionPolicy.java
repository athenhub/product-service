package com.athenhub.productservice.product.domain.service;

import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.VendorId;

public interface ProductCreatePermissionPolicy {
  boolean isNotAllowed(HubId hubId, VendorId vendorId);
}
