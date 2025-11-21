package com.athenhub.productservice.product.application.mapper;

import com.athenhub.productservice.product.application.dto.ProductVariantUpdateRequest;
import com.athenhub.productservice.product.domain.dto.ProductVariantCreateCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantRemoveCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantUpdateCommand;
import com.athenhub.productservice.product.domain.dto.VariantUpdateSet;
import com.athenhub.productservice.product.domain.vo.ProductColor;
import com.athenhub.productservice.product.domain.vo.ProductSize;
import com.athenhub.productservice.product.domain.vo.ProductVariantId;
import org.springframework.stereotype.Component;

/**
 * 옵션 변경 요청 DTO를 도메인 변경 명령 집합(VariantUpdateSet)으로 변환하는 매퍼.
 *
 * <p>추가·수정·삭제 요청을 각각 도메인 명령 객체로 매핑하여 Aggregate Root가 일괄 처리할 수 있는 형태로 변환한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
public class VariantUpdateCommandMapper {

  /** 옵션 변경 요청을 VariantUpdateSet으로 변환한다. */
  public VariantUpdateSet toChangeSet(ProductVariantUpdateRequest request, String username) {
    return new VariantUpdateSet(
        request.adds().stream()
            .map(
                it ->
                    new ProductVariantCreateCommand(
                        ProductColor.of(it.color()), ProductSize.of(it.size())))
            .toList(),
        request.updates().stream()
            .map(
                it ->
                    new ProductVariantUpdateCommand(
                        ProductVariantId.of(it.variantId()),
                        ProductColor.of(it.color()),
                        ProductSize.of(it.size())))
            .toList(),
        request.removes().stream()
            .map(
                it ->
                    new ProductVariantRemoveCommand(ProductVariantId.of(it.variantId()), username))
            .toList());
  }
}
