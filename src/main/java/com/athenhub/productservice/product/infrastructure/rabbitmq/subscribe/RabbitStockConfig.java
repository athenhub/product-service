package com.athenhub.productservice.product.infrastructure.rabbitmq.subscribe;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Stock 도메인 관련 RabbitMQ 설정.
 *
 * <p>재고 서비스(Stock Service)에서 발행하는 이벤트를 상품 서비스(Product Service)에서 구독하기 위한 Exchange / Queue /
 * Binding을 설정한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitStockProperties.class)
public class RabbitStockConfig {

  private final RabbitStockProperties stockProperties;

  /**
   * 재고(Stock) 관련 이벤트를 수신하기 위한 Topic Exchange.
   *
   * <p>예시: stock.exchange
   *
   * @return Stock 이벤트용 TopicExchange
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public TopicExchange stockExchange() {
    return new TopicExchange(
        stockProperties.getExchange(), // 예: stock.exchange
        true,
        false);
  }

  /**
   * 재고 등록 이벤트(StockRegisteredEvent)를 수신하는 큐.
   *
   * <p>재고 서비스에서 "재고 등록 완료" 이벤트가 발행되면 이 큐를 통해 상품 서비스가 메시지를 수신한다.
   *
   * @return 재고 등록 이벤트 수신용 Queue
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Queue stockRegisteredQueue() {
    return QueueBuilder.durable(stockProperties.getRegistered().getQueue()).build();
  }

  /**
   * 재고 등록 이벤트용 Binding.
   *
   * <p>stock.exchange 에서 stock.registered 라우팅 키로 들어오는 메시지를 stock.registered.queue 로 전달한다.
   *
   * @param stockRegisteredQueue 재고 등록 이벤트 큐
   * @param stockExchange Stock 토픽 익스체인지
   * @return Queue 와 Exchange 를 연결하는 Binding
   * @author 김지원
   * @since 1.0.0
   */
  @Bean
  public Binding stockRegisteredBinding(Queue stockRegisteredQueue, TopicExchange stockExchange) {

    return BindingBuilder.bind(stockRegisteredQueue)
        .to(stockExchange)
        .with(stockProperties.getRegistered().getRoutingKey());
  }
}
