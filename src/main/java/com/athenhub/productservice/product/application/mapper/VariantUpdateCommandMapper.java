package com.athenhub.productservice.product.application.mapper;

import com.athenhub.productservice.product.application.dto.ProductVariantUpdateRequest;
import com.athenhub.productservice.product.domain.dto.ProductVariantCreateCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantRemoveCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantUpdateCommand;
import com.athenhub.productservice.product.domain.dto.VariantUpdateSet;
import com.athenhub.productservice.product.domain.vo.ProductColor;
import com.athenhub.productservice.product.domain.vo.ProductSize;
import com.athenhub.productservice.product.domain.vo.ProductVariantId;
import org.springframework.stereotype.Component;

@Component
public class VariantUpdateCommandMapper {

  public VariantUpdateSet toChangeSet(ProductVariantUpdateRequest request, String username) {
    return new VariantUpdateSet(
        request.adds().stream()
            .map(
                it ->
                    new ProductVariantCreateCommand(
                        ProductColor.of(it.color()), ProductSize.of(it.size())))
            .toList(),
        request.updates().stream()
            .map(
                it ->
                    new ProductVariantUpdateCommand(
                        ProductVariantId.of(it.variantId()),
                        ProductColor.of(it.color()),
                        ProductSize.of(it.size())))
            .toList(),
        request.removes().stream()
            .map(
                it ->
                    new ProductVariantRemoveCommand(ProductVariantId.of(it.variantId()), username))
            .toList());
  }
}
