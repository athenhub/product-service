package com.athenhub.productservice.product.domain.dto;

import java.util.List;

public record VariantUpdateSet(
    List<ProductVariantCreateCommand> createCommands,
    List<ProductVariantUpdateCommand> updateCommands,
    List<ProductVariantRemoveCommand> removeCommands) {}
