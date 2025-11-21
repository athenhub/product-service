package com.athenhub.productservice.product.application.dto;

import com.athenhub.productservice.product.domain.ProductType;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record ProductRegisterRequest(
    @NotNull UUID hubId,
    @NotNull UUID vendorId,
    long price,
    @NotNull ProductType type,
    List<RegisterProductVariant> productVariants) {

  public record RegisterProductVariant(String color, String size) {}
}
