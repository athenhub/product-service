package com.athenhub.productservice.product.domain.dto;

import java.util.List;

public record VariantChangeSet(
    List<ProductVariantCreateCommand> createCommands,
    List<ProductVariantUpdateCommand> updateCommands,
    List<ProductVariantRemoveCommand> removeCommands) {}
