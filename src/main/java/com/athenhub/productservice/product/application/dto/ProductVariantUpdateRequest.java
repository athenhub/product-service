package com.athenhub.productservice.product.application.dto;

import java.util.List;
import java.util.UUID;

public record ProductVariantUpdateRequest(
    UUID productId, List<add> adds, List<update> updates, List<remove> removes) {
  public record add(String color, String size) {}

  public record update(UUID variantId, String color, String size) {}

  public record remove(UUID variantId) {}
}
