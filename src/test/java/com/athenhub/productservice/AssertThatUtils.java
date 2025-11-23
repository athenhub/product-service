package com.athenhub.productservice;

import java.util.function.Consumer;
import org.assertj.core.api.AssertProvider;
import org.assertj.core.api.Assertions;
import org.springframework.test.json.JsonPathValueAssert;

/**
 * 테스트 코드에서 JSON Path 기반 응답 값을 간결하고 직관적으로 검증하기 위한 유틸리티 클래스.
 *
 * <p>이 클래스는 {@link Consumer} 형태의 검증기(validator)를 제공하여, 공유된 Assertion DSL 내에서 메서드 레퍼런스를 활용한 가독성 높은
 * 검증을 가능하게 합니다.
 *
 * <p>예시:
 *
 * <pre>{@code
 * json.assertThat("$.data.id", AssertThatUtils.notNull());
 * json.assertThat("$.success", AssertThatUtils.isTrue());
 * json.assertThat("$.data.name", AssertThatUtils.isEqualTo("홍길동"));
 * }</pre>
 *
 * <p>각 검증 메서드는 {@link AssertProvider<JsonPathValueAssert>}를 입력으로 받아 AssertJ 기반의 실제 검증을 수행합니다.
 */
public class AssertThatUtils {
  /**
   * 값이 {@code null} 인지 검증하는 validator 를 반환합니다.
   *
   * @return 값이 null 이어야 테스트가 통과하는 Consumer
   */
  public static Consumer<AssertProvider<JsonPathValueAssert>> isNull() {
    return value -> Assertions.assertThat(value).isNull();
  }

  public static Consumer<AssertProvider<JsonPathValueAssert>> isNotNull() {
    return value -> Assertions.assertThat(value).isNotNull();
  }

  /**
   * 값이 {@code null} 이 아님을 검증하는 validator 를 반환합니다.
   *
   * @return 값이 not null 이어야 통과하는 Consumer
   */
  public static Consumer<AssertProvider<JsonPathValueAssert>> notNull() {
    return value -> Assertions.assertThat(value).isNotNull();
  }

  /**
   * Boolean 값이 {@code true} 인지 검증하는 validator 를 반환합니다.
   *
   * @return Boolean 값이 true 이어야 통과하는 Consumer
   */
  public static Consumer<AssertProvider<JsonPathValueAssert>> isTrue() {
    return value -> Assertions.assertThat(value).asBoolean().isTrue();
  }

  /**
   * Boolean 값이 {@code false} 인지 검증하는 validator 를 반환합니다.
   *
   * @return Boolean 값이 false 이어야 통과하는 Consumer
   */
  public static Consumer<AssertProvider<JsonPathValueAssert>> isFalse() {
    return value -> Assertions.assertThat(value).asBoolean().isFalse();
  }

  /**
   * 응답 값이 기대 값과 동일한지 검증하는 validator 를 반환합니다.
   *
   * @param expected 기대되는 값
   * @return 값이 expected 와 동일해야 통과하는 Consumer
   */
  public static Consumer<AssertProvider<JsonPathValueAssert>> isEqualTo(Object expected) {
    return value -> Assertions.assertThat(value).isEqualTo(expected);
  }
}
