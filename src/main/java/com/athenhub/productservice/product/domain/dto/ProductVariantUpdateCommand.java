package com.athenhub.productservice.product.domain.dto;

import com.athenhub.productservice.product.domain.vo.ProductColor;
import com.athenhub.productservice.product.domain.vo.ProductSize;
import com.athenhub.productservice.product.domain.vo.ProductVariantId;

/**
 * 상품 옵션(ProductVariant)의 색상(Color) 및 사이즈(Size)를 수정하기 위한 도메인 명령(Command) 객체이다.
 *
 * <p>Controller 또는 Application 계층에서 전달된 수정 요청을 도메인 엔티티({@link
 * com.athenhub.productservice.product.domain.ProductVariant})가 이해할 수 있는 형태로 변환하여 전달한다.
 *
 * <h3>포함 정보</h3>
 *
 * <ul>
 *   <li>{@link ProductVariantId} : 수정 대상 옵션의 식별자
 *   <li>{@link ProductColor} : 변경할 색상 정보
 *   <li>{@link ProductSize} : 변경할 사이즈 정보
 * </ul>
 *
 * <p>해당 Command는 단순한 값 전달(Param Object)을 위한 객체이며, 옵션 상품 여부 검증, 동일 옵션 중복 여부 검증 등의 실제 도메인 규칙은 Product
 * Aggregate(Product) 또는 ProductVariant 내부에서 처리된다.
 *
 * <p>본 객체는 불변(Immutable) 구조로 설계되어, 생성 이후 상태가 변경될 수 없다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public record ProductVariantUpdateCommand(
    ProductVariantId productVariantId, ProductColor color, ProductSize size) {}
