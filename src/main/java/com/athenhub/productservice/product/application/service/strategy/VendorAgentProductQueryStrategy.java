package com.athenhub.productservice.product.application.service.strategy;

import com.athenhub.productservice.membership.domain.MemberRoles;
import com.athenhub.productservice.product.application.service.ProductQueryService;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * {@link com.athenhub.productservice.membership.domain.MemberRole#VENDOR_AGENT} 역할을 가진 사용자를 위한 상품
 * 조회 전략.
 *
 * <p>현재 사용자가 소속된 벤더({@code vendorId}) 기준으로 상품을 조회한다. 모든 조회 로직은 {@link ProductQueryService}에 위임되며, 이
 * 클래스는 조회 범위만 제한하는 역할을 수행한다.
 *
 * <p>{@link
 * com.athenhub.productservice.product.application.service.ProductQueryApplicationService}에서 {@link
 * #supports(MemberRoles)}를 통해 선택되어 실행된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Component
public class VendorAgentProductQueryStrategy implements ProductQueryStrategy {

  /** 상품 조회를 담당하는 도메인/애플리케이션 서비스. */
  private final ProductQueryService productQueryService;

  /**
   * 사용자가 소속된 벤더 기준으로 상품을 조회한다.
   *
   * @param member 사용자 소속 정보
   * @param pageable 페이징 정보
   * @return 해당 벤더에 속한 상품 목록
   * @author 김지원
   * @since 1.0.0
   */
  @Override
  public Page<Product> query(MemberInfo member, Pageable pageable) {
    return productQueryService.getByVendorId(member.vendorId(), pageable);
  }

  /**
   * 현재 전략이 VENDOR_AGENT 역할을 지원하는지 여부를 반환한다.
   *
   * @param roles 사용자 역할
   * @return VENDOR_AGENT 역할을 갖고 있으면 true
   * @author 김지원
   * @since 1.0.0
   */
  @Override
  public boolean supports(MemberRoles roles) {
    return roles.containsVendorAgent();
  }
}
