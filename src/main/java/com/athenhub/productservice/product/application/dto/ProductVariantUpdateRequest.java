package com.athenhub.productservice.product.application.dto;

import java.util.List;
import java.util.UUID;

/**
 * 상품 옵션 변경 요청 DTO.
 *
 * <p>옵션 추가·수정·삭제 명령을 한 번에 전달한다.
 */
public record ProductVariantUpdateRequest(
    List<Add> adds, List<Update> updates, List<Remove> removes) {

  /** 옵션 추가 요청. */
  public record Add(String color, String size) {}

  /** 옵션 수정 요청. */
  public record Update(UUID variantId, String color, String size) {}

  /** 옵션 삭제 요청. */
  public record Remove(UUID variantId) {}
}
