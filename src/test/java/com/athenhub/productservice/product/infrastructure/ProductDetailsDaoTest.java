package com.athenhub.productservice.product.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.athenhub.productservice.global.infrastructure.audit.JpaAuditingConfig;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductFixture;
import com.athenhub.productservice.product.domain.ProductStatus;
import com.athenhub.productservice.product.domain.ProductType;
import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.dto.SearchDaoRequest;
import com.athenhub.productservice.product.domain.dto.SearchProductResponse;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.ProductVariantId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
@Import({ProductDetailsDao.class, QuerydslConfig.class, JpaAuditingConfig.class})
class ProductDetailsDaoTest {

  @Autowired EntityManager em;

  @Autowired ProductDetailsDao productDetailsDao;

  @Autowired JPAQueryFactory queryFactory;

  HubId hubId = HubId.of(UUID.randomUUID());
  VendorId vendorId = VendorId.of(UUID.randomUUID());

  private Map<String, Product> productMap;

  @BeforeEach
  void setUp() {
    queryFactory = new JPAQueryFactory(em);
    productMap = insertTestData();
    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("상품명으로 startsWith + 페이징 조회")
  void find_by_name_startsWith() {
    // given
    SearchDaoRequest search = new SearchDaoRequest("나이키", null, null, null, null);

    // when
    Page<Product> result = productDetailsDao.search(search, PageRequest.of(0, 10));

    // then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).allMatch(p -> p.getName().startsWith("나이키"));
  }

  @Test
  @DisplayName("가격 범위(min~max) 조회")
  void find_by_price_range() {
    // given
    SearchDaoRequest search = new SearchDaoRequest(null, null, null, 10000L, 30000L);

    // when
    Page<Product> result = productDetailsDao.search(search, PageRequest.of(0, 10));

    // then
    assertThat(result.getContent()).hasSize(3);
    assertThat(result.getContent())
        .allMatch(p -> p.getPrice().value() >= 10000 && p.getPrice().value() <= 30000);
  }

  @Test
  @DisplayName("허브 + 벤더 조건 조회")
  void find_by_hub_and_vendor() {
    // given
    SearchDaoRequest search = new SearchDaoRequest(null, hubId, vendorId, null, null);

    // when
    Page<Product> result = productDetailsDao.search(search, PageRequest.of(0, 10));
    result.forEach(it -> System.out.println("result: " + it));
    // then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getHubId()).isEqualTo(hubId);
    assertThat(result.getContent().getFirst().getVendorId()).isEqualTo(vendorId);
  }

  @Test
  @DisplayName("판매중(OnSale) 상품만 조회")
  void search_onSale() {
    // given
    SearchDaoRequest search = new SearchDaoRequest(null, null, null, null, null);

    // when
    Page<Product> result = productDetailsDao.search(search, PageRequest.of(0, 10));
    result.forEach(it -> System.out.println("result: " + it));

    // then (삭제 상품 제외: 총 4개 -> 3개로 줄어야 함)
    assertThat(result.getTotalElements()).isEqualTo(3);
  }

  @Test
  @DisplayName("상품 조회시 옵션 정보도 함께 조회한다.")
  void find_by_name_with_variant() {
    // given
    SearchDaoRequest search = new SearchDaoRequest("아디다스 신발", null, null, null, null);

    // when
    Page<Product> result = productDetailsDao.search(search, PageRequest.of(0, 10));
    Product product = result.getContent().getFirst();

    assertThat(product.getVariants())
        .hasSize(2)
        .extracting("color.value", "size.value")
        .contains(tuple("RED", "265"), tuple("RED", "270"));
  }

  @Test
  @DisplayName("hubId로 상품을 조회한다")
  void find_by_hubId() {
    // when
    Page<Product> result = productDetailsDao.findByHubId(hubId, PageRequest.of(0, 10));

    // then
    assertThat(result.getContent())
        .hasSize(3)
        .extracting("name")
        .contains("나이키 모자", "나이키 신발", "단종된 상품");
  }

  @Test
  @DisplayName("vendorId로 상품을 조회한다")
  void find_by_vendorId() {
    Page<Product> result = productDetailsDao.findByVendorId(vendorId, PageRequest.of(0, 10));

    // then
    assertThat(result.getContent())
        .hasSize(3)
        .extracting("name")
        .contains("아디다스 신발", "나이키 신발", "단종된 상품");
  }

  @Test
  @DisplayName("모든 상품을 조회한다. 판매중이 아닌 상품도 조회된다.")
  void find_all() {
    Page<Product> result = productDetailsDao.findAll(PageRequest.of(0, 10));

    // then
    assertThat(result.getContent()).hasSize(5);
  }

  @Test
  @DisplayName("옵션 아이디로 상품을 조회한다.")
  void findByOption() {
    Product nikeShoes = productMap.get("nikeShoes");
    ProductVariantId nikeShoesVariantId = nikeShoes.getVariants().getFirst().getId();
    Product adidasShoes = productMap.get("adidasShoes");
    ProductVariantId adidasShoesVariantId = adidasShoes.getVariants().getFirst().getId();

    List<SearchProductResponse> result =
        productDetailsDao.searchIn(List.of(nikeShoesVariantId, adidasShoesVariantId));

    assertThat(result)
        .hasSize(2)
        .extracting("productId", "productName", "productVariantId", "price")
        .containsExactlyInAnyOrder(
            tuple(
                nikeShoes.getId().toUuid(),
                nikeShoes.getName(),
                nikeShoesVariantId.toUuid(),
                nikeShoes.getPrice().value()),
            tuple(
                adidasShoes.getId().toUuid(),
                adidasShoes.getName(),
                adidasShoesVariantId.toUuid(),
                adidasShoes.getPrice().value()));
  }

  @TestConfiguration
  static class TestAuditingConfig {

    @Bean(name = "auditorAwareImpl")
    public AuditorAware<String> auditorAware() {
      return () -> Optional.of("TEST_USER");
    }
  }

  /** 테스트용 데이터 삽입 */
  private Map<String, Product> insertTestData() {
    Map<String, Product> products = new HashMap<>();
    Product nikeShoes =
        persistProduct(
            "나이키 신발", 10000L, hubId, vendorId, ProductType.SIMPLE, ProductStatus.ON_SALE);
    products.put("nikeShoes", nikeShoes);

    Product nikeHat =
        persistProduct(
            "나이키 모자",
            20000L,
            hubId,
            VendorId.of(UUID.randomUUID()),
            ProductType.SIMPLE,
            ProductStatus.ON_SALE);
    products.put("nikeHat", nikeHat);

    Product adidasShoes =
        persistProduct(
            "아디다스 신발",
            30000L,
            HubId.of(UUID.randomUUID()),
            vendorId,
            ProductType.OPTION,
            ProductStatus.ON_SALE);
    adidasShoes.addVariant(ProductFixture.newProductVariantCreateCommand("RED", "265"));
    adidasShoes.addVariant(ProductFixture.newProductVariantCreateCommand("RED", "270"));
    products.put("adidasShoes", adidasShoes);

    // 논리 삭제된 상품
    Product deleted =
        persistProduct(
            "삭제된 상품",
            5000L,
            HubId.of(UUID.randomUUID()),
            VendorId.of(UUID.randomUUID()),
            ProductType.SIMPLE,
            ProductStatus.DELETED);
    products.put("deleted", deleted);

    Product discontinue =
        persistProduct(
            "단종된 상품", 10000L, hubId, vendorId, ProductType.SIMPLE, ProductStatus.DISCONTINUED);
    products.put("discontinue", discontinue);

    return products;
  }

  private Product persistProduct(
      String name,
      long price,
      HubId hubId,
      VendorId vendorId,
      ProductType type,
      ProductStatus status) {
    ProductCreateCommand productCreateCommand =
        new ProductCreateCommand(name, "설명", Price.of(price), hubId, vendorId, type);
    Product product = Product.create(productCreateCommand);
    product.changeStatus(status);

    if (status == ProductStatus.DELETED) {
      product.delete("admin");
    }
    em.persist(product);
    return product;
  }
}
