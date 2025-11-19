package com.athenhub.productservice.product.domain;

import com.athenhub.productservice.product.domain.vo.ProductVariantId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_product_variant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductVariant {

  @EmbeddedId private ProductVariantId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @Enumerated(EnumType.STRING)
  private ProductColor color;

  @Enumerated(EnumType.STRING)
  private ProductSize size;

  private ProductVariant(ProductVariantId id, ProductColor color, ProductSize size) {
    this.id = id;
    this.color = color;
    this.size = size;
  }

  /** Variant 생성 (ID 자동 생성) */
  public static ProductVariant create(ProductColor color, ProductSize size) {
    return new ProductVariant(ProductVariantId.create(), color, size);
  }

  public void assignTo(Product product) {
    this.product = product;
  }

}
