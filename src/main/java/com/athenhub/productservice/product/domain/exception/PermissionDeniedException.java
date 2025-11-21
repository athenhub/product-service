package com.athenhub.productservice.product.domain.exception;

import com.athenhub.commoncore.error.AbstractServiceException;
import com.athenhub.commoncore.error.ErrorCode;

public class PermissionDeniedException extends AbstractServiceException {

  public PermissionDeniedException(ErrorCode errorCode, Object... errorArgs) {
    super(errorCode, errorArgs);
  }
}
