package com.athenhub.productservice.product.domain;

import com.athenhub.productservice.global.domain.AbstractTimeEntity;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.ProductId;
import com.athenhub.productservice.product.domain.vo.ProductVariantId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_stock")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockHistory extends AbstractTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded private ProductId productId;

  @Embedded private ProductVariantId variantId;

  @Embedded private HubId hubId;

  private int changedQuantity;

  @Enumerated(EnumType.STRING)
  private StockType type;

  public static StockHistory record(
      ProductId productId,
      ProductVariantId variantId,
      HubId hubId,
      int changedQuantity,
      StockType type) {
    StockHistory history = new StockHistory();
    history.productId = productId;
    history.variantId = variantId;
    history.hubId = hubId;
    history.changedQuantity = changedQuantity;
    history.type = type;
    return history;
  }
}
