package com.athenhub.productservice.product.domain;

import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantCreateCommand;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.ProductColor;
import com.athenhub.productservice.product.domain.vo.ProductSize;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.UUID;

public class ProductFixture {

  public static ProductCreateCommand productCreateCommand(ProductType type) {
    return new ProductCreateCommand(
        HubId.of(UUID.randomUUID()), VendorId.of(UUID.randomUUID()), Price.of(10_000L), type);
  }

  public static ProductVariantCreateCommand productVariantCreateCommand(String color, String size) {
    return new ProductVariantCreateCommand(ProductColor.of(color), ProductSize.of(size));
  }

  public static Product create(
      ProductCreateCommand productCreateRequest,
      ProductVariantCreateCommand... productVariantCreateRequest) {
    Product product = Product.create(productCreateRequest);
    for (ProductVariantCreateCommand variantCreateRequest : productVariantCreateRequest) {
      product.addVariant(ProductVariant.create(variantCreateRequest));
    }
    return product;
  }

  public static Product createProductWithoutVariant() {
    ProductCreateCommand productCreateRequest = productCreateCommand(ProductType.OPTION);
    Product product = Product.create(productCreateRequest);
    ProductVariant productVariant1 = ProductVariant.create(productVariantCreateCommand("RED", "M"));
    ProductVariant productVariant2 =
        ProductVariant.create(productVariantCreateCommand("BLACK", "M"));
    product.addVariant(productVariant1);
    product.addVariant(productVariant2);

    return product;
  }

  public static Product createProductWithVariant() {
    ProductCreateCommand productCreateRequest = productCreateCommand(ProductType.SIMPLE);
    return Product.create(productCreateRequest);
  }
}
