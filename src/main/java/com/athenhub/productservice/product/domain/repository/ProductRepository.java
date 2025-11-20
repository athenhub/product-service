package com.athenhub.productservice.product.domain.repository;

import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.vo.ProductId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 상품(Product) Aggregate Root를 관리하는 JPA 리포지토리.
 *
 * <p>식별자 타입은 값 객체(Value Object)인 {@link ProductId}를 사용한다.
 *
 * @author 김지원
 * @since 1.0.0
 */
public interface ProductRepository extends JpaRepository<Product, ProductId> {}
