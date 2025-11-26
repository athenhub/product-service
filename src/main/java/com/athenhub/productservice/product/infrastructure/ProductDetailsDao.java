package com.athenhub.productservice.product.infrastructure;

import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductStatus;
import com.athenhub.productservice.product.domain.QProduct;
import com.athenhub.productservice.product.domain.QProductVariant;
import com.athenhub.productservice.product.domain.dto.SearchDaoRequest;
import com.athenhub.productservice.product.domain.dto.SearchProductResponse;
import com.athenhub.productservice.product.domain.repository.ProductDetailRepository;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.ProductVariantId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 상품 상세 조회를 위한 Querydsl 기반 DAO 구현체.
 *
 * <p>{@link ProductDetailRepository}를 구현하며, 다양한 검색 조건(xxx, 허브, 벤더, 가격 범위 등)에 따라 {@link Product} 목록을
 * 동적으로 페이징 조회한다.
 *
 * <p>페이징 안정성을 위해 컬렉션 {@code fetch join}은 사용하지 않으며, 필요 시 DTO projection으로 대체한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class ProductDetailsDao implements ProductDetailRepository {

  private final JPAQueryFactory queryFactory;

  /**
   * 검색 조건 기반 조회.
   *
   * @param search 검색 조건 DTO
   * @param pageable 페이지 정보
   * @return 조건에 해당하는 상품 페이지
   * @author 김지원
   * @since 1.0.0
   */
  @Override
  public Page<Product> search(SearchDaoRequest search, Pageable pageable) {
    return executeQuery(
        hubIdEq(search.hubId()),
        vendorIdEq(search.vendorId()),
        nameStartWith(search.name()),
        minPriceGoe(search.minPrice()),
        maxPriceLoe(search.maxPrice()),
        onSale(),
        pageable);
  }

  /**
   * 특정 허브에 속한 상품 조회.
   *
   * @param hubId 허브 ID
   * @param pageable 페이지 정보
   * @return 허브에 속한 상품 페이지
   * @author 김지원
   * @since 1.0.0
   */
  @Override
  public Page<Product> findByHubId(HubId hubId, Pageable pageable) {
    return executeQuery(hubIdEq(hubId), null, null, null, null, null, pageable);
  }

  /**
   * 특정 벤더에 속한 상품 조회.
   *
   * @param vendorId 벤더 ID
   * @param pageable 페이지 정보
   * @return 벤더에 속한 상품 페이지
   * @author 김지원
   * @since 1.0.0
   */
  @Override
  public Page<Product> findByVendorId(VendorId vendorId, Pageable pageable) {
    return executeQuery(null, vendorIdEq(vendorId), null, null, null, null, pageable);
  }

  /**
   * 전체 상품 조회.
   *
   * @param pageable 페이지 정보
   * @return 전체 상품 페이지
   * @author 김지원
   * @since 1.0.0
   */
  @Override
  public Page<Product> findAll(Pageable pageable) {
    return executeQuery(null, null, null, null, null, null, pageable);
  }

  /**
   * 전달된 상품 옵션(Variant) ID 목록을 기반으로 상품과 옵션 정보를 조회한다.
   *
   * <p>ProductVariant를 시작점으로 Product를 조인하여 한 번의 쿼리로 조회하며, 옵션이 판매 중인 상품(onSale)만 필터링한다.
   *
   * <ul>
   *   <li>variantIds가 {@code null}이거나 비어있으면 빈 리스트를 반환한다.
   *   <li>ProductVariant → Product를 {@code fetchJoin}하여 N+1 문제를 방지한다.
   *   <li>결과는 {@link SearchProductResponse}로 매핑한다.
   * </ul>
   *
   * @param variantIds 조회할 상품 옵션 ID 목록
   * @return 상품 및 옵션 정보 응답 리스트
   * @author 김지원
   * @since 1.0.0
   */
  @Override
  public List<SearchProductResponse> searchIn(List<ProductVariantId> variantIds) {
    if (variantIds == null || variantIds.isEmpty()) {
      return List.of();
    }

    QProduct product = QProduct.product;
    QProductVariant productVariant = QProductVariant.productVariant;

    return queryFactory
        .select(product, productVariant)
        .from(productVariant)
        .join(productVariant.product, product)
        .fetchJoin()
        .where(productVariantIdIn(variantIds), onSale())
        .fetch()
        .stream()
        .map(
            item -> {
              String variant =
                  "size: %s, color: %s"
                      .formatted(
                          Objects.requireNonNull(item.get(productVariant)).getSize().getValue(),
                          Objects.requireNonNull(item.get(productVariant)).getColor().getValue());

              return new SearchProductResponse(
                  Objects.requireNonNull(item.get(product)).getId().toUuid(),
                  Objects.requireNonNull(item.get(product)).getName(),
                  variant,
                  Objects.requireNonNull(item.get(productVariant)).getId().toUuid(),
                  Objects.requireNonNull(item.get(productVariant)).getProduct().getPrice().value());
            })
        .toList();
  }

  /**
   * 공통 조회 로직.
   *
   * @param hub 허브 조건
   * @param vendor 벤더 조건
   * @param name 상품명 조건
   * @param minPrice 최소 가격 조건
   * @param maxPrice 최대 가격 조건
   * @param pageable 페이지 정보
   * @return 조건에 맞는 상품 페이지
   * @author 김지원
   * @since 1.0.0
   */
  private Page<Product> executeQuery(
      Predicate hub,
      Predicate vendor,
      Predicate name,
      Predicate minPrice,
      Predicate maxPrice,
      Predicate onSale,
      Pageable pageable) {
    QProduct product = QProduct.product;

    List<Product> results =
        queryFactory
            .select(product)
            .from(product)
            .where(hub, vendor, name, minPrice, maxPrice, onSale)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(product.createdAt.desc())
            .fetch();

    Long total =
        queryFactory
            .select(product.count())
            .from(product)
            .where(hub, vendor, name, minPrice, maxPrice, onSale)
            .fetchOne();

    return new PageImpl<>(results, pageable, total == null ? 0 : total);
  }

  /** 상품명 시작 문자열 조건 (대소문자 무시). */
  private Predicate nameStartWith(String name) {
    if (!StringUtils.hasText(name)) {
      return null;
    }
    return QProduct.product.name.startsWithIgnoreCase(name);
  }

  /** 최소 가격 이상 조건 (>=). */
  private Predicate minPriceGoe(Long price) {
    if (price == null || price <= 0) {
      return null;
    }
    return QProduct.product.price.amount.goe(price);
  }

  /** 최대 가격 이하 조건 (<=). */
  private Predicate maxPriceLoe(Long price) {
    if (price == null || price <= 0) {
      return null;
    }
    return QProduct.product.price.amount.loe(price);
  }

  /** 허브 ID 조건. */
  private Predicate hubIdEq(HubId hubId) {
    if (hubId == null) {
      return null;
    }
    return QProduct.product.hubId.eq(hubId);
  }

  /** 벤더 ID 조건. */
  private Predicate vendorIdEq(VendorId vendorId) {
    if (vendorId == null) {
      return null;
    }
    return QProduct.product.vendorId.eq(vendorId);
  }

  /** 판매중인 상품만 조회. */
  private Predicate onSale() {
    return QProduct.product.status.eq(ProductStatus.ON_SALE);
  }

  /**
   * 상품 옵션 ID 목록으로 IN 조건을 생성한다.
   *
   * @param variantIds 조회 대상 상품 옵션 ID 목록
   * @return Querydsl {@link Predicate}
   * @author 김지원
   * @since 1.0.0
   */
  private Predicate productVariantIdIn(List<ProductVariantId> variantIds) {
    return QProductVariant.productVariant.id.in(variantIds);
  }
}
