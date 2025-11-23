package com.athenhub.productservice.product.application.service;

import com.athenhub.commoncore.error.GlobalErrorCode;
import com.athenhub.productservice.membership.domain.MemberRole;
import com.athenhub.productservice.membership.domain.MemberRoles;
import com.athenhub.productservice.product.application.dto.ProductSummary;
import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.MemberInfo;
import com.athenhub.productservice.product.domain.service.MembershipProvider;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 상품 조회에 대한 <b>권한 기반 비즈니스 로직</b>을 담당하는 애플리케이션 서비스.
 *
 * <p>이 클래스는 단순 조회(Query)가 아닌, <b>사용자의 역할(Role)에 따라 조회 가능한 상품 범위를 제한</b>하는 정책을 포함한다.
 *
 * <p>실제 데이터 조회는 {@link ProductQueryService}에 위임되며, 이 클래스는 다음 역할만 수행한다:
 *
 * <ul>
 *   <li>현재 사용자의 소속 정보 조회 ({@link MembershipProvider})
 *   <li>사용자 역할({@link MemberRoles})에 따른 조회 범위 결정
 *   <li>조회 결과를 외부 응답용 {@link ProductSummary}로 변환
 * </ul>
 *
 * <p><b>역할별 조회 규칙</b>
 *
 * <ul>
 *   <li>{@link MemberRole#MASTER_MANAGER} : 전체 상품 조회
 *   <li>{@link MemberRole#HUB_MANAGER} : 소속 허브의 상품 조회
 *   <li>{@link MemberRole#VENDOR_AGENT} : 소속 업체(벤더)의 상품 조회
 * </ul>
 *
 * <p>위 역할에 해당하지 않는 경우 {@link ProductServiceException}이 발생한다.
 *
 * <p>✅ Application Layer 에 위치하며, 도메인 규칙을 직접 다루지 않고 조회 흐름과 권한 정책만을 오케스트레이션한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class ProductQueryApplicationService {

  private final MembershipProvider membershipProvider;
  private final ProductQueryService queryService;

  /**
   * 사용자가 관리할 수 있는 상품 목록을 조회한다.
   *
   * <p>사용자의 역할(Role)에 따라 조회 범위가 달라진다.
   *
   * <ul>
   *   <li>{@link MemberRole#MASTER_MANAGER} → 전체 상품 조회
   *   <li>{@link MemberRole#HUB_MANAGER} → 소속 허브의 상품 조회
   *   <li>{@link MemberRole#VENDOR_AGENT} → 소속 벤더의 상품 조회
   * </ul>
   *
   * <p>소속 정보는 {@link MembershipProvider}를 통해 조회하며, 조회 결과는 {@link ProductSummary}로 변환되어 반환된다.
   *
   * @param userId 요청자의 사용자 ID
   * @param memberRoles 요청자의 역할 목록
   * @param pageable 페이지 정보
   * @return 사용자에게 허용된 범위의 상품 목록
   * @throws ProductServiceException 권한이 없는 경우
   */
  public Page<ProductSummary> getProductsManagedBy(
      UUID userId, MemberRoles memberRoles, Pageable pageable) {
    MemberInfo member = membershipProvider.getMember(userId);

    Page<Product> result;

    if (memberRoles.containsMasterManager()) {
      result = queryService.getAll(pageable);

    } else if (memberRoles.containsHubManager()) {
      result = queryService.getByHubId(member.hubId(), pageable);

    } else if (memberRoles.containsVendorAgent()) {
      result = queryService.getByVendorId(member.vendorId(), pageable);

    } else {
      throw new ProductServiceException(GlobalErrorCode.FORBIDDEN);
    }

    return result.map(ProductSummary::from);
  }
}
