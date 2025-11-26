package com.athenhub.productservice.product.presentation.controller;

import com.athenhub.commonmvc.security.AuthenticatedUser;
import com.athenhub.productservice.membership.domain.MemberRole;
import com.athenhub.productservice.product.application.dto.ProductBasicUpdateRequest;
import com.athenhub.productservice.product.application.dto.ProductRegisterRequest;
import com.athenhub.productservice.product.application.dto.ProductResponse;
import com.athenhub.productservice.product.application.dto.ProductVariantUpdateRequest;
import com.athenhub.productservice.product.application.service.ProductCommandApplicationService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Product 도메인의 Command(변경) 요청을 처리하는 컨트롤러.
 *
 * <p>상품 등록, 수정, 삭제 등 상태를 변경하는 요청을 담당한다. 모든 비즈니스 로직은 {@link ProductCommandApplicationService}에 위임된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductCommandController {

  private final ProductCommandApplicationService commandApplicationService;

  /**
   * 상품 등록.
   *
   * <p><b>접근 권한</b>
   *
   * <ul>
   *   <li>{@link MemberRole#MASTER_MANAGER}
   *   <li>{@link MemberRole#HUB_MANAGER}
   *   <li>{@link MemberRole#VENDOR_AGENT}
   * </ul>
   *
   * @param request 상품 등록 요청 DTO
   * @param authenticatedUser 로그인한 사용자 정보
   * @return 등록된 상품 정보
   */
  @PreAuthorize("hasAnyRole('MASTER_MANAGER', 'HUB_MANAGER', 'VENDOR_AGENT')")
  @PostMapping
  public ProductResponse register(
      @Valid @RequestBody ProductRegisterRequest request,
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

    return commandApplicationService.register(request, authenticatedUser.id());
  }

  /**
   * 상품 기본 정보 수정.
   *
   * <p><b>접근 권한</b>
   *
   * <ul>
   *   <li>{@link MemberRole#MASTER_MANAGER}
   *   <li>{@link MemberRole#HUB_MANAGER}
   *   <li>{@link MemberRole#VENDOR_AGENT}
   * </ul>
   *
   * @param request 상품 수정 요청 DTO
   * @param authenticatedUser 로그인한 사용자 정보
   * @return 수정된 상품 정보
   */
  @PreAuthorize("hasAnyRole('MASTER_MANAGER', 'HUB_MANAGER', 'VENDOR_AGENT')")
  @PutMapping("/{productId}/basic")
  public ProductResponse updateBasicInfo(
      @PathVariable UUID productId,
      @RequestBody ProductBasicUpdateRequest request,
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

    return commandApplicationService.updateBasicInfo(productId, request, authenticatedUser.id());
  }

  /**
   * 상품 옵션(Variant) 수정.
   *
   * <p><b>접근 권한</b>
   *
   * <ul>
   *   <li>{@link MemberRole#MASTER_MANAGER}
   *   <li>{@link MemberRole#HUB_MANAGER}
   *   <li>{@link MemberRole#VENDOR_AGENT}
   * </ul>
   *
   * @param request 옵션 수정 요청
   * @param authenticatedUser 로그인한 사용자 정보
   * @return 수정된 상품 정보
   */
  @PreAuthorize("hasAnyRole('MASTER_MANAGER', 'HUB_MANAGER', 'VENDOR_AGENT')")
  @PutMapping("/{productId}/variants")
  public ProductResponse updateVariants(
      @PathVariable UUID productId,
      @RequestBody ProductVariantUpdateRequest request,
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

    return commandApplicationService.updateVariants(
        productId, request, authenticatedUser.id(), authenticatedUser.username());
  }

  /**
   * 상품 삭제(논리 삭제).
   *
   * <p><b>접근 권한</b>
   *
   * <ul>
   *   <li>{@link MemberRole#MASTER_MANAGER}
   *   <li>{@link MemberRole#HUB_MANAGER}
   * </ul>
   *
   * @param productId 상품 ID
   * @param authenticatedUser 로그인한 사용자 정보
   */
  @PreAuthorize("hasAnyRole('MASTER_MANAGER', 'HUB_MANAGER')")
  @DeleteMapping("/{productId}")
  public void delete(
      @PathVariable UUID productId, @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {

    commandApplicationService.delete(
        productId, authenticatedUser.id(), authenticatedUser.username());
  }
}
