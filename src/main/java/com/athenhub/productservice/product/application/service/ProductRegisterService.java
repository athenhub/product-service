package com.athenhub.productservice.product.application.service;

import com.athenhub.productservice.product.application.dto.ProductRegisterRequest;
import com.athenhub.productservice.product.application.dto.ProductResponse;
import com.athenhub.productservice.product.application.mapper.ProductCreateCommandMapper;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantCreateCommand;
import com.athenhub.productservice.product.domain.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 등록을 담당하는 애플리케이션 서비스.
 *
 * <p>클라이언트로부터 전달받은 {@link ProductRegisterRequest}를 도메인 생성 커맨드({@link ProductCreateCommand}, {@link
 * ProductVariantCreateCommand})로 변환한 뒤, {@link Product}와 그에 속한 옵션(Variant)을 생성하고 저장한다.
 *
 * <p>이 클래스는 비즈니스 흐름을 조율하는 역할만 담당하며, 실제 생성 규칙과 검증은 {@link Product} 도메인에 위임한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Transactional
@Service
@RequiredArgsConstructor
public class ProductRegisterService {

  private final ProductRepository productRepository;
  private final ProductCreateCommandMapper createCommandMapper;

  /**
   * 상품을 등록한다.
   *
   * <p>요청 데이터를 도메인 커맨드로 변환한 뒤, 상품과 옵션을 생성하고 저장한다.
   *
   * @param request 상품 등록 요청 DTO
   * @return 생성된 상품의 식별자를 포함한 응답 객체
   */
  public ProductResponse register(ProductRegisterRequest request) {
    Product product = createProduct(request);
    addVariants(request, product);

    productRepository.save(product);

    return new ProductResponse(product.getId().toUuid());
  }

  /**
   * {@link ProductRegisterRequest}를 {@link ProductCreateCommand}로 변환한 뒤 {@link Product} 엔티티를 생성한다.
   *
   * @param request 상품 등록 요청 DTO
   * @return 생성된 {@link Product} 엔티티
   */
  private Product createProduct(ProductRegisterRequest request) {
    ProductCreateCommand productCommand = createCommandMapper.toCreateCommand(request);
    return Product.create(productCommand);
  }

  /**
   * 요청 정보로부터 상품 옵션 생성 커맨드를 만들고, 각 옵션을 {@link Product}에 추가한다.
   *
   * @param request 상품 등록 요청 DTO
   * @param product 옵션이 추가될 {@link Product} 엔티티
   */
  private void addVariants(ProductRegisterRequest request, Product product) {
    List<ProductVariantCreateCommand> variantCommands =
        createCommandMapper.toVariantCommands(request);

    variantCommands.forEach(product::addVariant);
  }
}
