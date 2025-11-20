package com.athenhub.productservice.product.domain;

import com.athenhub.productservice.product.domain.dto.ProductCreateRequest;
import com.athenhub.productservice.product.domain.dto.ProductVariantCreateRequest;
import com.athenhub.productservice.product.domain.vo.ProductColor;
import com.athenhub.productservice.product.domain.vo.ProductSize;
import java.util.UUID;

public class ProductFixture {

  public static ProductCreateRequest productCreateRequest(ProductType type) {
    return new ProductCreateRequest(UUID.randomUUID(), UUID.randomUUID(), 10_000L, type);
  }

  public static ProductVariantCreateRequest productVariantCreateRequest(String color, String size) {
    return new ProductVariantCreateRequest(ProductColor.of(color), ProductSize.of(size));
  }

  public static Product create(
      ProductCreateRequest productCreateRequest,
      ProductVariantCreateRequest... productVariantCreateRequest) {
    Product product = Product.create(productCreateRequest);
    for (ProductVariantCreateRequest variantCreateRequest : productVariantCreateRequest) {
      product.addVariant(ProductVariant.create(variantCreateRequest));
    }
    return product;
  }

  public static Product createProductWithoutVariant() {
    ProductCreateRequest productCreateRequest = productCreateRequest(ProductType.OPTION);
    Product product = Product.create(productCreateRequest);
    ProductVariant productVariant1 = ProductVariant.create(productVariantCreateRequest("RED", "M"));
    ProductVariant productVariant2 =
        ProductVariant.create(productVariantCreateRequest("BLACK", "M"));
    product.addVariant(productVariant1);
    product.addVariant(productVariant2);

    return product;
  }

  public static Product createProductWithVariant() {
    ProductCreateRequest productCreateRequest = productCreateRequest(ProductType.SIMPLE);
    return Product.create(productCreateRequest);
  }
}
