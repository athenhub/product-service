package com.athenhub.productservice.product.domain;

import static com.athenhub.productservice.product.domain.ProductFixture.*;
import static com.athenhub.productservice.product.domain.ProductType.OPTION;
import static com.athenhub.productservice.product.domain.ProductType.SIMPLE;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
  void create_success() {
    // given
    ProductCreateCommand productCommand =
        newProductCreateCommand("test", "test-description", 1000, SIMPLE);

    // when
    Product product = Product.create(productCommand);

    // then
    assertThat(product.getName()).isEqualTo("test");
    assertThat(product.getDescription()).isEqualTo("test-description");
    assertThat(product.getPrice()).isEqualTo(Price.of(1000));
    assertThat(product.getType()).isEqualTo(SIMPLE);
    assertThat(product.getId()).isNotNull();
    assertThat(product.getHubId()).isNotNull();
    assertThat(product.getVendorId()).isNotNull();
    assertThat(product.getPrice()).isNotNull();
    assertThat(product.getStatus()).isEqualTo(ProductStatus.DRAFT);
  }

  @Test
  void updateBasic_success() {
    // given
    Product product = createSimpleProduct();

    // when
    String changedName = "test-name";
    String changedDescription = "test-description";
    Price changedPrice = Price.of(1000);
    product.updateBasic(
        new ProductBasicUpdateCommand(changedName, changedDescription, changedPrice));

    // then
    assertThat(product);
    assertThat(product.getName()).isEqualTo(changedName);
    assertThat(product.getDescription()).isEqualTo(changedDescription);
    assertThat(product.getPrice()).isEqualTo(changedPrice);
  }

  @Test
  void changeType_success() {
    // given
    Product product = createSimpleProduct();

    // when
    ProductType changedType = OPTION;
    product.changeType(changedType);

    // then
    assertThat(product.getType()).isEqualTo(changedType);
  }

  @Test
  void changeStatus_success() {
    // given
    Product product = createSimpleProduct();

    // when
    ProductStatus changedStatus = ProductStatus.HIDDEN;
    product.changeStatus(changedStatus);

    // then
    assertThat(product.getStatus()).isEqualTo(changedStatus);
  }

  @Test
  void addVariant_success() {
    // given
    Product product = createOptionProduct();
    ProductVariantCreateCommand variantCreateCommand1 = newProductVariantCreateCommand("RED", "M");
    ProductVariantCreateCommand variantCreateCommand2 = newProductVariantCreateCommand("BLUE", "L");

    // when
    product.addVariant(variantCreateCommand1);
    product.addVariant(variantCreateCommand2);

    // then
    assertThat(product.getVariants())
        .hasSize(2)
        .extracting("color.value", "size.value")
        .containsExactlyInAnyOrder(tuple("RED", "M"), Tuple.tuple("BLUE", "L"));
  }

  @Test
  void addVariant_simpleProductType_fail() {
    // given
    Product product = createSimpleProduct();
    ProductVariantCreateCommand variantCreateCommand = newProductVariantCreateCommand("RED", "M");

    // when & then
    assertThatThrownBy(() -> product.addVariant(variantCreateCommand))
        .isInstanceOf(ProductVariantNotSupportedException.class);
  }

  @Test
  void addVariant_duplicate_fail() {
    // given
    Product product = createOptionProduct();
    ProductVariantCreateCommand variantCreateCommand1 = newProductVariantCreateCommand("RED", "M");
    product.addVariant(variantCreateCommand1);

    // when & then
    ProductVariantCreateCommand variantCreateCommand2 = newProductVariantCreateCommand("RED", "M");

    assertThatThrownBy(() -> product.addVariant(variantCreateCommand2))
        .isInstanceOf(VariantAlreadyExistsException.class);
  }

  @Test
  void updateVariant_success() {
    // given
    Product product = createOptionProduct();
    ProductVariantCreateCommand variantCreateCommand = newProductVariantCreateCommand("RED", "M");
    ProductVariantId targetProductVariantId = product.addVariant(variantCreateCommand);

    // when & then
    ProductVariantUpdateCommand productVariantUpdateCommand =
        new ProductVariantUpdateCommand(
            targetProductVariantId, ProductColor.of("BLUE"), ProductSize.of("S"));

    product.updateVariant(productVariantUpdateCommand);

    // then
    assertThat(product.getVariants())
        .extracting("color.value", "size.value")
        .contains(tuple("BLUE", "S"))
        .doesNotContain(tuple("RED", "L"));
  }

  @Test
  void updateVariant_withSimpleType_success() {
    // given
    Product product = createSimpleProduct();

    // when & then
    ProductVariantId targetProductVariantId = ProductVariantId.of(UUID.randomUUID());

    ProductVariantUpdateCommand productVariantUpdateCommand =
        new ProductVariantUpdateCommand(
            targetProductVariantId, ProductColor.of("RED"), ProductSize.of("M"));

    assertThatThrownBy(() -> product.updateVariant(productVariantUpdateCommand))
        .isInstanceOf(ProductVariantNotSupportedException.class);
  }

  @Test
  void updateVariant_notFound_fail() {
    // given
    Product product = createOptionProduct();
    product.addVariant(newProductVariantCreateCommand("RED", "L"));

    // when & then
    ProductVariantId targetProductVariantId = ProductVariantId.of(UUID.randomUUID());

    ProductVariantUpdateCommand productVariantUpdateCommand =
        new ProductVariantUpdateCommand(
            targetProductVariantId, ProductColor.of("BLUE"), ProductSize.of("M"));

    assertThatThrownBy(() -> product.updateVariant(productVariantUpdateCommand))
        .isInstanceOf(VariantNotFoundException.class);
  }

  @Test
  void removeVariant_success() {
    // given
    Product product = createOptionProduct();
    ProductVariantId targetProductVariantId =
        product.addVariant(newProductVariantCreateCommand("RED", "M"));

    // when
    ProductVariantRemoveCommand productVariantRemoveCommand =
        new ProductVariantRemoveCommand(targetProductVariantId, "TEST_USER");
    product.removeVariant(productVariantRemoveCommand);

    // then
    assertThat(product.getVariants())
        .extracting("color.value", "size.value", "deletedBy")
        .contains(tuple("RED", "M", "TEST_USER"));
  }

  @Test
  void removeVariant_simpleProductType_fail() {
    // given
    Product product = createSimpleProduct();

    // when & then
    ProductVariantRemoveCommand productVariantRemoveCommand =
        new ProductVariantRemoveCommand(ProductVariantId.of(UUID.randomUUID()), "test");

    assertThatThrownBy(() -> product.removeVariant(productVariantRemoveCommand))
        .isInstanceOf(ProductVariantNotSupportedException.class);
  }

  @Test
  void removeVariant_notFound_fail() {
    // given
    Product product = createOptionProduct();
    product.addVariant(newProductVariantCreateCommand("RED", "M"));

    // when & then
    ProductVariantId targetProductVariantId = ProductVariantId.of(UUID.randomUUID());

    assertThatThrownBy(
            () ->
                product.removeVariant(
                    new ProductVariantRemoveCommand(targetProductVariantId, "test")))
        .isInstanceOf(VariantNotFoundException.class);
  }

  @Test
  void getVariants_returnsImmutableList() {
    // given
    Product product = createOptionProduct();
    List<ProductVariant> result = product.getVariants();

    // when & then
    assertThrows(
        UnsupportedOperationException.class, () -> result.add(createProductVariant("RED", "S")));
  }
}
