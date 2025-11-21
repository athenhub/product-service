package com.athenhub.productservice.product.domain.dto;

import java.util.UUID;

public record SearchDto(String name, UUID hubId, UUID vendorId, long minPrice, long maxPrice) {}
