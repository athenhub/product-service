package com.athenhub.productservice.product.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상품의 사이즈(Size)를 표현하는 값 객체(Value Object)이다.
 *
 * <p>사이즈 값은 문자열 기반으로 표현되며, 값이 존재할 경우 생성 시점에 정규화(Trim + UpperCase)되어 저장된다. 사이즈 정보가 없는 상품의 경우 {@code
 * null} 값으로 표현될 수 있다.
 *
 * <p>해당 클래스는 불변(Immutable) 객체이며, JPA의 {@link Embeddable}로 매핑된다. 외부에서는 반드시 정적 팩토리 메서드 {@link
 * #of(String)}를 통해 생성해야 한다.
 *
 * <h3>도메인 규칙</h3>
 *
 * <ul>
 *   <li>사이즈 값이 있을 경우, 앞뒤 공백을 제거하고 대문자로 통일한다.
 *   <li>사이즈 값이 없을 경우(null), "사이즈 미지정" 상태를 나타낸다.
 *   <li>빈 문자열("")은 허용되지만 도메인 단에서 별도의 검증은 하지 않는다 (필요할 경우 상위 계층에서 검증 가능).
 *   <li>문자 기반 사이즈("S", "M", "L", "XL")와 숫자 기반 사이즈("230", "240") 모두 사용자 입력값에 따라 허용된다.
 * </ul>
 *
 * <h3>설계 의도</h3>
 *
 * <ul>
 *   <li>정규화 로직은 생성자에서 단 한 번 수행된다.
 *   <li>{@code normalize()} 메서드는 문자열이 존재할 때만 호출되며, null 처리 책임은 생성자에서 담당한다.
 *   <li>VO는 생성 이후 변경되지 않는 불변 객체로서, 항상 일관된 상태를 보장한다.
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ProductSize {

  /** 상품의 사이즈 값. 예: "M", "XL", "FREE", "240". 값이 없으면 null. */
  @Column(name = "size")
  private String value;

  /**
   * 주어진 사이즈 문자열을 기반으로 ProductSize 객체를 생성한다. null이 아닌 경우 정규화(Trim + UpperCase) 로직이 적용된다.
   *
   * @param value 사이즈 문자열 값. null일 수 있음.
   */
  private ProductSize(String value) {
    if (value != null) {
      value = normalize(value);
    }
    this.value = value;
  }

  /**
   * {@link ProductSize} 생성 팩토리 메서드.
   *
   * @param value 사이즈 문자열 값. null일 수 있음.
   * @return 생성된 {@link ProductSize} 인스턴스
   */
  public static ProductSize of(String value) {
    return new ProductSize(value);
  }

  /**
   * 사이즈 문자열을 정규화한다.
   *
   * <ul>
   *   <li>앞뒤 공백 제거
   *   <li>대문자 변환
   * </ul>
   *
   * @param value 정규화 대상 문자열(절대 null이 아님)
   * @return 정규화된 문자열
   */
  private String normalize(String value) {
    return value.trim().toUpperCase();
  }
}
