package com.athenhub.productservice.product.infrastructure.rabbitmq.subscribe;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 재고 등록 완료를 알리는 이벤트이다.
 *
 * <p>재고 서비스(Stock Service)에서 특정 상품의 재고가 성공적으로 등록된 후 발행되며, 상품 서비스가 이를 구독하여 후속 처리를 수행할 수 있도록 사용된다.
 *
 * <p>주요 용도:
 *
 * <ul>
 *   <li>상품 상태 변경(예: 대기 → 판매중)
 *   <li>상품-재고 연동 상태 반영
 *   <li>이벤트 발생 시점 기록
 * </ul>
 *
 * @param productId 재고가 등록된 상품 ID
 * @param requestAt 이벤트 발생 시각
 * @author 김지원
 * @since 1.0.0
 */
public record StockRegisteredEvent(UUID productId, LocalDateTime requestAt) {}
