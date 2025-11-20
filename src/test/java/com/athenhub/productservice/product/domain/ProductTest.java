package com.athenhub.productservice.product.domain;

import static com.athenhub.productservice.product.domain.ProductFixture.productCreateRequest;
import static com.athenhub.productservice.product.domain.ProductFixture.productVariantCreateRequest;
import static com.athenhub.productservice.product.domain.ProductType.OPTION;
import static com.athenhub.productservice.product.domain.ProductType.SIMPLE;
import static org.assertj.core.api.Assertions.*;

import com.athenhub.productservice.product.domain.dto.*;
import com.athenhub.productservice.product.domain.exception.ProductVariantNotSupportedException;
import com.athenhub.productservice.product.domain.exception.VariantAlreadyExistsException;
import com.athenhub.productservice.product.domain.exception.VariantNotFoundException;
import com.athenhub.productservice.product.domain.vo.*;
import java.util.List;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

class ProductTest {

  @Test
  void create() {
    // given
    ProductCreateRequest productRequest = productCreateRequest(OPTION);
    ProductVariantCreateRequest productVariantRequest1 = productVariantCreateRequest("RED", "M");
    ProductVariantCreateRequest productVariantRequest2 = productVariantCreateRequest("BLUE", "L");
    ProductVariant productVariant1 = ProductVariant.create(productVariantRequest1);
    ProductVariant productVariant2 = ProductVariant.create(productVariantRequest2);

    // when
    Product product = Product.create(productRequest);
    product.addVariant(productVariant1);
    product.addVariant(productVariant2);

    // then
    assertThat(product);
    assertThat(product.getType()).isEqualTo(OPTION);
    assertThat(product.getId()).isNotNull();
    assertThat(product.getHubId()).isNotNull();
    assertThat(product.getVendorId()).isNotNull();
    assertThat(product.getPrice()).isNotNull();
    assertThat(product.getStatus()).isEqualTo(ProductStatus.DRAFT);
    assertThat(product.getVariants()).hasSize(2);
    assertThat(product.getVariants())
        .extracting("color.value", "size.value")
        .containsExactlyInAnyOrder(tuple("RED", "M"), Tuple.tuple("BLUE", "L"));
  }

  @Test
  void updateBasic() {
    // given
    ProductCreateRequest productCreateRequest = productCreateRequest(SIMPLE);
    Product product = Product.create(productCreateRequest);

    // when
    UUID changedHubId = UUID.randomUUID();
    UUID changedVendorID = UUID.randomUUID();
    long changedPrice = 100;
    product.updateBasic(new ProductBasicUpdateRequest(changedHubId, changedVendorID, changedPrice));

    // then
    assertThat(product);
    assertThat(product.getHubId()).isEqualTo(HubId.of(changedHubId));
    assertThat(product.getVendorId()).isEqualTo(VendorId.of(changedVendorID));
    assertThat(product.getPrice()).isEqualTo(Price.of(changedPrice));
  }

  @Test
  void changeType() {
    // given
    ProductCreateRequest productCreateRequest = productCreateRequest(SIMPLE);
    Product product = Product.create(productCreateRequest);

    // when
    ProductType changedType = OPTION;
    product.changeType(changedType);

    // then
    assertThat(product.getType()).isEqualTo(changedType);
  }

  @Test
  void changeStatus() {
    // given
    ProductCreateRequest productCreateRequest = productCreateRequest(SIMPLE);
    Product product = Product.create(productCreateRequest);

    // when
    ProductStatus changedStatus = ProductStatus.HIDDEN;
    product.changeStatus(changedStatus);

    // then
    assertThat(product.getStatus()).isEqualTo(changedStatus);
  }

  @Test
  void addVariant() {
    // given
    Product product = Product.create(productCreateRequest(OPTION));
    ProductVariant productVariant1 = ProductVariant.create(productVariantCreateRequest("RED", "M"));
    ProductVariant productVariant2 =
        ProductVariant.create(productVariantCreateRequest("BLUE", "L"));

    // when
    product.addVariant(productVariant1);
    product.addVariant(productVariant2);

    // then
    assertThat(product.getVariants())
        .hasSize(2)
        .extracting("color.value", "size.value")
        .containsExactlyInAnyOrder(tuple("RED", "M"), Tuple.tuple("BLUE", "L"));
  }

  @Test
  void addVariant_withSimpleType() {
    // given
    Product product = Product.create(productCreateRequest(SIMPLE));
    ProductVariant productVariant = ProductVariant.create(productVariantCreateRequest("RED", "M"));

    // when & then
    assertThatThrownBy(() -> product.addVariant(productVariant))
        .isInstanceOf(ProductVariantNotSupportedException.class);
  }

