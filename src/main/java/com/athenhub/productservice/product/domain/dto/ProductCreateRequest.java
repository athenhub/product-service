package com.athenhub.productservice.product.domain.dto;

import com.athenhub.productservice.product.domain.ProductType;
import java.util.UUID;

/**
 * 새로운 상품(Product)을 생성하기 위해 필요한 핵심 정보를 담는 도메인 커맨드 객체.
 *
 * <p>해당 커맨드는 애플리케이션 계층 또는 도메인 서비스에서 {@code Product} Aggregate Root 생성 시 사용되며, 상품이 갖추어야 할 최소한의 기본
 * 속성을 전달한다.
 *
 * <p>옵션(Variant) 정보는 포함하지 않으며, 옵션이 필요한 상품의 경우 이후 별도의 Variant 추가 흐름에서 처리한다. 이 커맨드는 상품 엔티티의 생성 책임을
 * 명확히 분리하기 위한 Command 패턴의 일환으로, 불변(immutable) 레코드 구조로 정의되어 데이터 무결성과 의도를 보장한다.
 *
 * @param hubId 상품이 속한 허브(Hub)의 식별자
 * @param vendorId 판매자(Vendor)의 식별자
 * @param price 상품 기본 가격. 0 이하가 되지 않도록 상위 계층에서 검증해야 한다.
 * @param type 상품 유형(ProductType). 옵션 지원 여부 등 도메인 규칙에 영향을 준다.
 * @author 김지원
 * @since 1.0.0
 */
public record ProductCreateRequest(UUID hubId, UUID vendorId, long price, ProductType type) {}
