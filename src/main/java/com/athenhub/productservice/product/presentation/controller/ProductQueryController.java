package com.athenhub.productservice.product.presentation.controller;

import com.athenhub.commonmvc.security.AuthenticatedUser;
import com.athenhub.productservice.membership.domain.MemberRole;
import com.athenhub.productservice.membership.domain.MemberRoles;
import com.athenhub.productservice.product.application.dto.ProductDetail;
import com.athenhub.productservice.product.application.dto.ProductSummary;
import com.athenhub.productservice.product.application.service.ProductQueryApplicationService;
import com.athenhub.productservice.product.application.service.ProductQueryService;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.SearchDaoRequest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 상품 조회(Query)를 담당하는 Controller.
 *
 * <p>상품에 대한 단건 조회, 검색 조회, 권한 기반 조회를 처리한다. 모든 조회 로직은 Application / Domain Service 계층을 통해 위임되며,
 * Controller는 요청 파라미터 매핑과 응답 DTO 변환만을 담당한다.
 *
 * <p>공통 특징:
 *
 * <ul>
 *   <li>조회 전용 컨트롤러 (쓰기/수정 기능 없음)
 *   <li>상품 Domain 직접 노출 방지 (DTO로 변환)
 *   <li>권한 기반 조회는 AuthenticatedUser 기반으로 처리
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductQueryController {

  /** 권한 기반 조회를 담당하는 Application Service. */
  private final ProductQueryApplicationService queryApplicationService;

  /** 단순 조회 / 검색을 담당하는 Query Service. */
  private final ProductQueryService productService;

  /**
   * 상품 단건 조회.
   *
   * <p>상품 ID를 기반으로 상품 상세 정보를 조회한다.
   *
   * @param productId 조회할 상품의 ID
   * @return 상품 상세 정보
   */
  @GetMapping("/{productId}")
  public ProductDetail get(@PathVariable UUID productId) {
    Product product = productService.get(productId);
    return ProductDetail.from(product);
  }

  /**
   * 상품 검색 조회.
   *
   * <p>검색 조건(SearchDaoRequest)과 페이지 정보(Pageable)를 기반으로 상품 목록을 조회한다.
   *
   * <p>인증/권한과 무관한 일반 검색용 API이다.
   *
   * @param request 검색 조건
   * @param pageable 페이징 정보
   * @return 검색된 상품 목록 (요약 정보)
   */
  @GetMapping("/search")
  public Page<ProductSummary> search(SearchDaoRequest request, Pageable pageable) {
    Page<Product> result = productService.search(request, pageable);
    return result.map(ProductSummary::from);
  }

  /**
   * 현재 로그인 사용자가 관리 가능한 상품 조회.
   *
   * <p>인증 객체({@link AuthenticatedUser})의 권한을 분석하여 다음 기준으로 상품을 조회한다.
   *
   * <ul>
   *   <li>{@link MemberRole#MASTER_MANAGER} → 전체 상품 조회
   *   <li>{@link MemberRole#HUB_MANAGER} → 소속 허브 상품 조회
   *   <li>{@link MemberRole#VENDOR_AGENT} → 소속 업체 상품 조회
   * </ul>
   *
   * <p>권한에 맞는 범위의 상품만 반환되며, 허용되지 않은 경우 예외가 발생한다.
   *
   * @param authenticatedUser 현재 로그인한 사용자 정보
   * @param pageable 페이지 정보
   * @return 사용자가 관리 가능한 상품 목록
   */
  @PreAuthorize("hasAnyRole('MASTER_MANAGER', 'HUB_MANAGER', 'VENDOR_AGENT')")
  @GetMapping("/managed")
  public Page<ProductSummary> managed(
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser, Pageable pageable) {

    List<MemberRole> memberRoleList =
        Stream.of(authenticatedUser.roles().split(","))
            .map(role -> role.replace("ROLE_", ""))
            .map(MemberRole::valueOf)
            .toList();

    return queryApplicationService.getProductsManagedBy(
        authenticatedUser.id(), MemberRoles.of(memberRoleList), pageable);
  }
}
