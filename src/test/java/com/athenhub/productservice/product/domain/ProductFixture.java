package com.athenhub.productservice.product.domain;

import com.athenhub.productservice.product.domain.dto.ProductCreateRequest;
import com.athenhub.productservice.product.domain.dto.ProductVariantCreateRequest;
import java.util.UUID;

public class ProductFixture {

  public static ProductCreateRequest productCreateRequest(ProductType type) {
    return new ProductCreateRequest(UUID.randomUUID(), UUID.randomUUID(), 10_000L, type);
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

  public static Product createWithoutVariant() {
    ProductCreateRequest productCreateRequest = productCreateRequest(ProductType.OPTION);
    Product product = Product.create(productCreateRequest);
    ProductVariant productVariant1 =
        ProductVariant.create(new ProductVariantCreateRequest(ProductColor.RED, ProductSize.M));
    ProductVariant productVariant2 =
        ProductVariant.create(new ProductVariantCreateRequest(ProductColor.BLACK, ProductSize.M));
    product.addVariant(productVariant1);
    product.addVariant(productVariant2);

    return product;
  }

  public static Product createWithVariant() {
    ProductCreateRequest productCreateRequest = productCreateRequest(ProductType.SIMPLE);
    return Product.create(productCreateRequest);
  }
}
