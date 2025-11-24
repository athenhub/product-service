package com.athenhub.productservice.product.application.service.strategy;

import com.athenhub.productservice.membership.domain.MemberRoles;
import com.athenhub.productservice.product.application.service.ProductQueryApplicationService;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.MemberInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 사용자 역할(Role)에 따라 서로 다른 상품 조회 방식을 제공하는 전략 인터페이스.
 *
 * <p>각 구현체는 특정 역할에 대한 조회 로직을 담당하며, {@link #supports(MemberRoles)}를 통해 해당 역할을 처리 가능한지 판단한다.
 *
 * <p>{@link ProductQueryApplicationService}에서 이 전략들을 순회하며, 현재 사용자 역할에 맞는 구현체를 선택해 실행한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public interface ProductQueryStrategy {

  /**
   * 주어진 사용자 정보에 대해 상품 목록을 조회한다.
   *
   * @param member 사용자 소속 정보
   * @param pageable 페이징 정보
   * @return 조회된 상품 목록
   * @author 김지원
   * @since 1.0.0
   */
  Page<Product> query(MemberInfo member, Pageable pageable);

  /**
   * 해당 전략이 주어진 역할을 지원하는지 여부를 반환한다.
   *
   * @param roles 사용자 역할 정보
   * @return 지원하면 true, 아니면 false
   * @author 김지원
   * @since 1.0.0
   */
  boolean supports(MemberRoles roles);
}
