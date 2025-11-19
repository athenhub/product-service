package com.athenhub.productservice.product.domain.vo;

import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubId {

  private UUID id;

  private HubId(UUID id) {
    this.id = id;
  }

  public static HubId of(UUID uuid) {
    return new HubId(uuid);
  }
}
