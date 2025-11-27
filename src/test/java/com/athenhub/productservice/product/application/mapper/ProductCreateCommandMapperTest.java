package com.athenhub.productservice.product.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.athenhub.productservice.product.application.dto.ProductRegisterRequest;
import com.athenhub.productservice.product.domain.ProductType;
import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantCreateCommand;
import java.util.List;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link ProductCreateCommandMapper} 단위 테스트
 *
 * @author 김지원
 * @since 1.0.0
 */
class ProductCreateCommandMapperTest {

  private final ProductCreateCommandMapper mapper = new ProductCreateCommandMapper();

  @Test
  @DisplayName("ProductRegisterRequest를 ProductCreateCommand로 정상 변환한다")
  void toCreateCommand() {
    // given
    UUID hubId = UUID.randomUUID();
    UUID vendorId = UUID.randomUUID();

    ProductRegisterRequest request =
        new ProductRegisterRequest(
            "test-productName",
            "test-description",
            10_000L,
            hubId,
            vendorId,
            ProductType.OPTION,
            List.of());

    // when
    ProductCreateCommand result = mapper.toCreateCommand(request);

    // then
    assertThat(result.name()).isEqualTo("test-productName");
    assertThat(result.description()).isEqualTo("test-description");
    assertThat(result.hubId().toUuid()).isEqualTo(hubId);
    assertThat(result.vendorId().toUuid()).isEqualTo(vendorId);
    assertThat(result.price().value()).isEqualTo(10_000L);
    assertThat(result.type()).isEqualTo(ProductType.OPTION);
  }

  @Test
  @DisplayName("옵션 요청을 ProductVariantCreateCommand 리스트로 정상 변환한다")
  void toVariantCommands() {
    // given
    ProductRegisterRequest.RegisterProductVariant v1 =
        new ProductRegisterRequest.RegisterProductVariant("RED", "M");

    ProductRegisterRequest.RegisterProductVariant v2 =
        new ProductRegisterRequest.RegisterProductVariant("BLACK", "L");

    ProductRegisterRequest request =
        new ProductRegisterRequest(
            "test-productName",
            "test-description",
            5_000L,
            UUID.randomUUID(),
            UUID.randomUUID(),
            ProductType.OPTION,
            List.of(v1, v2));

    // when
    List<ProductVariantCreateCommand> result = mapper.toVariantCommands(request);

    // then
    assertThat(result)
        .hasSize(2)
        .extracting("color.value", "size.value")
        .containsExactlyInAnyOrder(Tuple.tuple("RED", "M"), Tuple.tuple("BLACK", "L"));
  }

  @Test
  @DisplayName("옵션이 없는 경우 빈 리스트를 반환한다")
  void toVariantCommands_empty() {
    // given
    ProductRegisterRequest request =
        new ProductRegisterRequest(
            "test-productName",
            "test-description",
            5_000L,
            UUID.randomUUID(),
            UUID.randomUUID(),
            ProductType.SIMPLE,
            List.of());

    // when
    List<ProductVariantCreateCommand> result = mapper.toVariantCommands(request);

    // then
    assertThat(result).isEmpty();
  }
}
