package com.athenhub.productservice.product.domain;

import static com.athenhub.productservice.product.domain.ProductFixture.*;
import static com.athenhub.productservice.product.domain.ProductType.OPTION;
import static com.athenhub.productservice.product.domain.ProductType.SIMPLE;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.athenhub.productservice.product.domain.dto.ProductBasicUpdateCommand;
import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantRemoveCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantUpdateCommand;
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
    ProductCreateCommand productCommand = newProductCreateCommand(SIMPLE);

    // when
    Product product = Product.create(productCommand);

    // then
    assertThat(product);
    assertThat(product.getType()).isEqualTo(SIMPLE);
    assertThat(product.getId()).isNotNull();
    assertThat(product.getHubId()).isNotNull();
    assertThat(product.getVendorId()).isNotNull();
    assertThat(product.getPrice()).isNotNull();
    assertThat(product.getStatus()).isEqualTo(ProductStatus.DRAFT);
  }

  @Test
  void updateBasic() {
    // given
    Product product = createSimpleProduct();

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
    Product product = createSimpleProduct();

    // when
    ProductType changedType = OPTION;
    product.changeType(changedType);

    // then
    assertThat(product.getType()).isEqualTo(changedType);
  }

  @Test
  void changeStatus() {
    // given
    Product product = createSimpleProduct();

    // when
    ProductStatus changedStatus = ProductStatus.HIDDEN;
    product.changeStatus(changedStatus);

    // then
    assertThat(product.getStatus()).isEqualTo(changedStatus);
  }

  @Test
  void addVariant() {
    // given
    Product product = createOptionProduct();
    ProductVariant productVariant1 = createProductVariant("RED", "M");
    ProductVariant productVariant2 = createProductVariant("BLUE", "L");

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
    Product product = createSimpleProduct();
    ProductVariant productVariant = createProductVariant("RED", "M");

    // when & then
    assertThatThrownBy(() -> product.addVariant(productVariant))
        .isInstanceOf(ProductVariantNotSupportedException.class);
  }

  @Test
  void addVariant_withDuplicatedVariant() {
    // given
    Product product = createOptionProduct();
    ProductVariant productVariant1 = createProductVariant("RED", "M");
    product.addVariant(productVariant1);

    // when & then
    ProductVariant productVariant2 = createProductVariant("RED", "M");

    assertThatThrownBy(() -> product.addVariant(productVariant2))
        .isInstanceOf(VariantAlreadyExistsException.class);
  }

  @Test
  void updateVariant() {
    // given
    Product product = createOptionProduct();
    ProductVariant productVariant = createProductVariant("RED", "L");
    product.addVariant(productVariant);

    // when & then
    ProductVariantId targetProductVariantId = productVariant.getId();

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
  void updateVariant_withSimpleType() {
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
  void updateVariant_withNotFoundVariantId() {
    // given
    Product product = createOptionProduct();
    product.addVariant(createProductVariant("RED", "L"));

    // when & then
    ProductVariantId targetProductVariantId = ProductVariantId.of(UUID.randomUUID());

    ProductVariantUpdateCommand productVariantUpdateCommand =
        new ProductVariantUpdateCommand(
            targetProductVariantId, ProductColor.of("BLUE"), ProductSize.of("M"));

    assertThatThrownBy(() -> product.updateVariant(productVariantUpdateCommand))
        .isInstanceOf(VariantNotFoundException.class);
  }

  @Test
  void removeVariant() {
    // given
    Product product = createOptionProduct();
    ProductVariant productVariant = createProductVariant("RED", "M");
    product.addVariant(productVariant);

    // when
    ProductVariantId targetProductVariantId = productVariant.getId();
    ProductVariantRemoveCommand productVariantRemoveCommand =
        new ProductVariantRemoveCommand(targetProductVariantId, "TEST_USER");
    product.removeVariant(productVariantRemoveCommand);

    // then
    assertThat(product.getVariants())
        .extracting("color.value", "size.value", "deletedBy")
        .contains(tuple("RED", "M", "TEST_USER"));
  }

  @Test
  void removeVariant_withSimpleType() {
    // given
    Product product = createSimpleProduct();

    // when & then
    ProductVariantRemoveCommand productVariantRemoveCommand =
        new ProductVariantRemoveCommand(ProductVariantId.of(UUID.randomUUID()), "test");

    assertThatThrownBy(() -> product.removeVariant(productVariantRemoveCommand))
        .isInstanceOf(ProductVariantNotSupportedException.class);
  }

  @Test
  void removeVariant_withNotFoundVariantId() {
    // given
    Product product = createOptionProduct();
    product.addVariant(createProductVariant("RED", "M"));

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
