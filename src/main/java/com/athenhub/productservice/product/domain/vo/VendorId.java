package com.athenhub.productservice.product.domain.vo;

import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VendorId {

  private UUID id;

  private VendorId(UUID id) {
    this.id = id;
  }

  public static VendorId of(UUID uuid) {
    return new VendorId(uuid);
  }
}
