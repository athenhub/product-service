package com.athenhub.productservice.product.domain;

import com.athenhub.productservice.global.domain.AbstractAuditEntity;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.ProductId;
import com.athenhub.productservice.product.domain.vo.VendorId;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends AbstractAuditEntity {

  @EmbeddedId private ProductId id;

  @Embedded private HubId hubId;

  @Embedded private VendorId vendorId;

  @Embedded private Price price;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProductVariant> variants = new ArrayList<>();

  private boolean active;

  private Product(ProductId id, HubId hubId, VendorId vendorId, Price price) {
    this.id = id;
    this.hubId = hubId;
    this.vendorId = vendorId;
    this.price = price;
    this.active = true;
  }

  public static Product create(HubId hubId, VendorId vendorId, Price price) {
    return new Product(ProductId.create(), hubId, vendorId, price);
  }

  public void addVariant(ProductVariant variant) {
    variant.assignTo(this);
    this.variants.add(variant);
  }
}
