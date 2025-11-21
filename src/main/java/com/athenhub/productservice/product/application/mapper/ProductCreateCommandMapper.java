package com.athenhub.productservice.product.application.mapper;

import com.athenhub.productservice.product.application.dto.ProductRegisterRequest;
import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantCreateCommand;
import com.athenhub.productservice.product.domain.vo.*;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductCreateCommandMapper {

  public ProductCreateCommand toCreateCommand(ProductRegisterRequest req) {
    return new ProductCreateCommand(
        HubId.of(req.hubId()), VendorId.of(req.vendorId()), Price.of(req.price()), req.type());
  }

  public List<ProductVariantCreateCommand> toVariantCommands(ProductRegisterRequest req) {
    return req.productVariants().stream()
        .map(
            v ->
                new ProductVariantCreateCommand(
                    ProductColor.of(v.color()), ProductSize.of(v.size())))
        .toList();
  }
}
