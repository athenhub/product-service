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
 * {@link MemberRole#MASTER_MANAGER} 역할을 가진 사용자를 위한 상품
 * 조회 전략.
 *
 * <p>모든 상품에 대한 전체 조회 권한을 가지며, {@link ProductQueryService#getAll(Pageable)}를 통해 상품을 조회한다.
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
public class MasterManagerProductQueryStrategy implements ProductQueryStrategy {

  /** 전체 상품 조회를 담당하는 서비스. */
  private final ProductQueryService productQueryService;

  /**
   * 모든 상품을 조회한다.
   *
   * @param member 사용자 정보 (해당 전략에서는 조회 범위 산정에 사용되지 않음)
   * @param pageable 페이징 정보
   * @return 전체 상품 목록
   * @author 김지원
   * @since 1.0.0
   */
  @Override
  public Page<Product> query(MemberInfo member, Pageable pageable) {
    return productQueryService.getAll(pageable);
  }

  /**
   * 현재 전략이 MASTER_MANAGER 역할을 지원하는지 여부를 반환한다.
   *
   * @param roles 사용자 역할
   * @return MASTER_MANAGER 권한이 있으면 true
   * @author 김지원
   * @since 1.0.0
   */
  @Override
  public boolean supports(MemberRoles roles) {
    return roles.containsMasterManager();
  }
}
