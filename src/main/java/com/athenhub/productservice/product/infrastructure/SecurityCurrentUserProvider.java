package com.athenhub.productservice.product.infrastructure;

import com.athenhub.productservice.product.application.service.CurrentUserProvider;
import com.athenhub.productservice.product.application.service.dto.RequestMember;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityCurrentUserProvider implements CurrentUserProvider {

  @Override
  public RequestMember getMember() {
    String username = "SYSTEM";
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.getPrincipal() != null) {
      UserDetails details = (UserDetails) authentication.getPrincipal();
      username = details.getUsername();
    }

    return new RequestMember(username);
  }
}
