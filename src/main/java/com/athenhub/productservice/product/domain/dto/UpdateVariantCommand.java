package com.athenhub.productservice.product.domain.dto;

import com.athenhub.productservice.product.domain.ProductColor;
import com.athenhub.productservice.product.domain.ProductSize;

public record UpdateVariantCommand(ProductSize size, ProductColor color) {}
