package com.athenhub.productservice.product.application.dto;

import com.athenhub.productservice.product.domain.dto.ProductBasicUpdateCommand;
import com.athenhub.productservice.product.domain.vo.Price;
import java.util.UUID;

/**
 * 상품 기본 정보 수정 요청 DTO.
 *
 * <p>Application Layer에서 들어온 요청을 도메인이 이해할 수 있는 {@link ProductBasicUpdateCommand}로 변환한다.
 */
public record ProductBasicUpdateRequest(
    UUID productId, String name, String description, long price) {

  /** 요청값을 도메인 명령 객체로 변환한다. */
  public ProductBasicUpdateCommand toBasicUpdateCommand() {
    return new ProductBasicUpdateCommand(name, description, Price.of(price));
  }
}
