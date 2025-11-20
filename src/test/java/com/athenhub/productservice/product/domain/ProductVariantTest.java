package com.athenhub.productservice.product.domain;

import static com.athenhub.productservice.product.domain.ProductColor.BLUE;
import static com.athenhub.productservice.product.domain.ProductColor.RED;
import static com.athenhub.productservice.product.domain.ProductSize.L;
import static com.athenhub.productservice.product.domain.ProductSize.M;
import static org.assertj.core.api.Assertions.assertThat;

import com.athenhub.productservice.product.domain.dto.ProductVariantCreateRequest;
import org.junit.jupiter.api.Test;

class ProductVariantTest {

  @Test
  void create() {
    // given
    ProductVariantCreateRequest request = new ProductVariantCreateRequest(RED, M);

    // when
    ProductVariant productVariant = ProductVariant.create(request);

    // then
    assertThat(productVariant);
    assertThat(productVariant.getId()).isNotNull();
    assertThat(productVariant.getColor()).isEqualTo(RED);
    assertThat(productVariant.getSize()).isEqualTo(M);
  }

  @Test
  void update() {
    // given
    ProductVariant productVariant = ProductVariant.create(new ProductVariantCreateRequest(RED, M));

    // when
    ProductSize changedSize = L;
    ProductColor changedColor = BLUE;
    productVariant.update(changedColor, changedSize);

    // then
    assertThat(productVariant.getColor()).isEqualTo(changedColor);
    assertThat(productVariant.getSize()).isEqualTo(changedSize);
  }

  @Test
  void isSameOption() {
    // given
    ProductVariant productVariant1 = ProductVariant.create(new ProductVariantCreateRequest(RED, M));
    ProductVariant productVariant2 = ProductVariant.create(new ProductVariantCreateRequest(RED, M));

    // when & then
    assertThat(productVariant1.isSameOption(productVariant2)).isTrue();
  }

  @Test
  void isDeleted() {
    // given
    ProductVariant productVariant = ProductVariant.create(new ProductVariantCreateRequest(RED, M));
    productVariant.delete("test");

    // when & then
    assertThat(productVariant.isDeleted()).isTrue();
  }
}
