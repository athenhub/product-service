package com.athenhub.productservice.product.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class PriceTest {

  @Test
  void of_negative_value() {
    assertThatThrownBy(() -> Price.of(-1L)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void add() {
    // given
    Price p1 = Price.of(1000L);
    Price p2 = Price.of(2000L);

    // when
    Price added = p1.add(p2);

    // then
    assertThat(added.value()).isEqualTo(3000L);
  }

  @Test
  void multiply() {
    // given
    Price p1 = Price.of(1000L);

    // when
    Price added = p1.multiply(100);

    // then
    assertThat(added.value()).isEqualTo(100_000);
  }

  @Test
  void equal() {
    // given
    Price p1 = Price.of(1000L);
    Price p2 = Price.of(1000L);

    // when
    boolean result = p1.equals(p2);

    // then
    assertThat(result).isTrue();
  }

  @Test
  void notEqual() {
    // given
    Price p1 = Price.of(1000L);
    Price p2 = Price.of(2000L);

    // when
    boolean result = p1.equals(p2);

    // then
    assertThat(result).isFalse();
  }
}
