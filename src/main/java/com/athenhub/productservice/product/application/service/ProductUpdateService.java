package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.PRODUCT_NOT_FOUND;

import com.athenhub.productservice.product.application.dto.ProductBasicUpdateRequest;
import com.athenhub.productservice.product.application.dto.ProductResponse;
import com.athenhub.productservice.product.application.dto.ProductVariantUpdateRequest;
import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.application.mapper.VariantUpdateCommandMapper;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.ProductBasicUpdateCommand;
import com.athenhub.productservice.product.domain.dto.VariantUpdateSet;
import com.athenhub.productservice.product.domain.repository.ProductRepository;
import com.athenhub.productservice.product.domain.vo.ProductId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품의 기본 정보 및 옵션(Variant) 변경을 처리하는 서비스.
 *
 * <p>상품을 조회한 뒤 존재하지 않으면 예외를 발생시키며, 도메인에서 정의한 업데이트 규칙에 따라 변경 작업을 적용한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProductUpdateService {

  private final ProductRepository productRepository;
  private final VariantUpdateCommandMapper variantCommandMapper;

  /**
   * 상품의 기본 정보를 수정한다.
   *
   * @param productId 수정 대상 상품 ID
   * @param request 수정할 기본 정보 요청
   * @return 수정된 상품의 식별자 응답
   * @throws ProductServiceException 상품이 존재하지 않을 경우
   * @author 김지원
   * @since 1.0.0
   */
  public ProductResponse updateBasicInfo(UUID productId, ProductBasicUpdateRequest request) {
    Product product = findProduct(productId);

    ProductBasicUpdateCommand basicUpdateCommand = request.toBasicUpdateCommand();
    product.updateBasic(basicUpdateCommand);

    return new ProductResponse(product.getId().toUuid());
  }

  /**
   * 상품 옵션(Variant)을 수정한다.
   *
   * @param productId 수정 대상 상품 ID
   * @param request 옵션 수정 요청
   * @param username 변경을 수행하는 사용자
   * @return 수정된 상품의 식별자 응답
   * @throws ProductServiceException 상품이 존재하지 않을 경우
   * @author 김지원
   * @since 1.0.0
   */
  public ProductResponse updateProductVariant(
      UUID productId, ProductVariantUpdateRequest request, String username) {

    Product product = findProduct(productId);

    VariantUpdateSet updateSet = variantCommandMapper.toChangeSet(request, username);
    product.apply(updateSet);

    return new ProductResponse(product.getId().toUuid());
  }

  /**
   * 상품 상태를 판매중(ON_SALE)으로 변경한다.
   *
   * <p>실제 상태 변경 로직은 도메인(Product)에 위임한다.
   *
   * @param productId 상태를 변경할 상품 ID
   * @throws ProductServiceException 상품이 존재하지 않을 경우
   * @author 김지원
   * @since 1.0.0
   */
  public void updateToOnSale(UUID productId) {
    Product product = findProduct(productId);
    product.updateToOnSale();
  }

  /**
   * 상품을 조회한다.
   *
   * <p>주어진 식별자에 해당하는 상품이 존재하지 않으면 예외를 발생시킨다.
   *
   * @param productId 조회할 상품 ID
   * @return 조회된 상품 엔티티
   * @throws ProductServiceException 상품이 존재하지 않을 경우
   * @author 김지원
   * @since 1.0.0
   */
  private Product findProduct(UUID productId) {
    return productRepository
        .findById(ProductId.of(productId))
        .orElseThrow(() -> new ProductServiceException(PRODUCT_NOT_FOUND));
  }
}
