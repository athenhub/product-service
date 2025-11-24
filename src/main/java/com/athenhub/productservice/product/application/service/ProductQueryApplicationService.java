package com.athenhub.productservice.product.application.service;

import com.athenhub.commoncore.error.GlobalErrorCode;
import com.athenhub.productservice.membership.domain.MemberRole;
import com.athenhub.productservice.membership.domain.MemberRoles;
import com.athenhub.productservice.product.application.dto.ProductSummary;
import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.application.service.strategy.ProductQueryStrategy;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.MemberInfo;
import com.athenhub.productservice.product.domain.service.MembershipProvider;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 사용자 역할(Role)에 따라 조회 가능한 상품 범위를 결정하는 <b>애플리케이션 계층의 조회 오케스트레이터</b>.
 *
 * <p>이 클래스는 직접 조회를 수행하지 않고, 역할에 맞는 {@link ProductQueryStrategy}를 선택하여 상품 조회를 위임한다.
 *
 * <h3>역할별 조회 범위</h3>
 *
 * <ul>
 *   <li>{@link MemberRole#MASTER_MANAGER} : 전체 상품
 *   <li>{@link MemberRole#HUB_MANAGER} : 소속 허브의 상품
 *   <li>{@link MemberRole#VENDOR_AGENT} : 소속 벤더의 상품
 * </ul>
 *
 * <p>소속 정보는 {@link MembershipProvider}를 통해 조회한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class ProductQueryApplicationService {

  /** 사용자 소속(허브, 벤더 등) 정보를 조회하는 Provider. */
  private final MembershipProvider membershipProvider;

  /** 역할별 상품 조회 전략 목록. */
  private final List<ProductQueryStrategy> productQueryStrategies;

  /**
   * 사용자가 관리할 수 있는 상품 목록을 조회한다.
   *
   * <p>사용자의 역할에 따라 적절한 {@link ProductQueryStrategy}가 선택되며, 해당 전략을 통해 상품 목록을 조회한다.
   *
   * @param userId 요청자 ID
   * @param memberRoles 요청자의 역할
   * @param pageable 페이징 정보
   * @return 사용자 권한 범위 내의 상품 목록
   * @throws ProductServiceException 지원되지 않는 역할이거나 권한이 없는 경우
   * @author 김지원
   * @since 1.0.0
   */
  public Page<ProductSummary> getProductsManagedBy(
      UUID userId, MemberRoles memberRoles, Pageable pageable) {

    MemberInfo member = membershipProvider.getMember(userId);
    Page<Product> products = getProductsByRole(memberRoles, pageable, member);

    return products.map(ProductSummary::from);
  }

  /**
   * 역할에 맞는 조회 전략을 찾아 상품을 조회한다.
   *
   * @param memberRoles 사용자 역할
   * @param pageable 페이징 정보
   * @param member 사용자 소속 정보
   * @return 조회된 상품 목록
   * @throws ProductServiceException 적절한 전략을 찾지 못한 경우
   * @author 김지원
   * @since 1.0.0
   */
  private Page<Product> getProductsByRole(
      MemberRoles memberRoles, Pageable pageable, MemberInfo member) {

    return productQueryStrategies.stream()
        .filter(strategy -> strategy.supports(memberRoles))
        .findFirst()
        .map(strategy -> strategy.query(member, pageable))
        .orElseThrow(() -> new ProductServiceException(GlobalErrorCode.FORBIDDEN));
  }
}
