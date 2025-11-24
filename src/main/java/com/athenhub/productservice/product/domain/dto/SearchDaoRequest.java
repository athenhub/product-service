package com.athenhub.productservice.product.domain.dto;

import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.VendorId;

/**
 * 상품 검색 조건을 전달하기 위한 요청 DTO.
 *
 * <p>상품 목록 조회 시 다양한 필터 조건(name, hubId, vendorId, 가격 범위)을 조합하여 동적 검색을 수행하기 위해 사용된다. 모든 필드는 선택적으로 사용할
 * 수 있으며, null인 경우 해당 조건은 검색에 반영되지 않는다.
 *
 * <p><b>필드 설명</b>
 *
 * <ul>
 *   <li>{@code name} : 상품명 (부분 일치 / startsWith 검색)
 *   <li>{@code hubId} : 허브 식별자
 *   <li>{@code vendorId} : 업체(벤더) 식별자
 *   <li>{@code minPrice} : 최소 가격
 *   <li>{@code maxPrice} : 최대 가격
 * </ul>
 *
 * <p><b>사용 예</b>
 *
 * <pre>
 * SearchRequest request =
 *     new SearchRequest("나이키", null, null, 10000L, 30000L);
 * </pre>
 *
 * @author 김지원
 * @since 1.0.0
 */
public record SearchDaoRequest(
    String name, HubId hubId, VendorId vendorId, Long minPrice, Long maxPrice) {}
