package com.athenhub.productservice.product.infrastructure;

import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.QProduct;
import com.athenhub.productservice.product.domain.dto.SearchDto;
import com.athenhub.productservice.product.domain.repository.ProductDetailRepository;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

/**
 * 상품 상세 조회를 위한 Querydsl 기반 DAO 구현체.
 *
 * <p>다음 조건에 따라 동적으로 조회 조건을 구성한다.
 *
 * <ul>
 *   <li>상품명 (startsWith, 대소문자 무시)
 *   <li>최소 가격 이상 (>=)
 *   <li>최대 가격 이하 (<=)
 *   <li>허브 ID
 *   <li>벤더 ID
 *   <li>삭제되지 않은 상품만 조회 (deletedAt IS NULL)
 * </ul>
 *
 * <p>페이지 번호는 1부터 시작하며, 내부적으로 0 기반 index로 변환된다.
 *
 * <p>조회 결과와 전체 개수를 분리하여 {@link org.springframework.data.domain.Page} 객체로 반환한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class ProductDetailsDao implements ProductDetailRepository {

  private final JPAQueryFactory queryFactory;

  /**
   * 검색 조건과 페이지 정보를 기반으로 상품 목록을 조회한다.
   *
   * @param search 검색 조건 DTO
   * @param page 요청 페이지 (1 이상)
   * @param size 페이지 크기 (1 이상, 기본값 20)
   * @return Page 형태의 상품 목록
   */
  @Override
  public Page<Product> findAll(SearchDto search, int page, int size) {
    QProduct product = QProduct.product;

    int safePage = Math.max(page, 1);
    int safeSize = size < 1 ? 20 : size;
    int offset = (safePage - 1) * safeSize;

    List<Product> results =
        queryFactory
            .select(product)
            .from(product)
            .leftJoin(product.variants.values)
            .fetchJoin()
            .where(
                nameStartWith(search.name()),
                minPriceGoe(search.minPrice()),
                maxPriceLoe(search.maxPrice()),
                hubIdEq(search.hubId()),
                vendorIdEq(search.vendorId()),
                notDeleted())
            .offset(offset)
            .limit(safeSize)
            .orderBy(product.createdAt.desc())
            .fetch();

    Long total =
        queryFactory
            .select(product.count())
            .from(product)
            .where(
                nameStartWith(search.name()),
                minPriceGoe(search.minPrice()),
                maxPriceLoe(search.maxPrice()),
                hubIdEq(search.hubId()),
                vendorIdEq(search.vendorId()),
                notDeleted())
            .fetchOne();

    return new PageImpl<>(
        results, PageRequest.of(safePage - 1, safeSize), total == null ? 0 : total);
  }

  /** 상품명 시작 문자열 조건 (대소문자 무시) */
  private Predicate nameStartWith(String name) {
    if (!StringUtils.hasText(name)) {
      return null;
    }
    QProduct product = QProduct.product;
    return product.name.startsWithIgnoreCase(name);
  }

  /** 최소 가격 이상 조건 (>=) */
  private Predicate minPriceGoe(Long price) {
    if (price == null || price <= 0) {
      return null;
    }
    QProduct product = QProduct.product;
    return product.price.amount.goe(price);
  }

  /** 최대 가격 이하 조건 (<=) */
  private Predicate maxPriceLoe(Long price) {
    if (price == null || price <= 0) {
      return null;
    }
    QProduct product = QProduct.product;
    return product.price.amount.loe(price);
  }

  /** 허브 ID 조건 */
  private Predicate hubIdEq(UUID hubId) {
    if (hubId == null) {
      return null;
    }
    QProduct product = QProduct.product;
    return product.hubId.eq(HubId.of(hubId));
  }

  /** 벤더 ID 조건 */
  private Predicate vendorIdEq(UUID vendorId) {
    if (vendorId == null) {
      return null;
    }
    QProduct product = QProduct.product;
    return product.vendorId.eq(VendorId.of(vendorId));
  }

  /** 논리 삭제되지 않은 상품만 조회 */
  private Predicate notDeleted() {
    QProduct product = QProduct.product;
    return product.deletedAt.isNull();
  }
}
