package com.athenhub.productservice.membership.domain;

import java.util.List;

/**
 * 사용자의 역할 목록을 감싸는 1급 컬렉션(First-Class Collection).
 *
 * <p>단순히 {@link List}를 노출하지 않고, 도메인 의미를 가진 객체로 한 번 감싸 역할 판단 로직을 이 클래스 내부로 응집시킨다.
 *
 * <p>이를 통해 Application / Service 계층에서는 다음과 같이 비즈니스 의도가 드러나는 코드 작성이 가능해진다.
 *
 * <pre>
 * if (memberRoles.containsHubManager()) { ... }
 * if (memberRoles.containsVendorAgent()) { ... }
 * </pre>
 *
 * <p>또한 향후 역할 정책(우선순위, 복합 권한 등)이 변경되더라도 호출부를 수정하지 않고 이 클래스에서만 수정하면 된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public record MemberRoles(List<MemberRole> roles) {

  /**
   * 정적 팩토리 메서드.
   *
   * <p>역할 목록을 {@link MemberRoles}로 감싼다. 생성자를 직접 호출하는 대신 의도를 드러내기 위해 제공된다.
   *
   * @param memberRoles 사용자 역할 목록
   * @return MemberRoles 객체
   */
  public static MemberRoles of(List<MemberRole> memberRoles) {
    return new MemberRoles(memberRoles);
  }

  /**
   * 사용자가 MASTER_MANAGER 권한을 가지고 있는지 확인한다.
   *
   * <p>MASTER_MANAGER는 전체 범위의 권한을 가지는 최상위 관리자이다.
   *
   * @return MASTER_MANAGER 포함 여부
   */
  public boolean containsMasterManager() {
    return roles.contains(MemberRole.MASTER_MANAGER);
  }

  /**
   * 사용자가 HUB_MANAGER 권한을 가지고 있는지 확인한다.
   *
   * <p>HUB_MANAGER는 특정 허브(Hub)에 대한 관리 권한을 가진다.
   *
   * @return HUB_MANAGER 포함 여부
   */
  public boolean containsHubManager() {
    return roles.contains(MemberRole.HUB_MANAGER);
  }

  /**
   * 사용자가 VENDOR_AGENT 권한을 가지고 있는지 확인한다.
   *
   * <p>VENDOR_AGENT는 특정 벤더(Vendor)에 대한 관리 권한을 가진다.
   *
   * @return VENDOR_AGENT 포함 여부
   */
  public boolean containsVendorAgent() {
    return roles.contains(MemberRole.VENDOR_AGENT);
  }
}
