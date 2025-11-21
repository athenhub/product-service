package com.athenhub.productservice.product.domain.service;

import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.UUID;

/**
 * 상품(Product)에 대한 행위별 접근 권한 정책을 정의하는 인터페이스.
 *
 * <p>요청자(사용자)가 특정 Hub / Vendor 내의 상품에 대해 생성(Create), 수정(Update), 삭제(Delete) 권한을 가지는지 판단한다.
 *
 * <p>구현체는 인프라 계층에서 작성되며(예: 외부 인증/인가 서버 연동), 도메인 또는 애플리케이션 계층에서는 이 인터페이스에만 의존한다.
 *
 * <p><b>네이밍 규칙:</b>
 *
 * <ul>
 *   <li>메서드는 허용이 아닌 <b>거부(Denied)</b> 여부를 반환한다.
 *   <li>{@code true} → 권한 없음 (차단됨)
 *   <li>{@code false} → 권한 있음 (허용됨)
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
public interface PermissionPolicy {

  /**
   * 상품 생성 권한 여부를 판단한다.
   *
   * @param requestId 요청 사용자 식별자(UUID)
   * @param hubId 상품이 속한 허브 식별자
   * @param vendorId 상품이 속한 업체 식별자
   * @return 권한이 없으면 {@code true}, 권한이 있으면 {@code false}
   */
  boolean isCreateDenied(UUID requestId, HubId hubId, VendorId vendorId);

  /**
   * 상품 수정 권한 여부를 판단한다.
   *
   * @param requestId 요청 사용자 식별자(UUID)
   * @param hubId 상품이 속한 허브 식별자
   * @param vendorId 상품이 속한 업체 식별자
   * @return 권한이 없으면 {@code true}, 권한이 있으면 {@code false}
   */
  boolean isUpdateDenied(UUID requestId, HubId hubId, VendorId vendorId);

  /**
   * 상품 삭제 권한 여부를 판단한다.
   *
   * @param requestId 요청 사용자 식별자(UUID)
   * @param hubId 상품이 속한 허브 식별자
   * @return 권한이 없으면 {@code true}, 권한이 있으면 {@code false}
   */
  boolean isDeleteDenied(UUID requestId, HubId hubId);
}
