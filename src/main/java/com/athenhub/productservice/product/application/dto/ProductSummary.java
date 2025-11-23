package com.athenhub.productservice.product.application.dto;

import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductStatus;
import com.athenhub.productservice.product.domain.ProductType;
import java.util.UUID;

/**
 * 상품의 요약 정보를 전달하기 위한 DTO.
 *
 * <p>목록 조회, 검색 결과, 간단한 카드 뷰 등에서 사용되는 경량화된 전송 객체이다. 상세 정보(옵션, 이력 등)는 포함하지 않으며, 기본적인 상품 메타 정보에 집중한다.
 *
 * <ul>
 *   <li>상품 식별자
 *   <li>이름, 설명, 가격
 *   <li>허브/벤더 소속
 *   <li>상품 타입 및 상태
 *   <li>논리 삭제 여부
 * </ul>
 *
 * <p><b>설계 의도</b>
 *
 * <ul>
 *   <li>도메인 엔티티({@link Product})를 그대로 외부에 노출하지 않기 위함
 *   <li>목록/검색 API에서 불필요한 데이터 전송을 줄이기 위함
 *   <li>상세 DTO({@code ProductDetails})와 역할을 명확히 분리
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
public record ProductSummary(
    UUID productId,
    String name,
    String description,
    Long price,
    UUID hubId,
    UUID vendorId,
    ProductType type,
    ProductStatus status,
    boolean deleted) {

  /**
   * {@link Product} 도메인 객체를 {@link ProductSummary}로 변환한다.
   *
   * <p>값 객체(VO)는 UUID 또는 기본 타입으로 변환되며, 논리 삭제 여부는 {@code deletedAt != null} 여부로 판단한다.
   *
   * @param product 변환할 도메인 상품 엔티티
   * @return 변환된 {@link ProductSummary}
   */
  public static ProductSummary from(Product product) {
    return new ProductSummary(
        product.getId().toUuid(),
        product.getName(),
        product.getDescription(),
        product.getPrice().value(),
        product.getHubId().toUuid(),
        product.getVendorId().toUuid(),
        product.getType(),
        product.getStatus(),
        product.getDeletedAt() != null);
  }
}
