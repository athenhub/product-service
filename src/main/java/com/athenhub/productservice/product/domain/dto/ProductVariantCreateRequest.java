package com.athenhub.productservice.product.domain.dto;

import com.athenhub.productservice.product.domain.ProductColor;
import com.athenhub.productservice.product.domain.ProductSize;

/**
 * 상품 옵션(ProductVariant)을 생성하기 위해 사용되는 도메인 커맨드 객체.
 *
 * <p>옵션 생성은 반드시 {@code Product} Aggregate Root를 통해 이루어지므로, 이 커맨드는 옵션 생성에 필요한 최소한의 값(색상, 사이즈)만을
 * 전달한다.
 *
 * <p>■ 용도 및 특징
 *
 * <ul>
 *   <li>색상({@link ProductColor})과 사이즈({@link ProductSize}) 정보를 전달하여 새로운 옵션을 생성하는 데 사용된다.
 *   <li>ID는 Aggregate Root(Product)가 생성 시점에 직접 할당하므로 커맨드에는 포함하지 않는다.
 *   <li>옵션 중복(Color + Size) 검증은 {@code Product} 도메인 규칙에서 수행된다.
 *   <li>Record 타입을 사용하여 불변성을 보장하고 값 전달 객체(Value Object) 역할을 명확히 한다.
 * </ul>
 *
 * @param color 생성할 옵션의 색상
 * @param size 생성할 옵션의 사이즈
 * @author 김지원
 * @since 1.0.0
 */
public record ProductVariantCreateRequest(ProductColor color, ProductSize size) {}
