package com.athenhub.productservice.product.domain;

import static com.athenhub.productservice.product.domain.ProductFixture.productCreateCommand;
import static com.athenhub.productservice.product.domain.ProductFixture.productVariantCreateCommand;
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
    ProductCreateCommand productCommand = productCreateCommand(OPTION);
    ProductVariantCreateCommand productVariantRequest1 = productVariantCreateCommand("RED", "M");
    ProductVariantCreateCommand productVariantRequest2 = productVariantCreateCommand("BLUE", "L");
    ProductVariant productVariant1 = ProductVariant.create(productVariantRequest1);
    ProductVariant productVariant2 = ProductVariant.create(productVariantRequest2);

    // when
    Product product = Product.create(productCommand);
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
    ProductCreateCommand productCreateRequest = productCreateCommand(SIMPLE);
    Product product = Product.create(productCreateRequest);

    // when
    HubId changedHubId = HubId.of(UUID.randomUUID());
    VendorId changedVendorID = VendorId.of(UUID.randomUUID());
    Price changedPrice = Price.of(1000);
    product.updateBasic(new ProductBasicUpdateCommand(changedHubId, changedVendorID, changedPrice));

    // then
    assertThat(product);
    assertThat(product.getHubId()).isEqualTo(changedHubId);
    assertThat(product.getVendorId()).isEqualTo(changedVendorID);
    assertThat(product.getPrice()).isEqualTo(changedPrice);
  }

  @Test
  void changeType() {
    // given
    ProductCreateCommand productCreateRequest = productCreateCommand(SIMPLE);
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
    ProductCreateCommand productCreateCommand = productCreateCommand(SIMPLE);
    Product product = Product.create(productCreateCommand);

    // when
    ProductStatus changedStatus = ProductStatus.HIDDEN;
    product.changeStatus(changedStatus);

    // then
    assertThat(product.getStatus()).isEqualTo(changedStatus);
  }

  @Test
  void addVariant() {
    // given
    Product product = Product.create(productCreateCommand(OPTION));
    ProductVariant productVariant1 = ProductVariant.create(productVariantCreateCommand("RED", "M"));
    ProductVariant productVariant2 =
        ProductVariant.create(productVariantCreateCommand("BLUE", "L"));

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
    Product product = Product.create(productCreateCommand(SIMPLE));
    ProductVariant productVariant = ProductVariant.create(productVariantCreateCommand("RED", "M"));

    // when & then
    assertThatThrownBy(() -> product.addVariant(productVariant))
        .isInstanceOf(ProductVariantNotSupportedException.class);
  }

  @Test
  void addVariant_withDuplicatedVariant() {
    // given
    Product product =
        ProductFixture.create(
            productCreateCommand(OPTION), productVariantCreateCommand("RED", "M"));

    // when & then
    assertThatThrownBy(
            () -> {
              ProductVariant productVariant2 =
                  ProductVariant.create(productVariantCreateCommand("RED", "M"));
              product.addVariant(productVariant2);
            })
        .isInstanceOf(VariantAlreadyExistsException.class);
  }

  @Test
  void updateVariant_withSimpleType() {
    // given
    Product product = Product.create(productCreateCommand(SIMPLE));

    // when & then
    assertThatThrownBy(
            () -> {
              ProductVariantUpdateCommand productVariantUpdateCommand =
                  new ProductVariantUpdateCommand(
                      ProductVariantId.of(UUID.randomUUID()),
                      ProductColor.of("RED"),
                      ProductSize.of("M"));
              product.updateVariant(productVariantUpdateCommand);
            })
        .isInstanceOf(ProductVariantNotSupportedException.class);
  }

  @Test
  void updateVariant_withNotFoundVariantId() {
    // given
    Product product =
        ProductFixture.create(
            productCreateCommand(OPTION), productVariantCreateCommand("RED", "M"));

    // when & then
    assertThatThrownBy(
            () -> {
              ProductVariantUpdateCommand productVariantUpdateRequest =
                  new ProductVariantUpdateCommand(
                      ProductVariantId.of(UUID.randomUUID()),
                      ProductColor.of("RED"),
                      ProductSize.of("M"));
              product.updateVariant(productVariantUpdateRequest);
            })
        .isInstanceOf(VariantNotFoundException.class);
  }

  @Test
  void removeVariant_withSimpleType() {
    // given
    Product product = Product.create(productCreateCommand(SIMPLE));

    // when & then
    assertThatThrownBy(
            () ->
                product.removeVariant(
                    new ProductVariantRemoveCommand(
                        ProductVariantId.of(UUID.randomUUID()), "test")))
        .isInstanceOf(ProductVariantNotSupportedException.class);
  }

  @Test
  void removeVariant_withNotFoundVariantId() {
    // given
    Product product =
        ProductFixture.create(
            productCreateCommand(OPTION), productVariantCreateCommand("RED", "M"));

    // when & then
    assertThatThrownBy(
            () ->
                product.removeVariant(
                    new ProductVariantRemoveCommand(
                        ProductVariantId.of(UUID.randomUUID()), "test")))
        .isInstanceOf(VariantNotFoundException.class);
  }

  @Test
  void getVariants_ByActive_activeOnlyTrue() {
    // given
    ProductCreateCommand productCommand = productCreateCommand(OPTION);
    ProductVariant productVariant1 = ProductVariant.create(productVariantCreateCommand("RED", "M"));
    ProductVariant productVariant2 =
        ProductVariant.create(productVariantCreateCommand("BLUE", "L"));
    ProductVariant productVariant3 =
        ProductVariant.create(productVariantCreateCommand("GREEN", "L"));

    Product product = Product.create(productCommand);
    product.addVariant(productVariant1);
    product.addVariant(productVariant2);
    product.addVariant(productVariant3);

    ProductVariantId deletedVariantId = productVariant3.getId();
    product.removeVariant(new ProductVariantRemoveCommand(deletedVariantId, "tset"));

    // when
    List<ProductVariant> variants = product.getVariantsByActive(true);

    assertThat(variants)
        .hasSize(2)
        .extracting(ProductVariant::getId)
        .doesNotContain(deletedVariantId);
  }

  @Test
  void getVariants_ByActive_activeOnlyFalse() {
    // given
    ProductCreateCommand productCommand = productCreateCommand(OPTION);
    ProductVariant productVariant1 = ProductVariant.create(productVariantCreateCommand("RED", "M"));
    ProductVariant productVariant2 =
        ProductVariant.create(productVariantCreateCommand("BLUE", "L"));
    ProductVariant productVariant3 =
        ProductVariant.create(productVariantCreateCommand("GREEN", "L"));

    Product product = Product.create(productCommand);
    product.addVariant(productVariant1);
    product.addVariant(productVariant2);
    product.addVariant(productVariant3);

    ProductVariantId deletedVariantId = productVariant3.getId();
    product.removeVariant(new ProductVariantRemoveCommand(deletedVariantId, "tset"));

    // when
    List<ProductVariant> variants = product.getVariantsByActive(false);

    assertThat(variants).hasSize(3).extracting(v -> v.getId()).contains(deletedVariantId);
  }
}
