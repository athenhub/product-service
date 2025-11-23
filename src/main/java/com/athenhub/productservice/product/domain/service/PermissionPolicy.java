package com.athenhub.productservice.product.domain.service;

import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.UUID;

/**
 * 상품(Product)에 대한 행위별 권한 정책을 정의하는 도메인 서비스 인터페이스.
 *
 * <p>요청 사용자가 특정 Hub / Vendor 범위 내에서 상품을 생성(Create), 수정(Update), 삭제(Delete)할 수 있는지 검증한다.
 *
 * <p>권한 판단 로직의 실제 구현은 외부 시스템(인증/인가 서버 등)에 의존할 수 있으므로, 구현체는 일반적으로 인프라 계층에 위치한다.
 *
 * <p><b>NOTE:</b> 메서드는 허용 여부가 아닌 <b>거부(Denied)</b> 여부를 반환한다. 즉, {@code true}는 접근 불가를 의미한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public interface PermissionPolicy {

  /**
   * 상품 생성 권한을 거부해야 하는지 여부를 반환한다.
   *
   * @param userId 요청 사용자 식별자
   * @param hubId 대상 허브 식별자
   * @param vendorId 대상 업체 식별자
   * @return {@code true}이면 생성 권한 없음, {@code false}이면 생성 가능
   */
  boolean isCreateDenied(UUID userId, HubId hubId, VendorId vendorId);

  /**
   * 상품 수정 권한을 거부해야 하는지 여부를 반환한다.
   *
   * @param userId 요청 사용자 식별자
   * @param hubId 대상 허브 식별자
   * @param vendorId 대상 업체 식별자
   * @return {@code true}이면 수정 권한 없음, {@code false}이면 수정 가능
   */
  boolean isUpdateDenied(UUID userId, HubId hubId, VendorId vendorId);

  /**
   * 상품 삭제 권한을 거부해야 하는지 여부를 반환한다.
   *
   * @param userId 요청 사용자 식별자
   * @param hubId 대상 허브 식별자
   * @return {@code true}이면 삭제 권한 없음, {@code false}이면 삭제 가능
   */
  boolean isDeleteDenied(UUID userId, HubId hubId);
}
