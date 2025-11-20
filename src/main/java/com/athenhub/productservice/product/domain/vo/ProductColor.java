package com.athenhub.productservice.product.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상품의 색상(Color)을 표현하는 값 객체(Value Object)이다.
 *
 * <p>색상 값은 문자열 기반으로 표현되며, 값이 존재할 경우 생성 시점에 정규화(Trim + UpperCase)되어 저장된다. 색상이 지정되지 않은 상품의 경우 {@code
 * null} 값으로 표현될 수 있다.
 *
 * <p>해당 클래스는 불변(Immutable) 객체이며, JPA의 {@link Embeddable}로 매핑된다. 외부에서는 반드시 정적 팩토리 메서드 {@link
 * #of(String)}를 통해 생성해야 한다.
 *
 * <h3>도메인 규칙</h3>
 *
 * <ul>
 *   <li>색상 값이 있을 경우, 앞뒤 공백을 제거하고 대문자로 통일하여 저장한다.
 *   <li>색상 값이 없을 경우(null), 색상 미지정 상태를 나타낸다.
 *   <li>빈 문자열("")은 허용되지만, 별도 검증 규칙은 적용하지 않는다 (필요 시 상위 계층에서 검증 가능).
 * </ul>
 *
 * <h3>설계 의도</h3>
 *
 * <ul>
 *   <li>정규화 로직은 생성자에서 한 번만 수행되며, 객체 생성 이후 값은 변경되지 않는다.
 *   <li>{@code normalize()} 메서드는 문자열이 존재하는 경우에만 호출되며, null 처리 책임은 생성자가 담당한다.
 *   <li>VO는 항상 일관된 형태의 값을 보장하도록 설계되었다.
 * </ul>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ProductColor {

  /** 상품의 색상값. 예: "RED", "BLACK", "#FFFFFF". 색상이 없을 경우 null. */
  @Column(name = "color")
  private String value;

  /**
   * 색상 값을 기반으로 ProductColor 객체를 생성한다. null이 아닌 경우 정규화(Trim + UpperCase) 로직이 적용된다.
   *
   * @param value 색상 문자열 값. null일 수 있음.
   */
  private ProductColor(String value) {
    if (value != null) {
      value = normalize(value);
    }
    this.value = value;
  }

  /**
   * {@link ProductColor} 생성 팩토리 메서드.
   *
   * @param value 색상 문자열 값. null일 수 있음.
   * @return 생성된 {@link ProductColor} 인스턴스
   */
  public static ProductColor of(String value) {
    return new ProductColor(value);
  }

  /**
   * 색상 문자열을 정규화한다.
   *
   * <ul>
   *   <li>앞뒤 공백 제거
   *   <li>대문자 변환
   * </ul>
   *
   * @param value 정규화 대상 문자열 (null이 아님)
   * @return 정규화된 문자열
   */
  private String normalize(String value) {
    return value.trim().toUpperCase();
  }
}
