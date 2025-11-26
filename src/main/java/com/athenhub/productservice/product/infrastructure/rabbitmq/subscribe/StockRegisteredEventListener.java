package com.athenhub.productservice.product.infrastructure.rabbitmq.subscribe;

import com.athenhub.productservice.product.application.service.ProductUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 재고 등록 완료 이벤트를 수신하는 리스너이다.
 *
 * <p>Stock 서비스에서 발행한 {@link StockRegisteredEvent}를 수신하여, 해당 상품을 판매중(ON_SALE) 상태로 전환한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class StockRegisteredEventListener {

  private final ProductUpdateService productUpdateService;

  /**
   * 재고 등록 완료 이벤트를 처리한다.
   *
   * <p>이벤트에 포함된 상품 ID를 기반으로 상품 상태를 판매중(ON_SALE)으로 변경한다.
   *
   * @param event 재고 등록 완료 이벤트
   * @author 김지원
   * @since 1.0.0
   */
  @RabbitListener(queues = "${rabbit.stock.registered.queue}")
  public void listen(StockRegisteredEvent event) {
    productUpdateService.updateToOnSale(event.productId());
  }
}
