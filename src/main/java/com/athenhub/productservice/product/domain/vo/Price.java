package com.athenhub.productservice.product.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Price {

  @Column(name = "price_amount", nullable = false)
  private final Long amount;

  protected Price() {
    this.amount = null; // JPA 전용
  }

  private Price(long amount) {
    this.amount = amount;
  }

  public static Price of(long amount) {
    return new Price(amount);
  }

  public Price add(Price other) {
    return new Price(this.value() + other.value());
  }

  public Price multiply(int quantity) {
    return new Price(this.value() * quantity);
  }

  public long value() {
    if (amount == null) {
      throw new IllegalStateException("Price.amount is null — JPA initialization problem");
    }
    return amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Price other)) return false;
    return Objects.equals(amount, other.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(amount);
  }
}
