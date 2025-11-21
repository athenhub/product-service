package com.athenhub.productservice.product.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.athenhub.productservice.product.application.dto.ProductVariantUpdateRequest;
import com.athenhub.productservice.product.domain.dto.VariantUpdateSet;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VariantUpdateCommandMapperTest {

  private final VariantUpdateCommandMapper mapper = new VariantUpdateCommandMapper();

  @Test
  @DisplayName("옵션 변경 요청을 VariantUpdateSet으로 정상 변환한다")
  void toChangeSet_success() {
    // given
    String username = "test_user";
    UUID variantId = UUID.randomUUID();

    ProductVariantUpdateRequest request =
        new ProductVariantUpdateRequest(
            UUID.randomUUID(),
            List.of(new ProductVariantUpdateRequest.Add("RED", "M")),
            List.of(new ProductVariantUpdateRequest.Update(variantId, "BLACK", "L")),
            List.of(new ProductVariantUpdateRequest.Remove(variantId)));

    // when
    VariantUpdateSet result = mapper.toChangeSet(request, username);

    // then
    // 1. size 검증
    assertThat(result.createCommands()).hasSize(1);
    assertThat(result.updateCommands()).hasSize(1);
    assertThat(result.removeCommands()).hasSize(1);

    // 2. createCommands 검증
    var create = result.createCommands().getFirst();
    assertThat(create.color().getValue()).isEqualTo("RED");
    assertThat(create.size().getValue()).isEqualTo("M");

    // 3. updateCommands 검증
    var update = result.updateCommands().getFirst();
    assertThat(update.productVariantId().toUuid()).isEqualTo(variantId);
    assertThat(update.color().getValue()).isEqualTo("BLACK");
    assertThat(update.size().getValue()).isEqualTo("L");

    // 4. removeCommands 검증
    var remove = result.removeCommands().getFirst();
    assertThat(remove.productVariantId().toUuid()).isEqualTo(variantId);
    assertThat(remove.username()).isEqualTo(username);
  }

  @Test
  @DisplayName("추가/수정/삭제 요청이 없어도 비어있는 ChangeSet을 반환한다")
  void toChangeSet_empty() {
    ProductVariantUpdateRequest request =
        new ProductVariantUpdateRequest(UUID.randomUUID(), List.of(), List.of(), List.of());

    VariantUpdateSet result = mapper.toChangeSet(request, "test_user");

    assertThat(result.createCommands()).isEmpty();
    assertThat(result.updateCommands()).isEmpty();
    assertThat(result.removeCommands()).isEmpty();
  }
}
