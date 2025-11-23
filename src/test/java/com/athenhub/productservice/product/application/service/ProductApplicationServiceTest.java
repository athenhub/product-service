package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.athenhub.commoncore.error.GlobalErrorCode;
import com.athenhub.productservice.membership.domain.MemberRole;
import com.athenhub.productservice.membership.domain.MemberRoles;
import com.athenhub.productservice.product.application.dto.*;
import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductType;
import com.athenhub.productservice.product.domain.dto.MemberInfo;
import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.service.MembershipProvider;
import com.athenhub.productservice.product.domain.service.PermissionPolicy;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
class ProductApplicationServiceTest {

  @Mock private ProductRegisterService productRegisterService;
  @Mock private ProductUpdateService productUpdateService;
  @Mock private ProductDeleteService productDeleteService;
  @Mock private ProductQueryService productQueryService;
  @Mock private PermissionPolicy permissionPolicy;
  @Mock private MembershipProvider membershipProvider;

  @InjectMocks private ProductApplicationService productApplicationService;

  private UUID userId;
  private HubId hubId;
  private VendorId vendorId;
  private String username;
  private Pageable pageable;
  private MemberInfo memberInfo;
  private Product product;
  private Page<Product> productPage;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    hubId = HubId.of(UUID.randomUUID());
    vendorId = VendorId.of(UUID.randomUUID());
    username = "tester";
    pageable = PageRequest.of(0, 10);

    memberInfo = new MemberInfo(hubId, vendorId);

    ProductCreateCommand command =
        new ProductCreateCommand(
            "테스트 상품", "설명", Price.of(1000L), hubId, vendorId, ProductType.SIMPLE);

    product = Product.create(command);

