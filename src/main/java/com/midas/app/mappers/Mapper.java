package com.midas.app.mappers;

import com.midas.app.models.Account;
import com.midas.app.providers.payment.CreateAccount;
import com.midas.generated.model.AccountDto;
import lombok.NonNull;

public class Mapper {
  // Prevent instantiation
  private Mapper() {}

  /**
   * toAccountDto maps an account to an account dto.
   *
   * @param account is the account to be mapped
   * @return AccountDto
   */
  public static AccountDto toAccountDto(@NonNull Account account) {
    var accountDto = new AccountDto();
    accountDto
        .id(account.getId())
        .firstName(account.getFirstName())
        .lastName(account.getLastName())
        .email(account.getEmail())
        .createdAt(account.getCreatedAt())
        .updatedAt(account.getUpdatedAt())
        .providerId(account.getProviderId())
        .providerType(String.valueOf(account.getProviderType()));
    return accountDto;
  }

  /**
   * createAccountToAccount maps an account to an account.
   *
   * @param createAccount is the system account to be mapped
   * @return Account
   */
  public static Account createAccountToAccount(@NonNull CreateAccount createAccount) {
    Account account = new Account();
    account.setFirstName(createAccount.getFirstName());
    account.setLastName(createAccount.getLastName());
    account.setEmail(createAccount.getEmail());
    account.setProviderId(createAccount.getUserId());
    return account;
  }

  /**
   * accountToCreateAccount maps an account to an CreateAccount.
   *
   * @param account is the account to be mapped
   * @return CreateAccount
   */
  public static CreateAccount accountToCreateAccount(@NonNull Account account) {
    CreateAccount createAccount = new CreateAccount();
    createAccount.setFirstName(account.getFirstName());
    createAccount.setLastName(account.getLastName());
    createAccount.setEmail(account.getEmail());
    createAccount.setUserId(account.getProviderId());
    return createAccount;
  }
}
