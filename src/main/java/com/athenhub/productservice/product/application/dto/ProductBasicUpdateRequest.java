package com.athenhub.productservice.product.application.dto;

import com.athenhub.productservice.product.domain.dto.ProductBasicUpdateCommand;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.UUID;

public record ProductBasicUpdateRequest(UUID productId, UUID hubId, UUID vendorId, long price) {
  public ProductBasicUpdateCommand toBasicUpdateCommand() {
    return new ProductBasicUpdateCommand(HubId.of(hubId), VendorId.of(vendorId), Price.of(price));
  }
}
