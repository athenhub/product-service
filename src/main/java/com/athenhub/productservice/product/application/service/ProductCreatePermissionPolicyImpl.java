package com.athenhub.productservice.product.application.service;

import com.athenhub.productservice.product.domain.service.ProductCreatePermissionPolicy;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCreatePermissionPolicyImpl implements ProductCreatePermissionPolicy {

  private final CurrentUserProvider currentUserProvider;

  @Override
  public boolean isNotAllowed(HubId hubId, VendorId vendorId) {
    // TODO 현재는 모두 통과
    // 업체 담당자 -> 본인 업체
    // 허브 관리자 -> 본인 허브
    // 마스터는 모두 허용
    return false;
  }
}
