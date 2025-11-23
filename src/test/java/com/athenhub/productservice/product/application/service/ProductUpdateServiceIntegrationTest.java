package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.PRODUCT_NOT_FOUND;
import static com.athenhub.productservice.product.domain.ProductFixture.newProductVariantCreateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.athenhub.productservice.product.application.dto.ProductBasicUpdateRequest;
import com.athenhub.productservice.product.application.dto.ProductVariantUpdateRequest;
import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductFixture;
import com.athenhub.productservice.product.domain.repository.ProductRepository;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.ProductId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class ProductUpdateServiceIntegrationTest {

  @Autowired private ProductUpdateService productUpdateService;

  @Autowired private ProductRepository productRepository;

  @Test
  @DisplayName("상품 기본정보가 정상적으로 수정된다")
  void update_basic_success() {
    // given
    Product product = ProductFixture.createSimpleProduct();
    productRepository.save(product);

    UUID productId = product.getId().toUuid();

    ProductBasicUpdateRequest request =
        new ProductBasicUpdateRequest("changedName", "changedDescription", 20000);

    // when
    productUpdateService.updateBasicInfo(productId, request);

    // then
    Product updated = productRepository.findById(ProductId.of(productId)).orElseThrow();

    assertThat(updated.getName()).isEqualTo("changedName");
    assertThat(updated.getDescription()).isEqualTo("changedDescription");
    assertThat(updated.getPrice()).isEqualTo(Price.of(20000));
  }

  @Test
  @DisplayName("상품이 존재하지 않으면 기본정보 수정 시 예외가 발생한다")
  void update_basic_fail_not_found() {
    // given
    ProductBasicUpdateRequest request =
        new ProductBasicUpdateRequest("changedName", "changedDescription", 20000);

    // when & then
    assertThatThrownBy(() -> productUpdateService.updateBasicInfo(UUID.randomUUID(), request))
        .isInstanceOf(ProductServiceException.class)
        .satisfies(
            e -> {
              ProductServiceException ex = (ProductServiceException) e;
              assertThat(ex.getCode()).isEqualTo(PRODUCT_NOT_FOUND.name());
            });
  }

  @Test
  @DisplayName("상품 옵션(Variant)이 정상적으로 수정/추가/삭제된다")
  void update_variant_success() {
    // given
    Product product = ProductFixture.createOptionProduct();
    product.addVariant(newProductVariantCreateCommand("RED", "M"));
    product.addVariant(newProductVariantCreateCommand("BLACK", "L"));
    productRepository.save(product);

    UUID productId = product.getId().toUuid();
    UUID variantId1 = product.getVariants().getFirst().getId().toUuid();
    UUID variantId2 = product.getVariants().get(1).getId().toUuid();

    ProductVariantUpdateRequest request =
        new ProductVariantUpdateRequest(
            List.of(new ProductVariantUpdateRequest.Add("BLUE", "L")),
            List.of(new ProductVariantUpdateRequest.Update(variantId1, "RED", "S")),
            List.of(new ProductVariantUpdateRequest.Remove(variantId2)));

    // when
    productUpdateService.updateProductVariant(productId, request, "test-user");

    // then
    Product updated = productRepository.findById(ProductId.of(productId)).orElseThrow();

    assertThat(updated.getVariants())
        .extracting("color.value", "size.value", "deletedBy")
        .contains(
            tuple("BLUE", "L", null), tuple("RED", "S", null), tuple("BLACK", "L", "test-user"));
  }

  @Test
  @DisplayName("상품이 존재하지 않으면 옵션 수정 시 예외가 발생한다")
  void update_variant_fail_not_found() {
    // given
    ProductVariantUpdateRequest request =
        new ProductVariantUpdateRequest(List.of(), List.of(), List.of());

    // when & then
    assertThatThrownBy(
            () -> productUpdateService.updateProductVariant(UUID.randomUUID(), request, "kim"))
        .isInstanceOf(ProductServiceException.class)
        .satisfies(
            e -> {
              ProductServiceException ex = (ProductServiceException) e;
              assertThat(ex.getCode()).isEqualTo(PRODUCT_NOT_FOUND.name());
            });
  }
}
