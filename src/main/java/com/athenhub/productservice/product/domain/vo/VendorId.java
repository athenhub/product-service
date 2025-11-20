package com.athenhub.productservice.product.domain.vo;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 업체(Vendor)를 식별하는 값 객체(Value Object).
 *
 * <p>VendorId는 UUID를 단순히 사용하는 대신, “업체라는 도메인 개념을 명확히 표현하는 타입”으로 분리된 VO이다. 이는 도메인 모델에서 타입
 * 안전성(type-safety)을 확보하고 잘못된 식별자 사용을 방지하기 위한 목적을 가진다.
 *
 * <p>■ 특징
 *
 * <ul>
 *   <li>UUID 기반의 불변 값 객체
 *   <li>JPA에서 @Embeddable 로 값 타입으로 사용
 *   <li>정적 팩토리 메서드 {@link #of(UUID)} 로만 생성 가능
 * </ul>
 *
 * <p>■ 사용 예
 *
 * <pre>
 * VendorId vendorId = VendorId.of(UUID.randomUUID());
 * System.out.println(vendorId); // UUID 문자열 출력
 * </pre>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class VendorId {

  /** 업체 식별자(UUID). */
  private UUID id;

  /**
   * 내부 생성자 — 외부에서는 정적 팩토리 메서드를 사용하도록 강제한다.
   *
   * @param id 값은 null 을 허용하지 않는다.
   */
  private VendorId(UUID id) {
    this.id = Objects.requireNonNull(id);
  }

  /**
   * VendorId 생성 팩토리 메서드.
   *
   * @param uuid 업체 UUID
   * @return VendorId 인스턴스
   */
  public static VendorId of(UUID uuid) {
    return new VendorId(uuid);
  }

  /**
   * UUID를 문자열로 반환한다.
   *
   * <p>로깅 또는 디버깅 시 편의를 위해 오버라이드한다.
   */
  @Override
  public String toString() {
    return id.toString();
  }
}
