package com.athenhub.productservice.product.domain.repository;

import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.SearchRequest;
import org.springframework.data.domain.Page;

/**
 * 상품 상세 조회를 위한 전용 레포지토리 인터페이스.
 *
 * <p>복잡한 검색 조건(상품명, 허브, 업체, 가격 범위 등)에 따라 {@link Product} 목록을 페이징 조회하기 위한 Querydsl 기반 조회 전용 포트이다.
 *
 * <p>구현체는 infrastructure 계층에서 작성되며 domain 계층에서는 해당 인터페이스에만 의존한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public interface ProductDetailRepository {

  /**
   * 검색 조건에 맞는 상품 목록을 페이지 단위로 조회한다.
   *
   * @param search 상품명, 허브 ID, 업체 ID, 가격 범위 등을 포함한 검색 조건
   * @param page 조회할 페이지 번호 (0부터 시작)
   * @param size 페이지당 데이터 건수
   * @return 검색 결과에 해당하는 {@link Product} 페이지 목록
   */
  Page<Product> findAll(SearchRequest search, int page, int size);
}
