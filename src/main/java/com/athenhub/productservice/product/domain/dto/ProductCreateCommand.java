package com.athenhub.productservice.product.domain.dto;

import com.athenhub.productservice.product.domain.ProductType;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.VendorId;

/**
 * 상품 생성을 위한 도메인 커맨드 객체.
 *
 * <p>Application Layer에서 전달받은 데이터를 도메인 계층에 전달하기 위한 불변(Immutable) DTO이며, {@link
 * com.athenhub.productservice.product.domain.Product} 생성 시 사용된다.
 *
 * <p>이 객체는 검증 로직을 포함하지 않으며, 모든 비즈니스 검증은 도메인 객체 내부에서 처리된다.
 *
 * @param name 상품 이름
 * @param description 상품 설명
 * @param price 상품 가격
 * @param hubId 상품이 소속된 허브 식별자
 * @param vendorId 상품을 등록한 업체 식별자
 * @param type 상품 유형 (예: 단일 상품 / 옵션 상품)
 * @author 김지원
 * @since 1.0.0
 */
public record ProductCreateCommand(
    String name,
    String description,
    Price price,
    HubId hubId,
    VendorId vendorId,
    ProductType type) {}
