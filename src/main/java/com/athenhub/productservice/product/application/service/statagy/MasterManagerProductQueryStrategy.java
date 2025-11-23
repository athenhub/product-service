package com.athenhub.productservice.product.application.service.statagy;

import com.athenhub.productservice.membership.domain.MemberRoles;
import com.athenhub.productservice.product.application.service.ProductQueryService;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MasterManagerProductQueryStrategy implements ProductQueryStrategy {

  private final ProductQueryService productQueryService;

  @Override
  public Page<Product> query(MemberInfo member, Pageable pageable) {
    return productQueryService.getAll(pageable);
  }

  @Override
  public boolean supports(MemberRoles roles) {
    return roles.containsMasterManager();
  }
}
