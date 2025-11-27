package com.athenhub.productservice.product.presentation.controller;

import static com.athenhub.productservice.AssertThatUtils.isEqualTo;
import static com.athenhub.productservice.AssertThatUtils.isNotNull;
import static org.assertj.core.api.Assertions.assertThat;

import com.athenhub.productservice.MockUser;
import com.athenhub.productservice.product.application.dto.ProductSummary;
import com.athenhub.productservice.product.application.service.ProductQueryApplicationService;
import com.athenhub.productservice.product.application.service.ProductQueryService;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductFixture;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@SpringBootTest
@AutoConfigureMockMvc
class ProductQueryControllerTest {

  @Autowired MockMvcTester mvc;

  @MockitoBean ProductQueryService productQueryService;

  @MockitoBean ProductQueryApplicationService queryApplicationService;

  @Test
  @MockUser(roles = "MASTER_MANAGER")
  void getProductDetail() {
    // given
    UUID productId = UUID.randomUUID();

    Product product = ProductFixture.createOptionProduct();
    product.addVariant(ProductFixture.newProductVariantCreateCommand("RED", "260"));

    BDDMockito.given(productQueryService.get(productId)).willReturn(product);

    // when
    MvcTestResult result = mvc.get().uri("/api/v1/products/{productId}", productId).exchange();

    // then
    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .hasPathSatisfying("$.productId", isNotNull())
        .hasPathSatisfying("$.name", isEqualTo("test-productName"))
        .hasPathSatisfying("$.description", isEqualTo("test-description"))
        .hasPathSatisfying("$.price", isEqualTo(1000))
        .hasPathSatisfying("$.hubId", isNotNull())
        .hasPathSatisfying("$.vendorId", isNotNull())
        .hasPathSatisfying("$.type", isEqualTo("OPTION"))
        .hasPathSatisfying("$.status", isEqualTo("DRAFT"))
        .hasPathSatisfying("$.variants[0].productVariantId", isNotNull())
        .hasPathSatisfying("$.variants[0].quantity", isEqualTo(0))
        .hasPathSatisfying("$.variants[0].color", isEqualTo("RED"))
        .hasPathSatisfying("$.variants[0].size", isEqualTo("260"));
  }

  @Test
  @MockUser(roles = "MASTER_MANAGER")
  void searchProducts() {
    // given
    Product product = ProductFixture.createSimpleProduct();

    Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);

    BDDMockito.given(productQueryService.search(BDDMockito.any(), BDDMockito.any()))
        .willReturn(page);

    // when
    MvcTestResult result = mvc.get().uri("/api/v1/products/search").exchange();

    // then
    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .hasPathSatisfying("$.content[0].productId", isNotNull())
        .hasPathSatisfying("$.content[0].name", isEqualTo("test-productName"))
        .hasPathSatisfying("$.content[0].description", isEqualTo("test-description"))
        .hasPathSatisfying("$.content[0].price", isEqualTo(1000))
        .hasPathSatisfying("$.content[0].hubId", isNotNull())
        .hasPathSatisfying("$.content[0].vendorId", isNotNull())
        .hasPathSatisfying("$.content[0].type", isEqualTo("SIMPLE"))
        .hasPathSatisfying("$.content[0].status", isEqualTo("DRAFT"))
        .hasPathSatisfying("$.content[0].deleted", isEqualTo(false))
        .hasPathSatisfying("$.pageable.pageNumber", isEqualTo(0))
        .hasPathSatisfying("$.pageable.pageSize", isEqualTo(10))
        .hasPathSatisfying("$.totalElements", isEqualTo(1));
  }

  @Test
  @MockUser(roles = "MASTER_MANAGER")
  void managedProducts_as_MasterManager() {
    // given
    Product product = ProductFixture.createSimpleProduct();

    Page<ProductSummary> page =
        new PageImpl<>(List.of(ProductSummary.from(product)), PageRequest.of(0, 10), 1);

    BDDMockito.given(
            queryApplicationService.getProductsManagedBy(
                BDDMockito.any(), BDDMockito.any(), BDDMockito.any()))
        .willReturn(page);

    // when
    MvcTestResult result = mvc.get().uri("/api/v1/products/managed").exchange();
    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .hasPathSatisfying("$.content[0].productId", isNotNull())
        .hasPathSatisfying("$.content[0].name", isEqualTo("test-productName"))
        .hasPathSatisfying("$.content[0].description", isEqualTo("test-description"))
        .hasPathSatisfying("$.content[0].price", isEqualTo(1000))
        .hasPathSatisfying("$.content[0].hubId", isNotNull())
        .hasPathSatisfying("$.content[0].vendorId", isNotNull())
        .hasPathSatisfying("$.content[0].type", isEqualTo("SIMPLE"))
        .hasPathSatisfying("$.content[0].status", isEqualTo("DRAFT"))
        .hasPathSatisfying("$.content[0].deleted", isEqualTo(false))
        .hasPathSatisfying("$.pageable.pageNumber", isEqualTo(0))
        .hasPathSatisfying("$.pageable.pageSize", isEqualTo(10))
        .hasPathSatisfying("$.totalElements", isEqualTo(1));
  }

  @Test
  @MockUser(roles = "SHIPPING_AGENT")
  void managedProducts_as_ShippingAgent() {
    // given
    Product product = ProductFixture.createSimpleProduct();

    Page<ProductSummary> page =
        new PageImpl<>(List.of(ProductSummary.from(product)), PageRequest.of(0, 10), 1);

    BDDMockito.given(
            queryApplicationService.getProductsManagedBy(
                BDDMockito.any(), BDDMockito.any(), BDDMockito.any()))
        .willReturn(page);

    // when
    MvcTestResult result = mvc.get().uri("/api/v1/products/managed").exchange();

    assertThat(result).hasStatus(HttpStatus.FORBIDDEN);
  }
}
