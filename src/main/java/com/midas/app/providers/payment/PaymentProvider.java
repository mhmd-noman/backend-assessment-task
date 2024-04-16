package com.midas.app.providers.payment;

import com.midas.app.models.Account;
import com.stripe.exception.StripeException;

public interface PaymentProvider {
  /** providerName is the name of the payment provider */
  String providerName();

  /**
   * createAccount creates a new account in the payment provider.
   *
   * @param details is the details of the account to be created.
   * @return Account
   */
  Account createAccount(CreateAccount details) throws StripeException;

  /**
   * updateAccount updates an existing account in the payment provider.
   *
   * @param assignedId is the id, assigned by system/provider to user.
   * @param details is the details of the account to be updated.
   * @return Account
   */
  Account updateAccount(CreateAccount details, String assignedId) throws StripeException;
}
