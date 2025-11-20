package com.athenhub.productservice.product.domain.dto;

import com.athenhub.productservice.product.domain.ProductType;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.VendorId;

/**
 * 상품 생성을 위한 도메인 명령(Command) 객체이다.
 *
 * <p>Controller 또는 Application 계층에서 전달된 입력값을 도메인 모델이 이해할 수 있는 형태로 변환하여 전달하는 용도로 사용된다. 도메인
 * 엔티티({@link com.athenhub.productservice.product.domain.Product})는 외부의 기술적 DTO를 직접 참조하지 않고, 오직 도메인
 * 전용 Command 객체를 통해 생성 로직을 수행한다.
 *
 * <h3>포함 정보</h3>
 *
 * <ul>
 *   <li>{@link HubId} : 상품이 속한 허브 정보
 *   <li>{@link VendorId} : 공급사(Vendor) 식별자
 *   <li>{@link Price} : 상품 기본 가격
 *   <li>{@link ProductType} : 상품 유형 (NORMAL / OPTION)
 * </ul>
 *
 * <p>Command 객체는 단순한 값 전달(Param Object) 역할을 수행하며, 상세 도메인 규칙 및 유효성 검증은 각각의 VO(HubId, VendorId,
 * Price) 또는 ProductType 자체에서 담당한다.
 *
 * <p>해당 클래스는 불변(Immutable) 구조로 설계되었으며, 생성 이후 그 상태가 변경될 수 없다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public record ProductCreateCommand(HubId hubId, VendorId vendorId, Price price, ProductType type) {}