  @Test
  void addVariant_withDuplicatedVariant() {
    // given
    Product product =
        ProductFixture.create(
            productCreateRequest(OPTION), productVariantCreateRequest("RED", "M"));

    // when & then
    assertThatThrownBy(
            () -> {
              ProductVariant productVariant2 =
                  ProductVariant.create(productVariantCreateRequest("RED", "M"));
              product.addVariant(productVariant2);
            })
        .isInstanceOf(VariantAlreadyExistsException.class);
  }

  @Test
  void updateVariant_withSimpleType() {
    // given
    Product product = Product.create(productCreateRequest(SIMPLE));

    // when & then
    assertThatThrownBy(
            () -> {
              ProductVariantUpdateRequest productVariantUpdateRequest =
                  new ProductVariantUpdateRequest(
                      UUID.randomUUID(), ProductColor.of("RED"), ProductSize.of("M"));
              product.updateVariant(productVariantUpdateRequest);
            })
        .isInstanceOf(ProductVariantNotSupportedException.class);
  }

  @Test
  void updateVariant_withNotFoundVariantId() {
    // given
    Product product =
        ProductFixture.create(
            productCreateRequest(OPTION), productVariantCreateRequest("RED", "M"));

    // when & then
    assertThatThrownBy(
            () -> {
              ProductVariantUpdateRequest productVariantUpdateRequest =
                  new ProductVariantUpdateRequest(
                      UUID.randomUUID(), ProductColor.of("RED"), ProductSize.of("M"));
              product.updateVariant(productVariantUpdateRequest);
            })
        .isInstanceOf(VariantNotFoundException.class);
  }

  @Test
  void removeVariant_withSimpleType() {
    // given
    Product product = Product.create(productCreateRequest(SIMPLE));

    // when & then
    assertThatThrownBy(
            () -> product.removeVariant(new ProductVariantRemoveRequest(UUID.randomUUID(), "test")))
        .isInstanceOf(ProductVariantNotSupportedException.class);
  }

  @Test
  void removeVariant_withNotFoundVariantId() {
    // given
    Product product =
        ProductFixture.create(
            productCreateRequest(OPTION), productVariantCreateRequest("RED", "M"));

    // when & then
    assertThatThrownBy(
            () -> product.removeVariant(new ProductVariantRemoveRequest(UUID.randomUUID(), "test")))
        .isInstanceOf(VariantNotFoundException.class);
  }

  @Test
  void getVariants_ByActive_activeOnlyTrue() {
    // given
    ProductCreateRequest productRequest = productCreateRequest(OPTION);
    ProductVariant productVariant1 = ProductVariant.create(productVariantCreateRequest("RED", "M"));
    ProductVariant productVariant2 =
        ProductVariant.create(productVariantCreateRequest("BLUE", "L"));
    ProductVariant productVariant3 =
        ProductVariant.create(productVariantCreateRequest("GREEN", "L"));
    Product product = Product.create(productRequest);
    product.addVariant(productVariant1);
    product.addVariant(productVariant2);
    product.addVariant(productVariant3);

    UUID deletedVariantId = productVariant3.getId().toUuid();
    product.removeVariant(new ProductVariantRemoveRequest(deletedVariantId, "tset"));

    // when
    List<ProductVariant> variants = product.getVariantsByActive(true);

    assertThat(variants)
        .hasSize(2)
        .extracting(v -> v.getId().toUuid())
        .doesNotContain(deletedVariantId);
  }

  @Test
  void getVariants_ByActive_activeOnlyFalse() {
    // given
    ProductCreateRequest productRequest = productCreateRequest(OPTION);
    ProductVariant productVariant1 = ProductVariant.create(productVariantCreateRequest("RED", "M"));
    ProductVariant productVariant2 =
        ProductVariant.create(productVariantCreateRequest("BLUE", "L"));
    ProductVariant productVariant3 =
        ProductVariant.create(productVariantCreateRequest("GREEN", "L"));
    Product product = Product.create(productRequest);
    product.addVariant(productVariant1);
    product.addVariant(productVariant2);
    product.addVariant(productVariant3);

    UUID deletedVariantId = productVariant3.getId().toUuid();
    product.removeVariant(new ProductVariantRemoveRequest(deletedVariantId, "tset"));

    // when
    List<ProductVariant> variants = product.getVariantsByActive(false);

    assertThat(variants).hasSize(3).extracting(v -> v.getId().toUuid()).contains(deletedVariantId);
  }
}
