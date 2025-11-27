package com.athenhub.productservice.product.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.athenhub.productservice.product.application.dto.ProductRegisterRequest;
import com.athenhub.productservice.product.application.dto.ProductResponse;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductType;
import com.athenhub.productservice.product.domain.repository.ProductRepository;
import com.athenhub.productservice.product.domain.vo.ProductId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProductRegisterServiceIntegrationTest {

  @Autowired private ProductRegisterService productRegisterService;

  @Autowired private ProductRepository productRepository;

  @Test
  @DisplayName("상품을 등록하면 실제 DB에 저장된다")
  void register_success_integration() {
    // given
    ProductRegisterRequest request =
        new ProductRegisterRequest(
            "test-productName",
            "test-description",
            10_000L,
            UUID.randomUUID(),
            UUID.randomUUID(),
            ProductType.OPTION,
            List.of(
                new ProductRegisterRequest.RegisterProductVariant("RED", "M"),
                new ProductRegisterRequest.RegisterProductVariant("BLACK", "L")));

    // when
    ProductResponse response = productRegisterService.register(request);

    // then
    Product savedProduct =
        productRepository.findById(ProductId.of(response.productId())).orElseThrow();
    assertThat(savedProduct.getId()).isNotNull();
  }
}
