package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.PRODUCT_NOT_FOUND;

import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.SearchRequest;
import com.athenhub.productservice.product.domain.repository.ProductDetailRepository;
import com.athenhub.productservice.product.domain.repository.ProductRepository;
import com.athenhub.productservice.product.domain.vo.ProductId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
  public Product getProduct(UUID productId) {
    return productRepository
        .findById(ProductId.of(productId))
        .orElseThrow(() -> new ProductServiceException(PRODUCT_NOT_FOUND));
  }

  /**
   * 검색 조건에 따라 상품 목록을 조회한다.
   *
   * <p>전달받은 {@link SearchRequest}를 기반으로 동적 검색(Querydsl)을 수행하여 상품 목록을 페이징 형태로 조회한다.
   *
   * <p>요청 필드 중 {@code null} 값은 해당 조건을 적용하지 않고 조회된다.
   *
   * <p><b>적용 가능한 검색 조건</b>
   *
   * <ul>
   *   <li>상품명 (startsWith 기반 검색)
   *   <li>Hub 식별자
   *   <li>Vendor 식별자
   *   <li>최소 / 최대 가격 범위
   * </ul>
   *
   * <p><b>페이징 규칙</b>
   *
   * <ul>
   *   <li>{@code page}는 0부터 시작한다
   *   <li>{@code size}는 페이지당 조회할 데이터 개수이다
   * </ul>
   *
   * @param searchDto 상품 검색 조건
   * @param page 조회할 페이지 번호 (0부터 시작)
   * @param size 페이지당 조회할 데이터 개수
   * @return 검색 결과가 담긴 {@link Page}<{@link Product}> 객체
   */
  public Page<Product> searchProduct(SearchRequest searchDto, int page, int size) {
    return productDetailRepository.findAll(searchDto, page, size);
  }
}
