package com.athenhub.productservice.product.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.athenhub.commoncore.error.GlobalErrorCode;
import com.athenhub.productservice.membership.domain.MemberRole;
import com.athenhub.productservice.membership.domain.MemberRoles;
import com.athenhub.productservice.product.application.dto.ProductSummary;
import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.application.service.strategy.ProductQueryStrategy;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductType;
import com.athenhub.productservice.product.domain.dto.MemberInfo;
import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.service.MembershipProvider;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductQueryApplicationServiceTest {

  @Mock private ProductQueryService productQueryService;
  @Mock private MembershipProvider membershipProvider;

  @Mock private ProductQueryStrategy hubMangerStrategy;
  @Mock private ProductQueryStrategy masterMangerStrategy;
  @Mock private ProductQueryStrategy vendorAgentStrategy;

  @InjectMocks private ProductQueryApplicationService productApplicationService;

  private UUID userId;
  private HubId hubId;
  private VendorId vendorId;
  private Product product;
  private Pageable pageable;
  private MemberInfo memberInfo;
  private Page<Product> productPage;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    hubId = HubId.of(UUID.randomUUID());
    vendorId = VendorId.of(UUID.randomUUID());
    pageable = PageRequest.of(0, 10);

    memberInfo = new MemberInfo(hubId, vendorId);

    ProductCreateCommand command =
        new ProductCreateCommand(
            "테스트 상품", "설명", Price.of(1000L), hubId, vendorId, ProductType.SIMPLE);

    product = Product.create(command);

    productPage = new PageImpl<>(List.of(product));

    productApplicationService =
        new ProductQueryApplicationService(
            membershipProvider,
            List.of(masterMangerStrategy, hubMangerStrategy, vendorAgentStrategy));
  }

  @Test
  @DisplayName("MASTER_MANAGER는 전체 상품을 가져온다.")
  void getProductsManagedBy_MASTER_MANAGER() {
    // given
    MemberRoles roles = MemberRoles.of(List.of(MemberRole.MASTER_MANAGER));

    when(membershipProvider.getMember(userId)).thenReturn(memberInfo);
    when(masterMangerStrategy.query(memberInfo, pageable)).thenReturn(productPage);

    when(masterMangerStrategy.supports(roles)).thenReturn(true);

    // when
    Page<ProductSummary> result =
        productApplicationService.getProductsManagedBy(userId, roles, pageable);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  @DisplayName("HUB_MANAGER는 자신의 허브에 속한 상품을 가져온다.")
  void getProductsManagedBy_HUB_MANAGER() {
    // given
    MemberRoles roles = MemberRoles.of(List.of(MemberRole.HUB_MANAGER));

    when(membershipProvider.getMember(userId)).thenReturn(memberInfo);
    when(hubMangerStrategy.query(memberInfo, pageable)).thenReturn(productPage);

    when(hubMangerStrategy.supports(roles)).thenReturn(true);
    when(masterMangerStrategy.supports(roles)).thenReturn(false);

    // when
    Page<ProductSummary> result =
        productApplicationService.getProductsManagedBy(userId, roles, pageable);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  @DisplayName("VENDOR_AGENT는 자신의 업체에 속한 상품을 가져온다.")
  void getProductsManagedBy_VENDOR_AGENT() {
    // given
    MemberRoles roles = MemberRoles.of(List.of(MemberRole.VENDOR_AGENT));

    when(membershipProvider.getMember(userId)).thenReturn(memberInfo);
    when(vendorAgentStrategy.query(memberInfo, pageable)).thenReturn(productPage);

    when(vendorAgentStrategy.supports(roles)).thenReturn(true);
    when(masterMangerStrategy.supports(roles)).thenReturn(false);
    when(hubMangerStrategy.supports(roles)).thenReturn(false);

    // when
    Page<ProductSummary> result =
        productApplicationService.getProductsManagedBy(userId, roles, pageable);

    // then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  @DisplayName("권한이 없으면 실패한다.")
  void getProductsManagedBy_no_permission_fail() {
    // given
    MemberRoles roles = MemberRoles.of(List.of(MemberRole.USER));

    when(membershipProvider.getMember(userId)).thenReturn(memberInfo);

    when(vendorAgentStrategy.supports(roles)).thenReturn(false);
    when(masterMangerStrategy.supports(roles)).thenReturn(false);
    when(hubMangerStrategy.supports(roles)).thenReturn(false);

    // when & then
    assertThatThrownBy(
            () -> productApplicationService.getProductsManagedBy(userId, roles, pageable))
        .isInstanceOf(ProductServiceException.class)
        .satisfies(
            e -> {
              ProductServiceException ex = (ProductServiceException) e;
              assertThat(ex.getCode()).isEqualTo(GlobalErrorCode.FORBIDDEN.name());
            });
  }
}
