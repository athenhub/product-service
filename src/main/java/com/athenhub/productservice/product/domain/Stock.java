package com.athenhub.productservice.product.domain;

import com.athenhub.productservice.product.domain.vo.ProductVariantId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_stock")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

  @EmbeddedId private ProductVariantId id;

  @OneToOne
  @MapsId // Stock.id = variant.id 연결
  @JoinColumn(name = "variant_id")
  private ProductVariant variant;

  private int quantity;

  private Stock(ProductVariant variant, int quantity) {
    this.variant = variant;
    this.id = variant.getId();
    this.quantity = quantity;
    variant.assignStock(this);
  }

  public static Stock create(ProductVariant variant, int quantity) {
    return new Stock(variant, quantity);
  }

  /** 정적 팩토리 메서드 Variant를 넣어야 정상적으로 PK 공유가 이루어짐 */
  public static Stock of(ProductVariant variant, int quantity) {
    return new Stock(variant, quantity);
  }

  public void increase(int amount) {
    this.quantity += amount;
  }

  public void decrease(int amount) {
    this.quantity -= amount;
  }
}
