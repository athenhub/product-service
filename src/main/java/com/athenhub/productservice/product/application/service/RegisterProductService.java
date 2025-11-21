package com.athenhub.productservice.product.application.service;

import com.athenhub.productservice.product.application.dto.ProductRegisterRequest;
import com.athenhub.productservice.product.application.dto.ProductResponse;
import com.athenhub.productservice.product.application.mapper.ProductCreateCommandMapper;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductVariant;
import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.dto.ProductVariantCreateCommand;
import com.athenhub.productservice.product.domain.repository.ProductRepository;
import com.athenhub.productservice.product.domain.service.ProductCreatePermissionPolicy;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
public class RegisterProductService {

  private final ProductRepository productRepository;
  private final ProductCreateCommandMapper createCommandMapper;
  private final ProductCreatePermissionPolicy productRoleCheck;

  public ProductResponse register(ProductRegisterRequest request) {
    Product product = createProduct(request);
    addVariants(request, product);

    productRepository.save(product);

    return new ProductResponse(product.getId().toUuid());
  }

  private Product createProduct(ProductRegisterRequest request) {
    ProductCreateCommand productCommand = createCommandMapper.toCreateCommand(request);
    return Product.create(productCommand, productRoleCheck);
  }

  private void addVariants(ProductRegisterRequest request, Product product) {
    List<ProductVariantCreateCommand> variantCommands =
        createCommandMapper.toVariantCommands(request);
    variantCommands.forEach(cmd -> product.addVariant(ProductVariant.create(cmd)));
  }
}
