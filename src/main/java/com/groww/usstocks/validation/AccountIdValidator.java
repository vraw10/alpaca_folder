package com.groww.usstocks.validation;

import com.groww.usstocks.exception.RequestValidationException;
import org.springframework.stereotype.Component;

/**
 * Shared validator for account ID — used by all services that require an account context.
 */
@Component
public class AccountIdValidator {

    public void validate(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new RequestValidationException("account_id must not be blank.");
        }
    }
}
