package com.midas.app.services;

import com.midas.app.exceptions.ResourceNotFoundException;
import com.midas.app.models.Account;
import com.midas.app.repositories.AccountRepository;
import com.midas.app.workflows.CreateAccountWorkflow;
import com.midas.app.workflows.UpdateAccountWorkflow;
import com.stripe.exception.StripeException;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.workflow.Workflow;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
  private final Logger logger = Workflow.getLogger(AccountServiceImpl.class);

  private final WorkflowClient workflowClient;

  private final AccountRepository accountRepository;

  /**
   * createAccount creates a new account in the system or provider.
   *
   * @param details is the details of the account to be created.
   * @return Account
   */
  @Override
  public Account createAccount(Account details) throws StripeException {
    details.setCreatedAt(OffsetDateTime.now());
    var options =
        WorkflowOptions.newBuilder()
            .setTaskQueue(CreateAccountWorkflow.QUEUE_NAME)
            .setWorkflowId(details.getEmail())
            .build();

    logger.info("initiating workflow to create account for email: {}", details.getEmail());

    var workflow = workflowClient.newWorkflowStub(CreateAccountWorkflow.class, options);

    return workflow.createAccount(details);
  }

  /**
   * updateAccount updates firstName, lastName, and email of an existing account in the system.
   *
   * @param details is the details of the account(firstName, lastName, and email) to be updated.
   * @return Account
   */
  @Override
  public Account updateAccount(Account details) throws StripeException {
    logger.info("Account details provided to update the profile: {}", details);
    Optional<Account> opAccountFromDb = accountRepository.findById(details.getId());
    Account accountFromDb =
        opAccountFromDb.orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    if (Objects.nonNull(details.getFirstName())) {
      accountFromDb.setFirstName(details.getFirstName());
    }
    if (Objects.nonNull(details.getLastName())) {
      accountFromDb.setLastName(details.getLastName());
    }
    if (Objects.nonNull(details.getEmail())) {
      accountFromDb.setEmail(details.getEmail());
    }
    var options =
        WorkflowOptions.newBuilder()
            .setTaskQueue(UpdateAccountWorkflow.QUEUE_NAME)
            .setWorkflowId(String.valueOf(accountFromDb.getProviderId()))
            .build();
    logger.info("initiating workflow to update account for id: {}", details.getId());
    var workflow = workflowClient.newWorkflowStub(UpdateAccountWorkflow.class, options);
    return workflow.updateAccount(accountFromDb);
  }

  /**
   * getAccounts returns a list of accounts.
   *
   * @return List<Account>
   */
  @Override
  public List<Account> getAccounts() {
    return accountRepository.findAll();
  }
}
