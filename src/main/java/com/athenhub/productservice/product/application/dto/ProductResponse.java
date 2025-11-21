package com.athenhub.productservice.product.application.dto;

import java.util.UUID;

/**
 * 상품 처리 결과를 전달하는 응답 DTO.
 *
 * <p>생성·수정·삭제 후 반환되는 상품 식별자를 포함한다.
 */
public record ProductResponse(UUID productId) {}
