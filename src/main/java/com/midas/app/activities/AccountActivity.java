package com.midas.app.activities;

import com.midas.app.models.Account;
import com.stripe.exception.StripeException;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface AccountActivity {
  /**
   * saveAccount saves an account in the data store.
   *
   * @param account is the account to be saved
   * @return Account
   */
  @ActivityMethod
  Account saveAccount(Account account);

  /**
   * createPaymentAccount creates a payment account in the system or provider.
   *
   * @param account is the account to be created
   * @return Account
   */
  @ActivityMethod
  Account createPaymentAccount(Account account) throws StripeException;

  /**
   * updatePaymentAccount creates a payment account in the system or provider.
   *
   * @param account is the account to be updated
   * @return Account
   */
  @ActivityMethod
  Account updatePaymentAccount(Account account, String assignedId) throws StripeException;
}
