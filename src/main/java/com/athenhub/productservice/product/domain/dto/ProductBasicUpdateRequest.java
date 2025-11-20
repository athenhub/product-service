package com.athenhub.productservice.product.domain.dto;

import java.util.UUID;

/**
 * 상품의 기본 정보(허브, 판매자, 가격)를 변경하기 위한 도메인 커맨드 객체.
 *
 * <p>이 커맨드는 애플리케이션 계층 또는 도메인 서비스에서 {@code Product} Aggregate Root의 상태를 갱신할 때 사용된다. 상품의 옵션(Variant)
 * 정보 수정은 포함하지 않으며, 상품 자체의 메타 정보만 변경하는 용도로 제한된다.
 *
 * <p>Command 패턴을 통해 Product 엔티티가 필요한 변경값만 명확하게 전달할 수 있으며, 불변(immutable) 레코드 구조를 사용해 데이터 무결성을 보장한다.
 *
 * @param hubId 상품이 속한 허브(Hub)의 식별자
 * @param vendorId 판매자(Vendor)의 식별자
 * @param price 변경하려는 상품 가격. 값이 0 또는 음수가 되지 않도록 상위 계층에서 검증해야 한다.
 * @author 김지원
 * @since 1.0.0
 */
public record ProductBasicUpdateRequest(UUID hubId, UUID vendorId, long price) {}
