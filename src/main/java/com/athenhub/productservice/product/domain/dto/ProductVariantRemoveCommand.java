package com.athenhub.productservice.product.domain.dto;

import com.athenhub.productservice.product.domain.vo.ProductVariantId;

/**
 * 상품 옵션(ProductVariant) 삭제(Soft Delete)를 수행하기 위한 도메인 명령(Command) 객체이다.
 *
 * <p>옵션 삭제 요청 시 필요한 식별자({@link ProductVariantId})와 삭제 수행 주체(사용자명 또는 시스템 계정)를 캡슐화하여 도메인 엔티티({@link
 * com.athenhub.productservice.product.domain.ProductVariant})로 전달하는 역할을 한다.
 *
 * <h3>포함 정보</h3>
 *
 * <ul>
 *   <li>{@link ProductVariantId} : 삭제 대상 옵션의 식별자
 *   <li>{@code username} : 삭제(Soft Delete)를 수행한 사용자명
 * </ul>
 *
 * <p>해당 Command는 단순한 데이터 전달(Param Object)을 위해 존재하며, 실제 삭제 규칙(이미 삭제된 옵션 여부, 옵션 상품 여부 등)은 Product
 * Aggregate(Product) 및 ProductVariant 내부의 도메인 로직에서 처리된다.
 *
 * <p>본 객체는 불변(Immutable) 구조로 생성 이후 상태가 변경되지 않는다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public record ProductVariantRemoveCommand(ProductVariantId productVariantId, String username) {}
