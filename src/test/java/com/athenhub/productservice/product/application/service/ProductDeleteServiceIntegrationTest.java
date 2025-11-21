package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.PRODUCT_NOT_FOUND;
import static com.athenhub.productservice.product.domain.ProductFixture.newProductVariantCreateCommand;
import static org.assertj.core.api.Assertions.*;

import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductFixture;
import com.athenhub.productservice.product.domain.ProductStatus;
import com.athenhub.productservice.product.domain.repository.ProductRepository;
import com.athenhub.productservice.product.domain.vo.ProductId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class ProductDeleteServiceIntegrationTest {

  @Autowired private ProductRepository productRepository;

  @Autowired private ProductDeleteService productDeleteService;

  @Test
  @DisplayName("상품이 존재하면 soft delete 처리된다.")
  void delete_success() {
    // given
    Product product = ProductFixture.createSimpleProduct();
    productRepository.save(product);

    // when
    UUID productId = product.getId().toUuid();
    productDeleteService.delete(productId, "test-user");

    // then
    Product deletedProduct = productRepository.findById(ProductId.of(productId)).orElseThrow();

    assertThat(deletedProduct.getDeletedBy()).isEqualTo("test-user");
    assertThat(deletedProduct.getDeletedAt()).isNotNull();
  }

  @Test
  @DisplayName("상품과 옵션이 존재하면 soft delete 처리된다.")
  void delete_withVariant_success() {
    // given
    Product product = ProductFixture.createOptionProduct();
    product.addVariant(newProductVariantCreateCommand("RED", "L"));
    productRepository.save(product);

    // when
    UUID productId = product.getId().toUuid();
    productDeleteService.delete(productId, "test-user");

    // then
    Product deletedProduct = productRepository.findById(ProductId.of(productId)).orElseThrow();

    assertThat(deletedProduct.getDeletedBy()).isEqualTo("test-user");
    assertThat(deletedProduct.getDeletedAt()).isNotNull();
    assertThat(deletedProduct.getStatus()).isEqualTo(ProductStatus.DELETED);

    assertThat(deletedProduct.getVariants())
        .hasSize(1)
        .extracting("color.value", "size.value", "deletedBy")
        .contains(tuple("RED", "L", "test-user"));
  }

  @Test
  @DisplayName("상품이 존재하지 않으면 PRODUCT_NOT_FOUND 예외가 발생한다")
  void delete_fail_not_found() {
    UUID targetProductId = UUID.randomUUID();

    // when & then
    assertThatThrownBy(() -> productDeleteService.delete(targetProductId, "test-user"))
        .isInstanceOf(ProductServiceException.class)
        .satisfies(
            e -> {
              ProductServiceException ex = (ProductServiceException) e;
              assertThat(ex.getCode()).isEqualTo(PRODUCT_NOT_FOUND.name());
            });
  }
}
