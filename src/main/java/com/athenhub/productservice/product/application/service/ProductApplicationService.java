package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.CREATE_NOT_ALLOWED;
import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.DELETE_NOT_ALLOWED;
import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.UPDATE_NOT_ALLOWED;

import com.athenhub.productservice.product.application.dto.ProductBasicUpdateRequest;
import com.athenhub.productservice.product.application.dto.ProductRegisterRequest;
import com.athenhub.productservice.product.application.dto.ProductResponse;
import com.athenhub.productservice.product.application.dto.ProductVariantUpdateRequest;
import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.service.PermissionPolicy;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Product 도메인의 <b>애플리케이션 계층(Application Layer)</b>을 담당하는 서비스이다.
 *
 * <p>본 클래스는 다음 책임을 수행한다:
 *
 * <ul>
 *   <li><b>유스케이스 실행</b>: 상품 등록, 수정, 삭제 등 Product 도메인의 주요 비즈니스 흐름을 조합한다.
 *   <li><b>권한 검증</b>: PermissionPolicy를 통해 허브/벤더 소속 여부 및 액션 허용 여부를 검사한다.
 *   <li><b>도메인 서비스 호출</b>: 등록/수정/삭제 로직은 각각의 ProductRegisterService, ProductUpdateService,
 *       ProductDeleteService에 위임한다.
 *   <li><b>트랜잭션 경계 분리</b>: 트랜잭션은 Register/Update/Delete 각 서비스 내부에서 관리되며, ApplicationService는 트랜잭션
 *       오케스트레이션만 수행한다.
 *   <li><b>조회 분리</b>: ProductQueryService를 사용해 읽기 전용 조회를 수행하고, 이 결과를 기반으로 권한을 검증한다.
 * </ul>
 *
 * <p>ApplicationService는 도메인 로직을 직접 구현하지 않으며, 도메인 모델과 외부 시스템 간의 조정자(Coordinator) 역할을 수행한다. 이는 DDD의
 * 애플리케이션 계층 설계와 일치한다.
 */
@Service
@RequiredArgsConstructor
public class ProductApplicationService {

  private final ProductRegisterService productRegisterService;
  private final ProductUpdateService productUpdateService;
  private final ProductDeleteService productDeleteService;
  private final ProductQueryService productQueryService;

  private final PermissionPolicy permissionPolicy;

  /**
   * 상품 등록 유스케이스를 실행한다.
   *
   * <p>등록 요청에 포함된 hubId, vendorId가 요청자의 권한과 일치하는지 PermissionPolicy로 검증한 후, ProductRegisterService에
   * 등록 작업을 위임한다.
   *
   * @param request 상품 등록 요청 DTO
   * @param requestId 요청자의 인증 정보(UUID)
   * @return 생성된 상품 정보
   * @throws ProductServiceException 권한이 없는 경우
   */
  public ProductResponse register(ProductRegisterRequest request, UUID requestId) {
    if (permissionPolicy.isCreateDenied(
        requestId, HubId.of(request.hubId()), VendorId.of(request.vendorId()))) {

      throw new ProductServiceException(CREATE_NOT_ALLOWED);
    }
    return productRegisterService.register(request);
  }

  /**
   * 상품의 기본 정보를 수정하는 유스케이스를 실행한다.
   *
   * <p>1) 상품 조회 (ProductQueryService) 2) 권한 체크 (PermissionPolicy) 3) 도메인 업데이트 실행
   * (ProductUpdateService)
   *
   * @param request 수정 요청 DTO
   * @param requestId 요청자 인증 정보(UUID)
   * @return 수정된 상품 정보
   * @throws ProductServiceException 상품 수정 권한이 없는 경우
   */
  public ProductResponse updateBasicInfo(ProductBasicUpdateRequest request, UUID requestId) {
    Product product = productQueryService.getProduct(request.productId());

    if (permissionPolicy.isUpdateDenied(requestId, product.getHubId(), product.getVendorId())) {
      throw new ProductServiceException(UPDATE_NOT_ALLOWED);
    }

    return productUpdateService.updateBasicInfo(request);
  }

  /**
   * 상품 옵션(Variant)을 수정하는 유스케이스를 실행한다.
   *
   * <p>상품 기본 정보 수정과 동일한 흐름으로, Variant 수정만 별도의 도메인 서비스에 위임한다.
   *
   * @param request 옵션 수정 요청 DTO
   * @param requestId 요청자 인증 정보(UUID)
   * @param username 수정 작업을 수행하는 사용자명(감사 로그용)
   * @return 수정된 상품 정보
   * @throws ProductServiceException 수정 권한이 없는 경우
   */
  public ProductResponse updateVariants(
      ProductVariantUpdateRequest request, UUID requestId, String username) {
    Product product = productQueryService.getProduct(request.productId());

    if (permissionPolicy.isUpdateDenied(requestId, product.getHubId(), product.getVendorId())) {
      throw new ProductServiceException(UPDATE_NOT_ALLOWED);
    }

    return productUpdateService.updateProductVariant(request, username);
  }

  /**
   * 상품 삭제 유스케이스를 실행한다.
   *
   * <p>상품 삭제는 다음 순서로 진행된다.
   *
   * <ol>
   *   <li>상품 조회(ProductQueryService)
   *   <li>권한 체크(PermissionPolicy)
   *   <li>삭제 작업 위임(ProductDeleteService)
   * </ol>
   *
   * <p>ApplicationService는 삭제 비즈니스 규칙을 직접 수행하지 않으며, 단지 흐름을 조립하는 역할만 한다.
   *
   * @param productId 삭제할 상품 ID
   * @param requestId 요청자 인증 정보(UUID)
   * @param username 삭제 작업자명
   * @throws ProductServiceException 삭제 권한이 없는 경우
   */
  public void delete(UUID productId, UUID requestId, String username) {
    Product product = productQueryService.getProduct(productId);

    if (permissionPolicy.isDeleteDenied(requestId, product.getHubId())) {
      throw new ProductServiceException(DELETE_NOT_ALLOWED);
    }

    productDeleteService.delete(productId, username);
  }
}
