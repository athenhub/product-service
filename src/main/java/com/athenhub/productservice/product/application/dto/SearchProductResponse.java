package com.athenhub.productservice.product.application.dto;

import java.util.UUID;

/**
 * 상품 옵션(Variant) 기반 조회 결과 응답 DTO이다.
 *
 * <p>각 상품의 기본 정보와 선택된 옵션(variant) 정보, 가격을 함께 제공한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public record SearchProductResponse(
    UUID productId, String name, String variant, UUID variantId, long price) {}
