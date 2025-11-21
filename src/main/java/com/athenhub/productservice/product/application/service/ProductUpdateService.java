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
   * @param request 수정할 기본 정보 요청
   * @return 수정된 상품의 식별자 응답
   * @throws ProductServiceException 상품이 존재하지 않을 경우
   */
  public ProductResponse updateBasicInfo(ProductBasicUpdateRequest request) {
    Product product =
        productRepository
            .findById(ProductId.of(request.productId()))
            .orElseThrow(() -> new ProductServiceException(PRODUCT_NOT_FOUND));

    ProductBasicUpdateCommand basicUpdateCommand = request.toBasicUpdateCommand();
    product.updateBasic(basicUpdateCommand);
    return new ProductResponse(request.productId());
  }

  /**
   * 상품 옵션(Variant)을 수정한다.
   *
   * @param request 옵션 수정 요청
   * @param username 변경을 수행하는 사용자
   * @return 수정된 상품의 식별자 응답
   * @throws ProductServiceException 상품이 존재하지 않을 경우
   */
  public ProductResponse updateProductVariant(
      ProductVariantUpdateRequest request, String username) {
    Product product =
        productRepository
            .findById(ProductId.of(request.productId()))
            .orElseThrow(() -> new ProductServiceException(PRODUCT_NOT_FOUND));

    VariantUpdateSet updateSet = variantCommandMapper.toChangeSet(request, username);
    product.apply(updateSet);

    return new ProductResponse(request.productId());
  }
}
