package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.PRODUCT_NOT_FOUND;

import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.repository.ProductDetailRepository;
import com.athenhub.productservice.product.domain.repository.ProductRepository;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.ProductId;
import com.athenhub.productservice.product.domain.vo.VendorId;
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
}
