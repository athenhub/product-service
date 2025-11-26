package com.athenhub.productservice.product.domain.repository;

import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.SearchDaoRequest;
import com.athenhub.productservice.product.domain.dto.SearchProductResponse;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.ProductVariantId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 상품 상세 조회를 위한 전용 레포지토리 인터페이스.
 *
 * <p>복잡한 검색 조건(상품명, 허브, 업체, 가격 범위 등)에 따라 {@link Product} 목록을 페이징 조회하기 위한 Querydsl 기반 조회 전용 포트이다.
 *
 * <p>본 인터페이스는 Domain 계층에 위치하며, 실제 구현체는 Infrastructure 계층에서 작성된다.
 *
 * <p>읽기 전용(Query) 용도로 사용되며, 쓰기 작업(Create/Update/Delete)은 {@link
 * com.athenhub.productservice.product.domain.repository.ProductRepository}를 통해 수행된다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public interface ProductDetailRepository {

  /**
   * 검색 조건에 맞는 상품 목록을 페이지 단위로 조회한다.
   *
   * <p>조건에 따라 동적으로 Querydsl WHERE 절이 구성되며, {@link Pageable}을 이용하여 페이징 처리된다.
   *
   * @param search 상품명, 허브 ID, 업체 ID, 가격 범위 등을 포함한 검색 조건
   * @param pageable 페이지 번호, 페이지 크기, 정렬 정보를 포함한 객체
   * @return 검색 결과에 해당하는 {@link Product}의 {@link Page}
   */
  Page<Product> search(SearchDaoRequest search, Pageable pageable);

  /**
   * 특정 허브에 속한 상품 목록을 페이지 단위로 조회한다.
   *
   * @param hubId 조회할 허브의 식별자
   * @param pageable 페이지 정보
   * @return 지정된 허브에 속한 {@link Product}의 {@link Page}
   */
  Page<Product> findByHubId(HubId hubId, Pageable pageable);

  /**
   * 특정 벤더(업체)에 속한 상품 목록을 페이지 단위로 조회한다.
   *
   * @param vendorId 조회할 벤더의 식별자
   * @param pageable 페이지 정보
   * @return 지정된 벤더에 속한 {@link Product}의 {@link Page}
   */
  Page<Product> findByVendorId(VendorId vendorId, Pageable pageable);

  /**
   * 모든 상품 목록을 페이지 단위로 조회한다.
   *
   * @param pageable 페이지 정보
   * @return {@link Product}의 {@link Page}
   */
  Page<Product> findAll(Pageable pageable);

  /**
   * 상품 옵션(Variant) ID 목록을 기반으로 상품 및 옵션 정보를 조회한다.
   *
   * <p>각 Variant와 연결된 Product 정보를 함께 조회하며, 전체 결과를 {@link SearchProductResponse} 목록으로 반환한다.
   *
   * @param variantIds 조회할 상품 옵션 ID 목록
   * @return 상품 및 옵션 정보 응답 리스트
   * @author 김지원
   * @since 1.0.0
   */
  List<SearchProductResponse> searchIn(List<ProductVariantId> variantIds);
}
