package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.PRODUCT_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.tuple;

import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.domain.*;
import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.repository.ProductRepository;
import com.athenhub.productservice.product.domain.vo.Price;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class ProductQueryServiceIntegrationTest {

  @Autowired private ProductRepository productRepository;

  @Autowired private ProductQueryService productQueryService;

  @Test
  @DisplayName("SIMPLE 타입 상품을 단건 조회할 수 있다")
  void getSimpleProduct_success() {
    // given
    ProductCreateCommand productCreateCommand =
        ProductFixture.newProductCreateCommand(
            "test-name", "test-description", 1000, ProductType.SIMPLE);

    Product product = Product.create(productCreateCommand);
    productRepository.save(product);

    // when
    UUID targetId = product.getId().toUuid();
    Product foundProduct = productQueryService.get(targetId);

    // then
    assertThat(foundProduct.getId()).isNotNull();
    assertThat(foundProduct.getName()).isEqualTo("test-name");
    assertThat(foundProduct.getDescription()).isEqualTo("test-description");
    assertThat(foundProduct.getPrice()).isEqualTo(Price.of(1000));
    assertThat(foundProduct.getType()).isEqualTo(ProductType.SIMPLE);
    assertThat(foundProduct.getStatus()).isEqualTo(ProductStatus.DRAFT);
    assertThat(foundProduct.getVariants())
        .hasSize(1)
        .extracting("color.value", "size.value")
        .contains(tuple("NONE", "NONE"));
  }

  @Test
  @DisplayName("OPTION 타입 상품과 옵션을 함께 조회할 수 있다")
  void getOptionProduct_success() {
    // given
    ProductCreateCommand productCreateCommand =
        ProductFixture.newProductCreateCommand(
            "test-name", "test-description", 1000, ProductType.OPTION);

    Product product = Product.create(productCreateCommand);
    product.addVariant(ProductFixture.newProductVariantCreateCommand("RED", "M"));
    product.addVariant(ProductFixture.newProductVariantCreateCommand("BLACK", "L"));

    productRepository.save(product);

    // when
    UUID targetId = product.getId().toUuid();
    Product foundProduct = productQueryService.get(targetId);

    // then
    assertThat(foundProduct.getId()).isNotNull();
    assertThat(foundProduct.getName()).isEqualTo("test-name");
    assertThat(foundProduct.getDescription()).isEqualTo("test-description");
    assertThat(foundProduct.getPrice()).isEqualTo(Price.of(1000));
    assertThat(foundProduct.getType()).isEqualTo(ProductType.OPTION);
    assertThat(foundProduct.getStatus()).isEqualTo(ProductStatus.DRAFT);
    assertThat(foundProduct.getVariants())
        .hasSize(2)
        .extracting("color.value", "size.value")
        .contains(tuple("RED", "M"), tuple("BLACK", "L"));
  }

  @Test
  @DisplayName("존재하지 않는 상품을 조회하면 PRODUCT_NOT_FOUND 예외가 발생한다")
  void get_fail_not_found() {
    // given
    UUID productId = UUID.randomUUID();

    // then
    assertThatThrownBy(() -> productQueryService.get(productId))
        .isInstanceOf(ProductServiceException.class)
        .satisfies(
            e -> {
              ProductServiceException ex = (ProductServiceException) e;
              assertThat(ex.getCode()).isEqualTo(PRODUCT_NOT_FOUND.name());
            });
  }
}
