package com.athenhub.productservice.product.application.service;

import com.athenhub.productservice.product.application.service.dto.RequestMember;

public interface CurrentUserProvider {

  RequestMember getMember();
}
