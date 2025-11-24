package com.athenhub.productservice.product.application.service.strategy;

import com.athenhub.productservice.membership.domain.MemberRole;
import com.athenhub.productservice.membership.domain.MemberRoles;
import com.athenhub.productservice.product.application.service.ProductQueryApplicationService;
import com.athenhub.productservice.product.application.service.ProductQueryService;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * {@link MemberRole#HUB_MANAGER} 역할을 가진 사용자를 위한 상품 조회.
 * 전략.
 *
 * <p>현재 사용자가 소속된 허브({@code hubId}) 기준으로 상품을 조회한다. 조회 자체는 {@link ProductQueryService}에 위임하며, 이 클래스는
 * 조회 범위(허브)를 결정하는 역할만 수행한다.
 *
 * <p>{@link
 * ProductQueryApplicationService}에서 {@link
 * #supports(MemberRoles)}를 통해 선택되어 실행된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Component
public class HubManagerProductQueryStrategy implements ProductQueryStrategy {

  /** 허브 기준 상품 조회를 담당하는 서비스. */
  private final ProductQueryService productQueryService;

  /**
   * 사용자가 소속된 허브 기준으로 상품을 조회한다.
   *
   * @param member 사용자 소속 정보
   * @param pageable 페이징 정보
   * @return 해당 허브에 속한 상품 목록
   * @author 김지원
   * @since 1.0.0
   */
  @Override
  public Page<Product> query(MemberInfo member, Pageable pageable) {
    return productQueryService.getByHubId(member.hubId(), pageable);
  }

  /**
   * 현재 전략이 HUB_MANAGER 역할을 지원하는지 여부를 반환한다.
   *
   * @param roles 사용자 역할
   * @return HUB_MANAGER 권한이 있으면 true
   * @author 김지원
   * @since 1.0.0
   */
  @Override
  public boolean supports(MemberRoles roles) {
    return roles.containsHubManager();
  }
}
