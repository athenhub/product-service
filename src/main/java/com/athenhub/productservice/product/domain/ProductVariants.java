package com.athenhub.productservice.product.domain;

import static com.athenhub.productservice.product.domain.exception.ProductDomainErrorCode.PRODUCT_VARIANT_ALREADY_EXIST;
import static com.athenhub.productservice.product.domain.exception.ProductDomainErrorCode.PRODUCT_VARIANT_NOT_FOUND;

import com.athenhub.productservice.product.domain.dto.ProductVariantUpdateCommand;
import com.athenhub.productservice.product.domain.dto.VariantUpdateSet;
import com.athenhub.productservice.product.domain.exception.VariantAlreadyExistsException;
import com.athenhub.productservice.product.domain.exception.VariantNotFoundException;
import com.athenhub.productservice.product.domain.vo.ProductVariantId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * ProductVariants는 상품 옵션(ProductVariant)의 컬렉션을 표현하는 1급 컬렉션(First-Class Collection)이다.
 *
 * <p>이 객체는 단순히 List<ProductVariant>를 감싸는 것이 아니라, 옵션 컬렉션과 관련된 도메인 규칙 및 상태 변경 로직을 캡슐화하여 Product
 * Aggregate Root의 복잡도를 낮추는 역할을 수행한다.
 *
 * <p>■ 주요 역할
 *
 * <ul>
 *   <li>옵션(ProductVariant) 목록 관리
 *   <li>옵션 단건 조회, 수정, 삭제 규칙 관리
 *   <li>동일 옵션(Color + Size) 중복 여부 판단
 *   <li>VariantUpdateSet을 통한 일괄 처리
 *   <li>Soft Delete 시 옵션 전체 삭제 전파
 * </ul>
 *
 * <p>■ 도메인 설계 의도
 *
 * <ul>
 *   <li>Product가 옵션 관련 로직을 모두 가지면 Aggregate Root가 비대해지므로 옵션 컬렉션 책임을 ProductVariants로 분리하여 응집도를 높인다.
 *   <li>옵션 자체는 엔티티이지만, 옵션 컬렉션의 규칙(add/remove/update)은 컬렉션 객체 내부에서 관리된다.
 *   <li>연관관계는 JPA 매핑을 위해 ProductVariant → Product 방향의 ManyToOne을 유지하며, ProductVariants는 단순히 컬렉션 관리
 *       책임만 가진다.
 * </ul>
 *
 * <p>■ JPA 매핑
 *
 * <ul>
 *   <li>단순 OneToMany 단방향 매핑은 성능 문제가 있으므로 mappedBy = "product" 형식의 양방향 매핑을 유지한다.
 *   <li>Aggregate 내부 컬렉션이므로 CascadeType.ALL + orphanRemoval = true 를 통해 Aggregate Root(Product)의
 *       생명주기를 그대로 따른다.
 * </ul>
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductVariants {

  /**
   * ProductVariant 목록.
   *
   * <p>주의:
   *
   * <ul>
   *   <li>JPA cascade와 orphanRemoval을 통해 Product Aggregate Root에 종속된다.
   *   <li>리스트 변경은 반드시 ProductVariants 내부 메서드를 통해서만 수행해야 한다.
   * </ul>
   */
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProductVariant> values = new ArrayList<>();

  /** 옵션 목록을 불변 리스트로 반환한다. */
  public List<ProductVariant> getValues() {
    return List.copyOf(values);
  }

  /**
   * 옵션 단건 조회 (없으면 예외).
   *
   * @throws VariantNotFoundException 옵션 ID가 존재하지 않을 경우
   */
  public ProductVariant get(ProductVariantId variantId) {
    return values.stream()
        .filter(v -> v.getId().equals(variantId))
        .findFirst()
        .orElseThrow(() -> new VariantNotFoundException(PRODUCT_VARIANT_NOT_FOUND));
  }

  /**
   * 동일 옵션(Color + Size) 중복 여부 검사.
   *
   * <p>ProductVariant의 isSameOption() 도메인 규칙을 활용하여 동일 조합의 옵션이 이미 존재하는지 판단한다.
   */
  public boolean exists(ProductVariant target) {
    return values.stream().anyMatch(v -> v.isSameOption(target));
  }

  /**
   * 옵션 추가.
   *
   * <p>옵션은 Product Aggregate Root에 종속되므로 추가 시 Product와의 연관 관계를 설정(assignTo)한다.
   *
   * <p>도메인 규칙:
   *
   * <ul>
   *   <li>중복 옵션(Color + Size)이 존재하면 추가 불가
   * </ul>
   */
  public void add(ProductVariant variant, Product product) {
    if (exists(variant)) {
      throw new VariantAlreadyExistsException(PRODUCT_VARIANT_ALREADY_EXIST);
    }

    variant.assignTo(product);
    values.add(variant);
  }

  /**
   * 옵션 단건 삭제 (Soft Delete).
   *
   * @param username 삭제 수행자 (감사 로그용)
   */
  public void remove(ProductVariantId variantId, String username) {
    get(variantId).delete(username);
  }

  /**
   * 전체 옵션 Soft Delete.
   *
   * <p>Product가 삭제될 경우 Cascade로 옵션도 함께 Soft Delete 된다.
   */
  public void removeAll(String deleteBy) {
    values.forEach(v -> v.delete(deleteBy));
  }

  /**
   * 옵션 수정.
   *
   * <p>ID로 해당 옵션을 조회하고, 엔티티가 직접 update()를 수행한다.
   */
  public void update(ProductVariantUpdateCommand command) {
    get(command.productVariantId()).update(command.color(), command.size());
  }

  /**
   * VariantUpdateSet을 적용(옵션 일괄 처리).
   *
   * <p>VariantUpdateSet은 옵션 추가/수정/삭제 명령을 포함한 도메인 객체로, ProductVariants는 이를 받아 일괄적으로 컬렉션 변경을 수행한다.
   *
   * @param product Product Aggregate Root (연관 관계 설정에 필요)
   */
  public void apply(VariantUpdateSet updateSet, Product product) {
    updateSet.createCommands().forEach(c -> add(ProductVariant.create(c), product));
    updateSet.updateCommands().forEach(this::update);
    updateSet.removeCommands().forEach(cmd -> remove(cmd.productVariantId(), cmd.username()));
  }
}
