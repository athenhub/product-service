package com.athenhub.productservice.product.domain.dto;

import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.VendorId;

/**
 * 회원의 소속 정보를 전달하기 위한 DTO.
 *
 * <p>Membership 서비스(또는 외부 인증/인가 시스템)로부터 조회된 사용자의 소속 Hub와 Vendor 정보를 담는다.
 *
 * <p>주로 상품 서비스에서 접근 권한 판단이나 조회 범위 제한 시 사용된다.
 *
 * <ul>
 *   <li>{@link HubId} : 사용자가 소속된 허브 ID
 *   <li>{@link VendorId} : 사용자가 소속된 벤더(업체) ID
 * </ul>
 *
 * <p>이 객체는 불변(Immutable)이며, 조회 결과 전달용으로만 사용된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public record MemberInfo(HubId hubId, VendorId vendorId) {}
