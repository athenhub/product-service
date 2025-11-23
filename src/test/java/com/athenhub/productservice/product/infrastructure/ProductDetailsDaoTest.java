package com.athenhub.productservice.product.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.athenhub.productservice.global.infrastructure.audit.JpaAuditingConfig;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductFixture;
import com.athenhub.productservice.product.domain.ProductType;
import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.dto.SearchRequest;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.VendorId;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
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

@DataJpaTest
@Import({ProductDetailsDao.class, QuerydslConfig.class, JpaAuditingConfig.class})
class ProductDetailsDaoTest {

  @Autowired EntityManager em;

  @Autowired ProductDetailsDao productDetailsDao;

  @Autowired JPAQueryFactory queryFactory;

  HubId hubId = HubId.of(UUID.randomUUID());
  VendorId vendorId = VendorId.of(UUID.randomUUID());

  @BeforeEach
  void setUp() {
    queryFactory = new JPAQueryFactory(em);
    insertTestData();
    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("상품명으로 startsWith + 페이징 조회")
  void find_by_name_startsWith() {
    // given
    SearchRequest search = new SearchRequest("나이키", null, null, null, null);

    // when
    Page<Product> result = productDetailsDao.findAll(search, 1, 10);

    // then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).allMatch(p -> p.getName().startsWith("나이키"));
  }

  @Test
  @DisplayName("가격 범위(min~max) 조회")
  void find_by_price_range() {
    // given
    SearchRequest search = new SearchRequest(null, null, null, 10000L, 30000L);

    // when
    Page<Product> result = productDetailsDao.findAll(search, 1, 10);

    // then
    assertThat(result.getContent()).hasSize(3);
    assertThat(result.getContent())
        .allMatch(p -> p.getPrice().value() >= 10000 && p.getPrice().value() <= 30000);
  }

  @Test
  @DisplayName("허브 + 벤더 조건 조회")
  void find_by_hub_and_vendor() {
    // given
    SearchRequest search = new SearchRequest(null, hubId.toUuid(), vendorId.toUuid(), null, null);

    // when
    Page<Product> result = productDetailsDao.findAll(search, 1, 10);

    // then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getHubId()).isEqualTo(hubId);
    assertThat(result.getContent().getFirst().getVendorId()).isEqualTo(vendorId);
  }

  @Test
  @DisplayName("논리 삭제된 상품은 조회에서 제외")
  void not_deleted_only() {
    // given
    SearchRequest search = new SearchRequest(null, null, null, null, null);

    // when
    Page<Product> result = productDetailsDao.findAll(search, 1, 10);

    // then (삭제 상품 제외: 총 4개 -> 3개로 줄어야 함)
    assertThat(result.getTotalElements()).isEqualTo(3);
  }

  @Test
  @DisplayName("상품 조회시 옵션 정보도 함께 조회한다.")
  void find_by_name_with_variant() {
    // given
    SearchRequest search = new SearchRequest("아디다스 신발", null, null, null, null);

    // when
    Page<Product> result = productDetailsDao.findAll(search, 1, 10);
    Product product = result.getContent().getFirst();

    assertThat(product.getVariants())
        .hasSize(2)
        .extracting("color.value", "size.value")
        .contains(tuple("RED", "265"), tuple("RED", "270"));
  }

  @TestConfiguration
  static class TestAuditingConfig {

    @Bean(name = "auditorAwareImpl")
    public AuditorAware<String> auditorAware() {
      return () -> Optional.of("TEST_USER");
    }
  }

  /** 테스트용 데이터 삽입 */
  private void insertTestData() {
    persistProduct("나이키 신발", 10000L, hubId, vendorId, ProductType.SIMPLE);
    persistProduct("나이키 모자", 20000L, hubId, VendorId.of(UUID.randomUUID()), ProductType.SIMPLE);
    Product adidasShoes =
        persistProduct(
            "아디다스 신발", 30000L, HubId.of(UUID.randomUUID()), vendorId, ProductType.OPTION);
    adidasShoes.addVariant(ProductFixture.newProductVariantCreateCommand("RED", "265"));
    adidasShoes.addVariant(ProductFixture.newProductVariantCreateCommand("RED", "270"));

    // 논리 삭제된 상품
    Product deleted =
        persistProduct(
            "삭제된 상품",
            5000L,
            HubId.of(UUID.randomUUID()),
            VendorId.of(UUID.randomUUID()),
            ProductType.SIMPLE);
    deleted.delete("admin");
  }

  private Product persistProduct(
      String name, long price, HubId hubId, VendorId vendorId, ProductType type) {
    ProductCreateCommand productCreateCommand =
        new ProductCreateCommand(name, "설명", Price.of(price), hubId, vendorId, type);
    Product product = Product.create(productCreateCommand);
    em.persist(product);
    return product;
  }
}
