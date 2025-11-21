package com.athenhub.productservice.product.domain;

import static com.athenhub.productservice.product.domain.ProductFixture.createOptionProduct;
import static com.athenhub.productservice.product.domain.ProductFixture.newProductVariantCreateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.athenhub.productservice.product.domain.dto.ProductVariantCreateCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantRemoveCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantUpdateCommand;
import com.athenhub.productservice.product.domain.dto.VariantUpdateSet;
import com.athenhub.productservice.product.domain.exception.VariantAlreadyExistsException;
import com.athenhub.productservice.product.domain.exception.VariantNotFoundException;
import com.athenhub.productservice.product.domain.vo.ProductColor;
import com.athenhub.productservice.product.domain.vo.ProductSize;
import com.athenhub.productservice.product.domain.vo.ProductVariantId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProductVariantsTest {

  private Product productOption() {
    return createOptionProduct();
  }

  private ProductVariantCreateCommand createCmd(String color, String size) {
    return newProductVariantCreateCommand(color, size);
  }

  @Nested
  @DisplayName("옵션 조회(get)")
  class GetVariantTest {

    @Test
    @DisplayName("옵션 단건 조회 성공")
    void getVariant_success() {
      // given
      Product product = productOption();
      ProductVariants variants = new ProductVariants();

      ProductVariant variant = ProductVariant.create(createCmd("RED", "M"));
      variants.add(variant, product);

      // when
      ProductVariant found = variants.get(variant.getId());

      // then
      assertThat(found.getColor().getValue()).isEqualTo("RED");
      assertThat(found.getSize().getValue()).isEqualTo("M");
    }

    @Test
    @DisplayName("존재하지 않는 옵션 조회 시 예외 발생")
    void getVariant_fail_notFound() {
      // given
      ProductVariants variants = new ProductVariants();

      // when & then
      assertThatThrownBy(() -> variants.get(ProductVariantId.create()))
          .isInstanceOf(VariantNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("옵션 중복 검사(exists)")
  class ExistsTest {

    @Test
    @DisplayName("동일 Color + Size 조합이 존재하면 true")
    void exists_true() {
      // given
      Product product = productOption();
      ProductVariants variants = new ProductVariants();

      ProductVariant v1 = ProductVariant.create(createCmd("RED", "M"));
      variants.add(v1, product);

      // when
      ProductVariant v2 = ProductVariant.create(createCmd("RED", "M"));

      // then
      assertThat(variants.exists(v2)).isTrue();
    }

    @Test
    @DisplayName("동일 조합이 없으면 false")
    void exists_false() {
      // given
      Product product = productOption();
      ProductVariants variants = new ProductVariants();

      variants.add(ProductVariant.create(createCmd("RED", "M")), product);

      // when
      ProductVariant other = ProductVariant.create(createCmd("BLUE", "L"));

      // then
      assertThat(variants.exists(other)).isFalse();
    }
  }

  @Nested
  @DisplayName("옵션 추가(add)")
  class AddTest {

    @Test
    @DisplayName("옵션 추가 성공")
    void add_success() {
      // given
      Product product = productOption();
      ProductVariants variants = new ProductVariants();

      // when
      ProductVariant variant = ProductVariant.create(createCmd("RED", "M"));
      variants.add(variant, product);

      // then
      assertThat(variants.getValues())
          .hasSize(1)
          .extracting("color.value", "size.value")
          .containsExactly(tuple("RED", "M"));

      assertThat(variant.getProduct()).isEqualTo(product); // assignTo 검증
    }

    @Test
    @DisplayName("중복 옵션 추가 시 예외 발생")
    void add_duplicate_fail() {
      // given
      Product product = productOption();
      ProductVariants variants = new ProductVariants();

      variants.add(ProductVariant.create(createCmd("RED", "M")), product);

      // when & then
      assertThatThrownBy(() -> variants.add(ProductVariant.create(createCmd("RED", "M")), product))
          .isInstanceOf(VariantAlreadyExistsException.class);
    }
  }

  @Nested
  @DisplayName("옵션 수정(update)")
  class UpdateTest {

    @Test
    @DisplayName("옵션 수정 성공")
    void update_success() {
      // given
      Product product = productOption();
      ProductVariants variants = new ProductVariants();

      ProductVariant variant = ProductVariant.create(createCmd("RED", "M"));
      variants.add(variant, product);

      // when
      ProductVariantUpdateCommand cmd =
          new ProductVariantUpdateCommand(
              variant.getId(), ProductColor.of("BLUE"), ProductSize.of("L"));

      variants.update(cmd);

      // then
      assertThat(variant.getColor().getValue()).isEqualTo("BLUE");
      assertThat(variant.getSize().getValue()).isEqualTo("L");
    }

    @Test
    @DisplayName("수정 대상이 존재하지 않으면 예외")
    void update_fail_notFound() {
      // given
      ProductVariants variants = new ProductVariants();

      // when & then
      ProductVariantUpdateCommand cmd =
          new ProductVariantUpdateCommand(
              ProductVariantId.create(), ProductColor.of("BLUE"), ProductSize.of("L"));

      assertThatThrownBy(() -> variants.update(cmd)).isInstanceOf(VariantNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("옵션 삭제(remove)")
  class RemoveTest {

    @Test
    @DisplayName("옵션 삭제 성공 (Soft Delete)")
    void remove_success() {
      // given
      Product product = productOption();
      ProductVariants variants = new ProductVariants();

      ProductVariant variant = ProductVariant.create(createCmd("RED", "M"));
      variants.add(variant, product);

      // when
      variants.remove(variant.getId(), "tester");

      // then
      assertThat(variant.isDeleted()).isTrue();
      assertThat(variant.getDeletedBy()).isEqualTo("tester");
    }

    @Test
    @DisplayName("없는 옵션 삭제 시 예외")
    void remove_notFound_fail() {
      // given
      ProductVariants variants = new ProductVariants();

      // when & then
      assertThatThrownBy(() -> variants.remove(ProductVariantId.create(), "tester"))
          .isInstanceOf(VariantNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("옵션 전체 삭제(removeAll)")
  class RemoveAllTest {

    @Test
    @DisplayName("전체 옵션 삭제 성공")
    void removeAll_success() {
      // given
      Product product = productOption();
      ProductVariants variants = new ProductVariants();

      ProductVariant v1 = ProductVariant.create(createCmd("RED", "M"));
      ProductVariant v2 = ProductVariant.create(createCmd("BLUE", "L"));

      variants.add(v1, product);
      variants.add(v2, product);

      // when
      variants.removeAll("tester");

      // then
      assertThat(v1.isDeleted()).isTrue();
      assertThat(v1.getDeletedBy()).isEqualTo("tester");
      assertThat(v2.isDeleted()).isTrue();
      assertThat(v2.getDeletedBy()).isEqualTo("tester");
    }
  }

  @Nested
  @DisplayName("옵션 일괄 처리(apply)")
  class ApplyTest {

    @Test
    @DisplayName("create / update / remove 일괄 반영")
    void apply_success() {
      // given
      Product product = productOption();
      ProductVariants variants = new ProductVariants();

      // 기존 옵션 1개
      ProductVariant v1 = ProductVariant.create(createCmd("RED", "M"));
      variants.add(v1, product);

      // 일괄 명령 세트
      VariantUpdateSet updateSet =
          new VariantUpdateSet(
              List.of(createCmd("BLUE", "S")), // create
              List.of(
                  new ProductVariantUpdateCommand(
                      v1.getId(), ProductColor.of("BLACK"), ProductSize.of("XL"))), // update
              List.of(new ProductVariantRemoveCommand(v1.getId(), "tester")) // remove
              );

      // when
      variants.apply(updateSet, product);

      // then → create, update, remove 모두 검증
      assertThat(variants.getValues())
          .hasSize(2)
          .extracting("color.value", "size.value", "deletedBy")
          .containsExactlyInAnyOrder(tuple("BLUE", "S", null), tuple("BLACK", "XL", "tester"));
      assertThat(v1.isDeleted()).isTrue(); // 삭제됨
    }
  }
}
