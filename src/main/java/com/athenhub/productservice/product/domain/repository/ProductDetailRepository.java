package com.athenhub.productservice.product.domain.repository;

import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.dto.SearchDto;
import org.springframework.data.domain.Page;

public interface ProductDetailRepository {

  Page<Product> findAll(SearchDto search, int page, int size);
}
