package com.athenhub.productservice.product.infrastructure.service;

import com.athenhub.productservice.product.domain.dto.MemberInfo;
import com.athenhub.productservice.product.domain.service.MembershipProvider;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * 멤버십 서비스와의 통신을 담당하는 {@link MembershipProvider}의 인프라 구현체.
 *
 * <p>외부 Membership 서비스(API 또는 다른 Bounded Context)로부터 사용자의 소속 정보(허브, 벤더)를 조회하여 {@link MemberInfo}로
 * 변환한다.
 *
 * <p>도메인 계층은 이 구현체의 존재를 알지 못하고, {@link MembershipProvider} 인터페이스에만 의존한다.
 *
 * <p>현재는 임시 구현으로 랜덤 값을 반환하지만, 향후 FeignClient, WebClient 등을 통해 실제 멤버십 서비스와 연동할 예정이다.
 *
 * <pre>
 *  MembershipProvider provider = new RemoteMembershipProvider();
 *  MemberInfo info = provider.getMember(userId);
 * </pre>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
public class RemoteMembershipProvider implements MembershipProvider {

  /**
   * 사용자 ID를 기반으로 멤버의 소속 정보를 조회한다.
   *
   * <p>향후 실제 구현에서는 외부 멤버십 서비스로 요청을 전송하여 허브 ID와 벤더 ID를 조회한다.
   *
   * @param userId 멤버의 사용자 ID
   * @return 멤버의 소속 정보
   */
  @Override
  public MemberInfo getMember(UUID userId) {
    // TODO: 실제 Membership 서비스 연동 (Feign / WebClient)
    return new MemberInfo(HubId.of(UUID.randomUUID()), VendorId.of(UUID.randomUUID()));
  }
}
