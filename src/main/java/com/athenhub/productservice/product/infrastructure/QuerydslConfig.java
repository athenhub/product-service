package com.athenhub.productservice.product.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Querydsl 사용을 위한 설정 클래스.
 *
 * <p>{@link EntityManager}를 주입받아 {@link JPAQueryFactory}를 Bean으로 등록하고, Repository / DAO 계층에서
 * Querydsl 기반 동적 쿼리를 사용할 수 있도록 한다.
 *
 * <p>{@code @PersistenceContext}를 통해 현재 트랜잭션에 바인딩된 영속성 컨텍스트를 주입받는다.
 *
 * @author 김지원
 * @since 1.0.0
 */
@Configuration
public class QuerydslConfig {

  /** 현재 트랜잭션에 연결된 JPA {@link EntityManager}. */
  @PersistenceContext private EntityManager entityManager;

  /**
   * {@link JPAQueryFactory}를 Bean으로 등록한다.
   *
   * <p>Querydsl 기반의 조회(Select) 쿼리를 간결하게 작성하기 위해 사용된다.
   *
   * @return {@link JPAQueryFactory} 인스턴스
   */
  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(entityManager);
  }
}
