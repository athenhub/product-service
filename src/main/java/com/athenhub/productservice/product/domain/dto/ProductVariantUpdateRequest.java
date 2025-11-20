package com.athenhub.productservice.product.domain.dto;

import com.athenhub.productservice.product.domain.ProductColor;
import com.athenhub.productservice.product.domain.ProductSize;
import java.util.UUID;

/**
 * 상품 옵션(ProductVariant)의 속성(size, color)을 수정하기 위해 사용되는 도메인 커맨드 객체.
 *
 * <p>해당 커맨드는 {@code Product} Aggregate Root 내의 옵션 수정 로직에서 사용되며, 수정 대상 옵션을 식별하기 위한 {@code
 * productVariantId}와 변경하고자 하는 옵션 속성(사이즈/색상)을 전달한다.
 *
 * <p>■ 용도 및 역할
 *
 * <ul>
 *   <li>옵션 수정은 반드시 Product Aggregate Root를 통해 수행되므로, 수정에 필요한 최소 데이터(ID + 변경값)만 전달한다.
 *   <li>{@code productVariantId}는 어떤 옵션을 수정할지 결정하는 식별자 역할을 한다.
 *   <li>{@code size}, {@code color}는 변경 가능한 옵션 속성으로, ProductVariant 엔티티의 update() 메서드에서 적용된다.
 *   <li>옵션 중복(Color + Size) 검증은 Product 도메인 규칙에서 수행된다.
 *   <li>Record(불변 객체)를 사용하여 커맨드 자체의 변경 불가능성(Immutability)을 보장한다.
 * </ul>
 *
 * @param productVariantId 수정할 옵션 ID(UUID)
 * @param size 변경할 옵션의 사이즈({@link ProductSize})
 * @param color 변경할 옵션의 색상({@link ProductColor})
 * @author 김지원
 * @since 1.0.0
 */
public record ProductVariantUpdateRequest(
    UUID productVariantId, ProductSize size, ProductColor color) {}
