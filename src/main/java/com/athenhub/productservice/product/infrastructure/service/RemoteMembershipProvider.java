package com.athenhub.productservice.product.infrastructure.service;

import com.athenhub.productservice.product.domain.dto.MemberInfo;
import com.athenhub.productservice.product.domain.service.MembershipProvider;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RemoteMembershipProvider implements MembershipProvider {
  @Override
  public MemberInfo getMember(UUID userId) {
    return new MemberInfo(HubId.of(UUID.randomUUID()), VendorId.of(UUID.randomUUID()));
  }
}
