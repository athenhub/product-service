package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.PRODUCT_NOT_FOUND;

import com.athenhub.productservice.product.application.dto.SearchProductResponse;
import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.SearchDaoRequest;
import com.athenhub.productservice.product.domain.repository.ProductDetailRepository;
import com.athenhub.productservice.product.domain.repository.ProductRepository;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.ProductId;
import com.athenhub.productservice.product.domain.vo.ProductVariantId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 조회를 담당하는 애플리케이션 서비스.
 *
 * <p>상품 식별자({@link UUID})를 기반으로 {@link Product}를 조회하며, 존재하지 않는 경우 {@link ProductServiceException}을
 * 발생시킨다.
 *
 * <p>단순 조회 서비스를 목적으로 하며, 읽기 전용 트랜잭션으로 동작한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService {

  private final ProductRepository productRepository;
  private final ProductDetailRepository productDetailRepository;

  /**
   * 상품 단건을 조회한다.
   *
   * <p>조회 대상이 존재하지 않을 경우 {@code PRODUCT_NOT_FOUND} 에러 코드와 함께 {@link ProductServiceException}을
   * 발생시킨다.
   *
   * @param productId 조회할 상품의 UUID
   * @return 조회된 {@link Product} 엔티티
   * @throws ProductServiceException 상품이 존재하지 않을 경우
   */
  public Product get(UUID productId) {
    return productRepository
        .findById(ProductId.of(productId))
        .orElseThrow(() -> new ProductServiceException(PRODUCT_NOT_FOUND));
  }

  /**
   * 특정 허브(Hub)에 속한 상품 목록을 조회한다.
   *
   * <p>주로 {@code HUB_MANAGER} 권한을 가진 사용자의 조회 시 사용된다.
   *
   * @param hubId 조회할 허브 ID
   * @param pageable 페이지 정보
   * @return 해당 허브에 속한 {@link Product} 페이지
   */
  public Page<Product> getByHubId(HubId hubId, Pageable pageable) {
    return productDetailRepository.findByHubId(hubId, pageable);
  }

  /**
   * 특정 벤더(Vendor)에 속한 상품 목록을 조회한다.
   *
   * <p>주로 {@code VENDOR_AGENT} 권한을 가진 사용자의 조회 시 사용된다.
   *
   * @param vendorId 조회할 벤더 ID
   * @param pageable 페이지 정보
   * @return 해당 벤더에 속한 {@link Product} 페이지
   */
  public Page<Product> getByVendorId(VendorId vendorId, Pageable pageable) {
    return productDetailRepository.findByVendorId(vendorId, pageable);
  }

  /**
   * 전체 상품 목록을 조회한다.
   *
   * <p>주로 {@code MASTER_MANAGER} 권한을 가진 사용자가 사용하는 조회 기능이다.
   *
   * @param pageable 페이지 정보
   * @return 전체 {@link Product} 페이지
   */
  public Page<Product> getAll(Pageable pageable) {
    return productDetailRepository.findAll(pageable);
  }

  /**
   * 조건 기반으로 상품 목록을 검색한다.
   *
   * <p>{@link SearchDaoRequest}에 전달된 조건에 따라 상품을 조회하며, 페이지네이션 정보({@link Pageable})를 적용하여 결과를 반환한다.
   *
   * <p>실제 조회 로직은 {@link ProductDetailRepository#search(SearchDaoRequest, Pageable)} 에 위임된다.
   *
   * @param request 검색 조건 DTO (상품명, 허브, 벤더, 가격 범위 등)
   * @param pageable 페이지 정보
   * @return 조건에 맞는 상품 목록(Page)
   */
  public Page<Product> search(SearchDaoRequest request, Pageable pageable) {
    return productDetailRepository.search(request, pageable);
  }

  /**
   * 상품 옵션(Variant) ID 목록을 기반으로 상품 정보를 조회한다.
   *
   * <p>전달된 UUID 리스트를 도메인 객체 {@link ProductVariantId}로 변환한 뒤, Repository를 통해 검색 결과를 {@link
   * SearchProductResponse} 목록으로 조회한다.
   *
   * <ul>
   *   <li>variantIds가 {@code null}이거나 비어있으면 빈 리스트를 반환한다.
   *   <li>UUID → {@link ProductVariantId} 변환은 내부 메서드에서 처리한다.
   * </ul>
   *
   * @author 김지원
   * @since 1.0.0
   */
  public List<SearchProductResponse> getProductsBy(List<UUID> variantIds) {
    if (isEmpty(variantIds)) {
      return List.of();
    }

    List<ProductVariantId> productVariantIds = getVariantIds(variantIds);

    return productDetailRepository.searchIn(productVariantIds);
  }

  /**
   * UUID 목록을 {@link ProductVariantId} 도메인 객체 목록으로 변환한다.
   *
   * @param variantIds 변환할 상품 옵션 UUID 목록
   * @return 변환된 {@link ProductVariantId} 목록
   * @author 김지원
   * @since 1.0.0
   */
  private List<ProductVariantId> getVariantIds(List<UUID> variantIds) {
    return variantIds.stream().map(ProductVariantId::of).toList();
  }

  /**
   * 전달된 variantIds가 비어 있는지 검사한다.
   *
   * <p>{@code null} 이거나 {@link List#isEmpty()} 인 경우 {@code true}를 반환한다.
   *
   * @param variantIds 검사할 UUID 목록
   * @return 비어있으면 {@code true}
   * @author 김지원
   * @since 1.0.0
   */
  private boolean isEmpty(List<UUID> variantIds) {
    return variantIds == null || variantIds.isEmpty();
  }
}
