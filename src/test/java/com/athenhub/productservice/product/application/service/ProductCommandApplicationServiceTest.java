package com.athenhub.productservice.product.application.service;

import static com.athenhub.productservice.product.application.exception.ProductServiceErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.athenhub.productservice.product.application.dto.*;
import com.athenhub.productservice.product.application.exception.ProductServiceException;
import com.athenhub.productservice.product.domain.Product;
import com.athenhub.productservice.product.domain.ProductType;
import com.athenhub.productservice.product.domain.dto.ProductCreateCommand;
import com.athenhub.productservice.product.domain.service.PermissionPolicy;
import com.athenhub.productservice.product.domain.vo.HubId;
import com.athenhub.productservice.product.domain.vo.Price;
import com.athenhub.productservice.product.domain.vo.VendorId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductCommandApplicationServiceTest {

  @Mock private ProductRegisterService productRegisterService;
  @Mock private ProductUpdateService productUpdateService;
  @Mock private ProductDeleteService productDeleteService;
  @Mock private ProductQueryService productQueryService;
  @Mock private PermissionPolicy permissionPolicy;

  @InjectMocks private ProductCommandApplicationService productApplicationService;

  private UUID userId;
  private HubId hubId;
  private VendorId vendorId;
  private String username;
  private Product product;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    hubId = HubId.of(UUID.randomUUID());
    vendorId = VendorId.of(UUID.randomUUID());
    username = "tester";

    ProductCreateCommand command =
        new ProductCreateCommand(
            "테스트 상품", "설명", Price.of(1000L), hubId, vendorId, ProductType.SIMPLE);

    product = Product.create(command);
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
}
