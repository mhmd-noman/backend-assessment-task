package com.midas.app.controllers;

import com.midas.app.enums.ProviderType;
import com.midas.app.exceptions.GlobalExceptionHandler;
import com.midas.app.mappers.Mapper;
import com.midas.app.models.Account;
import com.midas.app.services.AccountService;
import com.midas.generated.api.AccountsApi;
import com.midas.generated.model.AccountDto;
import com.midas.generated.model.CreateAccountDto;
import com.midas.generated.model.UpdateAccountDto;
import com.stripe.exception.StripeException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AccountController implements AccountsApi {
  private final AccountService accountService;
  private final GlobalExceptionHandler globalExceptionHandler;
  private final Logger logger = LoggerFactory.getLogger(AccountController.class);

  /**
   * POST /accounts : Create a new user account Creates a new user account with the given details
   * and attaches a supported payment provider such as &#39;stripe&#39;.
   *
   * @param createAccountDto User account details (required)
   * @return User account created (status code 201)
   */
  @Override
  public ResponseEntity<AccountDto> createUserAccount(CreateAccountDto createAccountDto) {
    logger.info("Creating account for user with email: {}", createAccountDto.getEmail());
    Account account = null;
    try {
      account =
          accountService.createAccount(
              Account.builder()
                  .firstName(createAccountDto.getFirstName())
                  .lastName(createAccountDto.getLastName())
                  .email(createAccountDto.getEmail())
                  .providerId(createAccountDto.getProviderId())
                  .providerType(
                      (Objects.nonNull(createAccountDto.getProviderType())
                              && ProviderType.STRIPE
                                  .toString()
                                  .equalsIgnoreCase(createAccountDto.getProviderType()))
                          ? ProviderType.STRIPE
                          : null)
                  .build());
    } catch (StripeException e) {
      globalExceptionHandler.handleAll(e, null);
    }

    return new ResponseEntity<>(Mapper.toAccountDto(account), HttpStatus.CREATED);
  }

  /**
   * GET /accounts : Get list of user accounts Returns a list of user accounts.
   *
   * @return List of user accounts (status code 200)
   */
  @Override
  public ResponseEntity<List<AccountDto>> getUserAccounts() {
    logger.info("Retrieving all accounts");

    var accounts = accountService.getAccounts();
    var accountsDto = accounts.stream().map(Mapper::toAccountDto).toList();

    return new ResponseEntity<>(accountsDto, HttpStatus.OK);
  }

  /**
   * PATCH /accounts : Update account details Update the firstName, lastName, and email of the
   * specified account.
   *
   * @param accountId ID of the account to update (required)
   * @param updateAccountDto Updated account details (required)
   * @return Account details updated successfully (status code 200) or Bad request (status code 400)
   *     or Unauthorized (status code 401) or Forbidden (status code 403) or Not found (status code
   *     404) or Internal server error (status code 500)
   */
  @Override
  public ResponseEntity<AccountDto> updateUserAccount(
      String accountId, UpdateAccountDto updateAccountDto) {
    logger.info("Updating account for user with account id: {}", accountId);
    Account account = null;
    try {
      account =
          accountService.updateAccount(
              Account.builder()
                  .id(UUID.fromString(accountId))
                  .firstName(updateAccountDto.getFirstName())
                  .lastName(updateAccountDto.getLastName())
                  .email(updateAccountDto.getEmail())
                  .build());
    } catch (StripeException e) {
      globalExceptionHandler.handleAll(e, null);
    }
    return new ResponseEntity<>(Mapper.toAccountDto(account), HttpStatus.OK);
  }
}
