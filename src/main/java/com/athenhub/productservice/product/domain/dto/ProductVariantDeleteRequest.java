package com.athenhub.productservice.product.domain.dto;

import java.util.UUID;

/**
 * 상품 옵션(Variant)을 삭제(Soft Delete)하기 위해 사용되는 도메인 커맨드 객체.
 *
 * <p>해당 커맨드는 {@code Product} Aggregate Root 내부의 옵션 삭제 로직에서 사용되며, 어떤 옵션을 삭제할지 결정하는 {@code
 * productVariantId}와 삭제 주체(사용자명)를 기록하기 위한 {@code username} 정보를 포함한다.
 *
 * <p>■ 용도 및 특징
 *
 * <ul>
 *   <li>옵션 삭제는 반드시 Product Aggregate Root를 통해 수행되므로, 이 커맨드는 옵션 삭제 요청을 위한 최소한의 도메인 데이터만을 전달한다.
 *   <li>{@code productVariantId}는 삭제 대상 옵션의 식별자로 사용된다.
 *   <li>{@code username}은 Soft Delete 감사(auditing)를 위해 사용되며, 실제 {@code ProductVariant} 엔티티 내부의
 *       delete() 메서드에서 삭제한 사용자로 기록된다.
 *   <li>Record(불변 객체)를 사용함으로써 데이터 변경에 대한 안정성과 무결성을 보장한다.
 * </ul>
 *
 * @param productVariantId 삭제할 ProductVariant ID(UUID)
 * @param username 삭제 요청을 수행한 사용자명
 * @author 김지원
 * @since 1.0.0
 */
public record ProductVariantDeleteRequest(UUID productVariantId, String username) {}
