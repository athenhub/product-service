package com.athenhub.productservice.product.application.dto;

import com.athenhub.productservice.product.domain.ProductType;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * 상품 등록 요청 DTO.
 *
 * <p>Application Layer에서 상품 생성에 필요한 기본 정보와 옵션 목록(필요한 경우)을 전달한다.
 */
public record ProductRegisterRequest(
    @NotNull UUID hubId,
    @NotNull UUID vendorId,
    long price,
    @NotNull ProductType type,
    List<RegisterProductVariant> productVariants) {

  /** 옵션 등록 요청 DTO. */
  public record RegisterProductVariant(String color, String size) {}
}
