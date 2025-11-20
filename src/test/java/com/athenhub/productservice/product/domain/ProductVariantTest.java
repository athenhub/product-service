package com.athenhub.productservice.product.domain;

import static com.athenhub.productservice.product.domain.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.athenhub.productservice.product.domain.dto.ProductVariantCreateCommand;
import com.athenhub.productservice.product.domain.vo.ProductColor;
import com.athenhub.productservice.product.domain.vo.ProductSize;
import org.junit.jupiter.api.Test;

class ProductVariantTest {

  @Test
  void create() {
    // given
    ProductVariantCreateCommand request = productVariantCreateCommand("RED", "M");

    // when
    ProductVariant productVariant = ProductVariant.create(request);

    // then
    assertThat(productVariant);
    assertThat(productVariant.getId()).isNotNull();
    assertThat(productVariant.getColor()).isEqualTo(ProductColor.of("RED"));
    assertThat(productVariant.getSize()).isEqualTo(ProductSize.of("M"));
  }

  @Test
  void update() {
    // given
    ProductVariant productVariant = ProductVariant.create(productVariantCreateCommand("RED", "M"));

    // when
    ProductSize changedSize = ProductSize.of("L");
    ProductColor changedColor = ProductColor.of("BLUE");
    productVariant.update(changedColor, changedSize);

    // then
    assertThat(productVariant.getColor()).isEqualTo(changedColor);
    assertThat(productVariant.getSize()).isEqualTo(changedSize);
  }

  @Test
  void isSameOption() {
    // given
    ProductVariant productVariant1 = ProductVariant.create(productVariantCreateCommand("RED", "M"));
    ProductVariant productVariant2 = ProductVariant.create(productVariantCreateCommand("RED", "M"));

    // when & then
    assertThat(productVariant1.isSameOption(productVariant2)).isTrue();
  }

  @Test
  void isDeleted() {
    // given
    ProductVariant productVariant = ProductVariant.create(productVariantCreateCommand("RED", "M"));
    productVariant.delete("test");

    // when & then
    assertThat(productVariant.isDeleted()).isTrue();
  }
}
