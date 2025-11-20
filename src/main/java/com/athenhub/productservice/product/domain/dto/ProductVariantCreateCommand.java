package com.athenhub.productservice.product.domain.dto;

import com.athenhub.productservice.product.domain.vo.ProductColor;
import com.athenhub.productservice.product.domain.vo.ProductSize;

/**
 * 상품 옵션(ProductVariant) 생성을 위한 도메인 명령(Command) 객체이다.
 *
 * <p>옵션 생성 시 필요한 색상(Color) 및 사이즈(Size) 정보를 캡슐화하여 도메인 엔티티({@link
 * com.athenhub.productservice.product.domain.ProductVariant})로 전달하는 역할을 수행한다.
 *
 * <p>Controller 또는 Application 계층에서 받은 입력값을 도메인 모델이 이해할 수 있는 형태로 변환하는 과정에서 생성되며, 각 필드는 이미 정규화/유효성
 * 검증이 완료된 VO(Value Object)로 구성된다.
 *
 * <h3>포함 정보</h3>
 *
 * <ul>
 *   <li>{@link ProductColor} : 옵션 색상 정보
 *   <li>{@link ProductSize} : 옵션 사이즈 정보
 * </ul>
 *
 * <p>본 Command는 단순한 데이터 전달(Param Object) 목적이며, 세부 도메인 규칙은 ProductColor / ProductSize 내부 또는
 * ProductAggregate(Product) 내부에서 처리된다.
 *
 * <p>해당 클래스는 불변(Immutable) 구조로 설계되었으며, 생성 이후 그 상태가 변경될 수 없다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public record ProductVariantCreateCommand(ProductColor color, ProductSize size) {}
