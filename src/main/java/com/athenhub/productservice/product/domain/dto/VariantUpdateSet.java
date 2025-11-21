package com.athenhub.productservice.product.domain.dto;

import java.util.List;

/**
 * 상품 옵션(ProductVariant)에 대한 변경 내역 묶음(Change Set)을 표현하는 DTO.
 *
 * <p>상품 수정 시 옵션에 대해 수행해야 할 다음 작업들을 하나의 객체로 관리한다.
 *
 * <ul>
 *   <li>생성(Create)
 *   <li>수정(Update)
 *   <li>삭제(Remove)
 * </ul>
 *
 * <p>애플리케이션/도메인 계층에서 옵션 변경 로직을 일관된 방식으로 처리하기 위해 전달되는 데이터 구조이며, 각 리스트는 독립적으로 비어 있을 수 있다.
 *
 * @param createCommands 생성할 상품 옵션 목록
 * @param updateCommands 수정할 상품 옵션 목록
 * @param removeCommands 삭제할 상품 옵션 목록
 * @author 김지원
 * @since 1.0.0
 */
public record VariantUpdateSet(
    List<ProductVariantCreateCommand> createCommands,
    List<ProductVariantUpdateCommand> updateCommands,
    List<ProductVariantRemoveCommand> removeCommands) {}
