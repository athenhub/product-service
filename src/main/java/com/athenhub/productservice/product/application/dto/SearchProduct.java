package com.athenhub.productservice.product.application.dto;

import java.util.List;
import java.util.UUID;

/**
 * 상품 옵션(Variant) ID 목록 기반 조회 요청 DTO이다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public record SearchProduct(List<UUID> variantIds) {}
