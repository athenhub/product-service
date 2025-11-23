package com.athenhub.productservice.membership.domain;

/**
 * 서비스 내에서 사용되는 회원의 역할(Role) 정의.
 *
 * <p>각 역할에 따라 접근 가능한 리소스와 수행 가능한 행위가 달라지며, 상품 조회, 등록, 수정, 배송 처리 등의 권한 판단 기준으로 사용된다.
 *
 * <ul>
 *   <li>{@link #USER} : 일반 사용자
 *   <li>{@link #MASTER_MANAGER} : 마스터 관리자
 *   <li>{@link #HUB_MANAGER} : 허브 관리자
 *   <li>{@link #SHIPPING_AGENT} : 배송 담당자
 *   <li>{@link #VENDOR_AGENT} : 업체 담당자
 * </ul>
 *
 * <p>주로 인증/인가 과정에서 사용되며, {@code SecurityContext} 또는 외부 Membership 서비스와 연동되어 활용된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public enum MemberRole {

  /** 일반 사용자. */
  USER,

  /** 마스터 관리자. */
  MASTER_MANAGER,

  /** 허브 관리자. */
  HUB_MANAGER,

  /** 배송 담당자. */
  SHIPPING_AGENT,

  /** 업체 담당자. */
  VENDOR_AGENT
}
