package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.PRODUCT_NOT_FOUND;

import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.repository.ProductRepository;
import com.athenhub.productservice.product.domain.vo.ProductId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
}
