package com.athenhub.productservice.product.domain.vo;

import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 허브(Hub)의 고유 식별자를 나타내는 값 객체(Value Object).
 *
 * <p>HubId는 단순한 UUID가 아닌, "허브라는 도메인 개념을 명확하게 식별"하기 위해 별도의 VO로 분리된 타입이다. 이는 도메인의 명확성 강화와 타입
 * 안전성(type-safety)을 제공하며, 잘못된 식별자 사용을 방지하는 데 기여한다.
 *
 * <p>■ 특징
 *
 * <ul>
 *   <li>UUID 기반의 불변(Immutable) 값 객체
 *   <li>@Embeddable을 통해 JPA에서 값 타입으로 매핑
 *   <li>정적 팩토리 메서드 {@link #of(UUID)} 로만 생성 가능
 * </ul>
 *
 * <p>■ 사용 예
 *
 * <pre>
 * HubId hubId = HubId.of(UUID.randomUUID());
 * UUID raw = hubId.toUuid();
 * </pre>
 *
 * @author 김지원
 * @since 1.0.0
 */
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubId {

  /** 허브를 식별하는 UUID. */
  private UUID id;

  /** 내부 생성자. 외부 생성은 정적 팩토리 메서드를 통해 제한한다. */
  private HubId(UUID id) {
    this.id = id;
  }

  /**
   * 저장된 UUID 값을 반환한다.
   *
   * @return UUID 값
   */
  public UUID toUuid() {
    return id;
  }

  /**
   * HubId 생성 팩토리 메서드.
   *
   * @param uuid 허브 식별용 UUID
   * @return 새로운 HubId 인스턴스
   */
  public static HubId of(UUID uuid) {
    return new HubId(uuid);
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