    productPage = new PageImpl<>(List.of(product));
  }

  @Nested
  class RegisterTest {

    @Test
    void success() {
      // given
      ProductRegisterRequest request = mock(ProductRegisterRequest.class);

      when(request.hubId()).thenReturn(hubId.toUuid());
      when(request.vendorId()).thenReturn(vendorId.toUuid());

      when(permissionPolicy.isCreateDenied(any(), any(), any())).thenReturn(false);
      when(productRegisterService.register(request)).thenReturn(mock(ProductResponse.class));

      // when
      ProductResponse result = productApplicationService.register(request, userId);

      // then
      assertThat(result).isNotNull();
      verify(productRegisterService).register(request);
    }

    @Test
    void no_permission_fail() {
      // given
      ProductRegisterRequest request = mock(ProductRegisterRequest.class);

      when(request.hubId()).thenReturn(hubId.toUuid());
      when(request.vendorId()).thenReturn(vendorId.toUuid());

      when(permissionPolicy.isCreateDenied(any(), any(), any())).thenReturn(true);

      // when & then
      assertThatThrownBy(() -> productApplicationService.register(request, userId))
          .isInstanceOf(ProductServiceException.class)
          .satisfies(
              e -> {
                ProductServiceException ex = (ProductServiceException) e;
                assertThat(ex.getCode()).isEqualTo(CREATE_NOT_ALLOWED.name());
              });
    }
  }

  @Nested
  class UpdateBasicInfoTest {

    @Test
    void success() {
      // given
      ProductBasicUpdateRequest request = mock(ProductBasicUpdateRequest.class);
      UUID productId = UUID.randomUUID();

      when(request.productId()).thenReturn(productId);
      when(productQueryService.get(productId)).thenReturn(product);
      when(permissionPolicy.isUpdateDenied(any(), any(), any())).thenReturn(false);
      when(productUpdateService.updateBasicInfo(request)).thenReturn(mock(ProductResponse.class));

      // when
      ProductResponse result = productApplicationService.updateBasicInfo(request, userId);

      // then
      assertThat(result).isNotNull();
      verify(productUpdateService).updateBasicInfo(request);
    }

    @Test
    void no_permission_fail() {
      // given
      ProductBasicUpdateRequest request = mock(ProductBasicUpdateRequest.class);

      when(request.productId()).thenReturn(UUID.randomUUID());
      when(productQueryService.get(any())).thenReturn(product);
      when(permissionPolicy.isUpdateDenied(any(), any(), any())).thenReturn(true);

      // when & then
      assertThatThrownBy(() -> productApplicationService.updateBasicInfo(request, userId))
          .satisfies(
              e -> {
                ProductServiceException ex = (ProductServiceException) e;
                assertThat(ex.getCode()).isEqualTo(UPDATE_NOT_ALLOWED.name());
              });
    }
  }

  @Nested
  class UpdateVariantsTest {

    @Test
    void success() {
      // given
      ProductVariantUpdateRequest request = mock(ProductVariantUpdateRequest.class);

      when(request.productId()).thenReturn(UUID.randomUUID());
      when(productQueryService.get(any())).thenReturn(product);
      when(permissionPolicy.isUpdateDenied(any(), any(), any())).thenReturn(false);
      when(productUpdateService.updateProductVariant(any(), any()))
          .thenReturn(mock(ProductResponse.class));

      // when
      ProductResponse result = productApplicationService.updateVariants(request, userId, username);

      // then
      assertThat(result).isNotNull();
      verify(productUpdateService).updateProductVariant(request, username);
    }

    @Test
    void no_permission_fail() {
      // given
      ProductVariantUpdateRequest request = mock(ProductVariantUpdateRequest.class);

      when(request.productId()).thenReturn(UUID.randomUUID());
      when(productQueryService.get(any())).thenReturn(product);
      when(permissionPolicy.isUpdateDenied(any(), any(), any())).thenReturn(true);

      // when & then
      assertThatThrownBy(() -> productApplicationService.updateVariants(request, userId, username))
          .isInstanceOf(ProductServiceException.class)
          .satisfies(
              e -> {
                ProductServiceException ex = (ProductServiceException) e;
                assertThat(ex.getCode()).isEqualTo(UPDATE_NOT_ALLOWED.name());
              });
    }
  }

  @Nested
  class DeleteTest {

    @Test
    void success() {
      // given
      UUID productId = UUID.randomUUID();

      when(productQueryService.get(productId)).thenReturn(product);
      when(permissionPolicy.isDeleteDenied(any(), any())).thenReturn(false);

      // when
      productApplicationService.delete(productId, userId, username);

      // then
      verify(productDeleteService).delete(productId, username);
    }

    @Test
    void no_permission_fail() {
      // given
      UUID productId = UUID.randomUUID();

      when(productQueryService.get(productId)).thenReturn(product);
      when(permissionPolicy.isDeleteDenied(any(), any())).thenReturn(true);

      // when & then
      assertThatThrownBy(() -> productApplicationService.delete(productId, userId, username))
          .isInstanceOf(ProductServiceException.class)
          .satisfies(
              e -> {
                ProductServiceException ex = (ProductServiceException) e;
                assertThat(ex.getCode()).isEqualTo(DELETE_NOT_ALLOWED.name());
              });
    }
  }

  @Nested
  class GetProductsManagedByTest {

    @Test
    @DisplayName("MASTER_MANAGER는 전체 상품을 가져온다.")
    void MASTER_MANAGER() {
      // given
      MemberRoles roles = MemberRoles.of(List.of(MemberRole.MASTER_MANAGER));

      when(membershipProvider.getMember(userId)).thenReturn(memberInfo);
      when(productQueryService.getAll(pageable)).thenReturn(productPage);

      // when
      Page<ProductSummary> result =
          productApplicationService.getProductsManagedBy(userId, roles, pageable);

      // then
      assertThat(result.getContent()).hasSize(1);
      verify(productQueryService).getAll(pageable);
    }

    @Test
    @DisplayName("HUB_MANAGER는 자신의 허브에 속한 상품을 가져온다.")
    void HUB_MANAGER() {
      // given
      MemberRoles roles = MemberRoles.of(List.of(MemberRole.HUB_MANAGER));

      when(membershipProvider.getMember(userId)).thenReturn(memberInfo);
      when(productQueryService.getByHubId(hubId, pageable)).thenReturn(productPage);

      // when
      Page<ProductSummary> result =
          productApplicationService.getProductsManagedBy(userId, roles, pageable);

      // then
      assertThat(result.getContent()).hasSize(1);
      verify(productQueryService).getByHubId(hubId, pageable);
    }

    @Test
    @DisplayName("VENDOR_AGENT는 자신의 업체에 속한 상품을 가져온다.")
    void VENDOR_AGENT() {
      // given
      MemberRoles roles = MemberRoles.of(List.of(MemberRole.VENDOR_AGENT));

      when(membershipProvider.getMember(userId)).thenReturn(memberInfo);
      when(productQueryService.getByVendorId(vendorId, pageable)).thenReturn(productPage);

      // when
      Page<ProductSummary> result =
          productApplicationService.getProductsManagedBy(userId, roles, pageable);

      // then
      assertThat(result.getContent()).hasSize(1);
      verify(productQueryService).getByVendorId(vendorId, pageable);
    }

    @Test
    @DisplayName("권한이 없으면 실패한다.")
    void no_permission_fail() {
      // given
      MemberRoles roles = MemberRoles.of(List.of(MemberRole.USER));

      when(membershipProvider.getMember(userId)).thenReturn(memberInfo);

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
}
