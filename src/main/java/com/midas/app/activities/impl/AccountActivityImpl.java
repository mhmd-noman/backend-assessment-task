package com.midas.app.activities.impl;

import com.midas.app.activities.AccountActivity;
import com.midas.app.mappers.Mapper;
import com.midas.app.models.Account;
import com.midas.app.providers.payment.PaymentProvider;
import com.midas.app.repositories.AccountRepository;
import com.stripe.exception.StripeException;
import io.temporal.spring.boot.ActivityImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
@ActivityImpl(taskQueues = {"create-account-workflow", "update-account-workflow"})
public class AccountActivityImpl implements AccountActivity {

  private final PaymentProvider paymentProvider;

  @Autowired AccountRepository accountRepository;

  /**
   * saveAccount saves an account in the data store.
   *
   * @param account is the account to be saved
   * @return Account
   */
  @Override
  public Account saveAccount(Account account) {
    return accountRepository.save(account);
  }

  /**
   * createPaymentAccount creates a payment account in the system or provider.
   *
   * @param account is the account to be created
   * @return Account
   */
  @Override
  public Account createPaymentAccount(Account account) throws StripeException {
    return paymentProvider.createAccount(Mapper.accountToCreateAccount(account));
  }

  /**
   * updatePaymentAccount updated an existing payment account in the system or provider.
   *
   * @param account is the account to be updated
   * @return Account
   */
  @Override
  public Account updatePaymentAccount(Account account, String assignedId) throws StripeException {
    return paymentProvider.updateAccount(Mapper.accountToCreateAccount(account), assignedId);
  }
}
