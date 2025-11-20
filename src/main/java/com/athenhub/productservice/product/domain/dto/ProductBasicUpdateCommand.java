package com.athenhub.productservice.product.domain.dto;

import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.VendorId;

/**
 * 상품의 기본 정보를 변경하기 위한 도메인 명령(Command) 객체이다.
 *
 * <p>Controller 혹은 Application 계층에서 전달된 입력값을 도메인 계층에서 이해할 수 있는 형태로 변환하여 캡슐화한다. 도메인 엔티티({@link
 * com.athenhub.productservice.product.domain.Product})는 외부의 기술적 DTO를 직접 참조하지 않고, 오직 도메인 전용 Command를
 * 통해 상태 변경을 수행한다.
 *
 * <h3>포함 정보</h3>
 *
 * <ul>
 *   <li>{@link HubId} : 상품이 소속된 허브 정보
 *   <li>{@link VendorId} : 공급사(Vendor) 식별자
 *   <li>{@link Price} : 상품의 기본 가격
 * </ul>
 *
 * <p>이 객체는 단순한 값 전달(Param Object)을 위한 용도로만 사용되며, 도메인 규칙 및 검증 로직은 각 VO(HubId, VendorId, Price) 내부에서
 * 수행된다.
 *
 * <p>Command 객체는 불변(Immutable) 구조이며, 생성 이후 변경될 수 없다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public record ProductBasicUpdateCommand(HubId hubId, VendorId vendorId, Price price) {}
