package com.athenhub.productservice.product.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.athenhub.productservice.AssertThatUtils;
import com.athenhub.productservice.MockUser;
import com.athenhub.productservice.product.application.dto.ProductBasicUpdateRequest;
import com.athenhub.productservice.product.application.dto.ProductRegisterRequest;
import com.athenhub.productservice.product.application.dto.ProductResponse;
import com.athenhub.productservice.product.application.dto.ProductVariantUpdateRequest;
import com.athenhub.productservice.product.application.service.ProductCommandApplicationService;
import com.athenhub.productservice.product.domain.ProductType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@AutoConfigureMockMvc
@SpringBootTest
class ProductCommandControllerTest {

  @Autowired MockMvcTester mvc;

  @Autowired ObjectMapper objectMapper;

  @MockitoBean ProductCommandApplicationService commandService;

  @Test
  @MockUser(roles = "MASTER_MANAGER")
  void register() throws Exception {
    // given
    ProductRegisterRequest request =
        new ProductRegisterRequest(
            "상품명", "설명", 1000L, UUID.randomUUID(), UUID.randomUUID(), ProductType.SIMPLE, null);

    UUID productId = UUID.randomUUID();
    given(commandService.register(any(), any())).willReturn(new ProductResponse(productId));

    // when
    MvcTestResult result =
        mvc.post()
            .uri("/products")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request))
            .exchange();
    // then
    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .hasPathSatisfying("$.productId", AssertThatUtils.isEqualTo(productId.toString()));
  }

  @Test
  @MockUser(roles = "SHIPPING_AGENT")
  void register_as_shippingAgent() throws Exception {
    // given
    ProductRegisterRequest request =
        new ProductRegisterRequest(
            "상품명", "설명", 1000L, UUID.randomUUID(), UUID.randomUUID(), ProductType.SIMPLE, null);

    UUID productId = UUID.randomUUID();

    // when
    MvcTestResult result =
        mvc.post()
            .uri("/products")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request))
            .exchange();
    // then
    assertThat(result).hasStatus(HttpStatus.FORBIDDEN);
  }

  @Test
  @MockUser(roles = "MASTER_MANAGER")
  void updateBasicInfo() throws Exception {
    // given
    UUID productId = UUID.randomUUID();

    ProductBasicUpdateRequest request = new ProductBasicUpdateRequest("변경된 상품명", "변경된 설명", 2000L);

    given(commandService.updateBasicInfo(eq(productId), any(), any()))
        .willReturn(new ProductResponse(productId));

    // when
    MvcTestResult result =
        mvc.put()
            .uri("/products/{productId}/basic", productId)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request))
            .exchange();

    // then
    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .hasPathSatisfying("$.productId", AssertThatUtils.isEqualTo(productId.toString()));
  }

  @Test
  @MockUser(roles = "MASTER_MANAGER")
  void updateVariants() throws Exception {
    // given
    UUID productId = UUID.randomUUID();

    ProductVariantUpdateRequest request = new ProductVariantUpdateRequest(null, null, null);

    given(commandService.updateVariants(eq(productId), any(), any(), any()))
        .willReturn(new ProductResponse(productId));

    // when
    MvcTestResult result =
        mvc.put()
            .uri("/products/{productId}/variants", productId)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request))
            .exchange();

    // then
    assertThat(result)
        .hasStatusOk()
        .bodyJson()
        .hasPathSatisfying("$.productId", AssertThatUtils.isEqualTo(productId.toString()));
  }

  @Test
  @MockUser(roles = "SHIPPING_AGENT")
  void updateBasicInfo_as_ShippingAgent() throws Exception {
    // given
    UUID productId = UUID.randomUUID();

    ProductBasicUpdateRequest request = new ProductBasicUpdateRequest("변경된 상품명", "변경된 설명", 2000L);

    given(commandService.updateBasicInfo(eq(productId), any(), any()))
        .willReturn(new ProductResponse(productId));

    // when
    MvcTestResult result =
        mvc.put()
            .uri("/products/{productId}/basic", productId)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(request))
            .exchange();

    // then
    assertThat(result).hasStatus(HttpStatus.FORBIDDEN);
  }

  @Test
  @MockUser(roles = "MASTER_MANAGER")
  void delete() {
    // given
    UUID productId = UUID.randomUUID();

    // when
    MvcTestResult result = mvc.delete().uri("/products/{productId}", productId).exchange();

    // then
    assertThat(result).hasStatusOk();
  }

  @Test
  @MockUser(roles = "SHIPPING_AGENT")
  void delete_as_shippingAgent() {
    // given
    UUID productId = UUID.randomUUID();

    // when
    MvcTestResult result = mvc.delete().uri("/products/{productId}", productId).exchange();

    // then
    assertThat(result).hasStatus(HttpStatus.FORBIDDEN);
  }

  @Test
  @MockUser(roles = "VENDOR_AGENT")
  void delete_as_vendorAgent() {
    UUID productId = UUID.randomUUID();

    MvcTestResult result = mvc.delete().uri("/products/{productId}", productId).exchange();

    assertThat(result).hasStatus(HttpStatus.FORBIDDEN);
  }
}
