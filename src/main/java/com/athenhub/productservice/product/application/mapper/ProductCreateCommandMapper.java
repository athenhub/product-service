package com.athenhub.productservice.product.application.mapper;

import com.athenhub.productservice.product.application.dto.ProductRegisterRequest;
import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantCreateCommand;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.ProductColor;
import com.athenhub.productservice.product.domain.vo.ProductSize;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 상품 등록 요청 DTO를 도메인 생성 명령 객체로 변환하는 매퍼.
 *
 * <p>Application Layer → Domain Layer 간 변환만 담당하며, 어떠한 도메인 로직도 포함하지 않는다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
public class ProductCreateCommandMapper {

  /** 상품 기본 정보 생성 명령으로 변환한다. */
  public ProductCreateCommand toCreateCommand(ProductRegisterRequest req) {
    return new ProductCreateCommand(
        HubId.of(req.hubId()), VendorId.of(req.vendorId()), Price.of(req.price()), req.type());
  }

  /** 옵션 등록 요청을 옵션 생성 명령 리스트로 변환한다. */
  public List<ProductVariantCreateCommand> toVariantCommands(ProductRegisterRequest req) {
    return req.productVariants().stream()
        .map(
            v ->
                new ProductVariantCreateCommand(
                    ProductColor.of(v.color()), ProductSize.of(v.size())))
        .toList();
  }
}
