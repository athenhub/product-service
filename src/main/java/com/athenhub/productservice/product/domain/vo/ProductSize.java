package com.athenhub.productservice.product.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ProductSize {

  @Column(name = "color")
  private String value;

  public static ProductSize of(String name) {
    return new ProductSize(name);
  }
}
