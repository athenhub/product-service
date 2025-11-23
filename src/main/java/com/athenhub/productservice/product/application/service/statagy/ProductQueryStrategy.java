package com.athenhub.productservice.product.application.service.statagy;

import com.athenhub.productservice.membership.domain.MemberRoles;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.MemberInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryStrategy {

  boolean supports(MemberRoles roles);

  Page<Product> query(MemberInfo member, Pageable pageable);
}
