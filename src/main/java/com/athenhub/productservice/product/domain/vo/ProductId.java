package com.athenhub.productservice.product.domain.vo;

import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductId {

  private UUID id;

  private ProductId(UUID id) {
    this.id = id;
  }

  public static ProductId of(UUID uuid) {
    return new ProductId(uuid);
  }

  public static ProductId create() {
    return new ProductId(UUID.randomUUID());
  }
}
