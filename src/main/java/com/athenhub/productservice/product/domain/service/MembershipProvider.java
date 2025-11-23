package com.athenhub.productservice.product.domain.service;

import com.athenhub.productservice.product.domain.dto.MemberInfo;
import java.util.UUID;

/**
 * 외부 Membership 시스템에서 회원 정보를 조회하기 위한 도메인 서비스 포트.
 *
 * <p>Product 도메인은 사용자(User)의 소속 Hub, Vendor 정보를 직접 관리하지 않기 때문에 해당 정보가 필요할 경우 이 인터페이스를 통해 Membership
 * 서비스에 위임한다.
 *
 * <p>구현체는 보통 다른 마이크로서비스 호출(OpenFeign, RestTemplate 등)로 구성되며 도메인 또는 애플리케이션 계층은 이 인터페이스에만 의존한다.
 *
 * <p>주 용도는 다음과 같다.
 *
 * <ul>
 *   <li>상품 조회 범위 제한
 *   <li>상품 등록/수정/삭제 권한 검증
 *   <li>사용자 소속 Hub / Vendor 식별
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
public interface MembershipProvider {

  /**
   * 사용자 ID를 기반으로 해당 사용자의 소속 정보를 조회한다.
   *
   * @param userId 조회할 사용자 ID
   * @return 사용자 소속 정보를 담은 {@link MemberInfo}
   * @author 김지원
   * @since 1.0.0
   */
  MemberInfo getMember(UUID userId);
}
