package com.athenhub.productservice.product.domain.dto;

import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductFixture;
import com.athenhub.productservice.product.domain.ProductType;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.VendorId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDetailsTest {
    @DisplayName("ProductDetails.from - 도메인 객체를 상세 DTO로 변환한다")
    @Test
    void from() {
        // given
        HubId hubId = HubId.of(UUID.randomUUID());
        VendorId vendorId = VendorId.of(UUID.randomUUID());

        Product product = Product.create(
                new ProductCreateCommand(
                        "테스트 상품",
                        "설명",
                        Price.of(10000),
                        hubId,
                        vendorId,
                        ProductType.OPTION
                )
        );

        product.addVariant(
                ProductFixture.newProductVariantCreateCommand("RED", "M")
        );

        // when
        ProductDetails details = ProductDetails.from(product);

        // then
        assertThat(details.productId()).isEqualTo(product.getId().toUuid());
        assertThat(details.name()).isEqualTo("테스트 상품");
        assertThat(details.hubId()).isEqualTo(hubId.toUuid());
        assertThat(details.vendorId()).isEqualTo(vendorId.toUuid());
        assertThat(details.type()).isEqualTo(ProductType.OPTION);
        assertThat(details.price()).isEqualTo(10000);


        assertThat(details.variants()).hasSize(1);
        assertThat(details.variants().getFirst().color()).isEqualTo("RED");
        assertThat(details.variants().getFirst().size()).isEqualTo("M");
        assertThat(details.variants().getFirst().quantity()).isEqualTo(0);
    }
}