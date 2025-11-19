package com.athenhub.productservice.product.domain.vo;

import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductVariantId {

  private UUID id;

  private ProductVariantId(UUID id) {
    this.id = id;
  }

  public static ProductVariantId of(UUID uuid) {
    return new ProductVariantId(uuid);
  }

  public static ProductVariantId create() {
    return new ProductVariantId(UUID.randomUUID());
  }
}
