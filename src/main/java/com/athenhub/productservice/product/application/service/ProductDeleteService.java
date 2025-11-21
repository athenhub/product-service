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
 * 상품 삭제를 처리하는 애플리케이션 서비스.
 *
 * <p>요청된 상품 ID로 {@link Product}를 조회한 후, 해당 상품이 존재하지 않으면 {@link ProductServiceException}을 발생시킨다.
 * 존재하는 경우 도메인 엔티티의 삭제 로직인 {@link Product#delete(String)}를 호출하여 상품을 논리적으로 삭제한다.
 *
 * <p>이 클래스는 비즈니스 흐름을 오케스트레이션하는 역할만 수행하며, 실제 삭제 처리(상태 변경, deletedBy 갱신 등)는 도메인에 위임한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProductDeleteService {

  private final ProductRepository productRepository;

  /**
   * 상품을 삭제(논리 삭제)한다.
   *
   * <p>상품이 존재하지 않을 경우 {@code PRODUCT_NOT_FOUND} 에러 코드와 함께 {@link ProductServiceException}을 발생시킨다.
   * 삭제 처리는 {@link Product#delete(String)} 도메인 메서드를 통해 수행된다.
   *
   * @param productId 삭제할 상품의 식별자(UUID)
   * @param username 삭제를 요청한 사용자명(감사 목적)
   * @throws ProductServiceException 상품이 존재하지 않을 경우
   */
  public void delete(UUID productId, String username) {
    Product product =
        productRepository
            .findById(ProductId.of(productId))
            .orElseThrow(() -> new ProductServiceException(PRODUCT_NOT_FOUND));

    product.delete(username);
  }
}
