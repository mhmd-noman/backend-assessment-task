package com.midas.app.workflows.impl;

import com.midas.app.activities.AccountActivity;
import com.midas.app.models.Account;
import com.midas.app.workflows.UpdateAccountWorkflow;
import com.stripe.exception.StripeException;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import java.time.Duration;

@WorkflowImpl(taskQueues = "update-account-workflow")
public class UpdateAccountWorkflowImpl implements UpdateAccountWorkflow {

  private final RetryOptions customRetryOptions =
      RetryOptions.newBuilder().setMaximumAttempts(3).build();

  private final AccountActivity accountActivity =
      Workflow.newActivityStub(
          AccountActivity.class,
          ActivityOptions.newBuilder()
              .setRetryOptions(customRetryOptions)
              .setStartToCloseTimeout(Duration.ofSeconds(5))
              .build());

  @Override
  public Account updateAccount(Account details) throws StripeException {
    Account account = accountActivity.updatePaymentAccount(details, details.getProviderId());
    account.setId(details.getId());
    return accountActivity.saveAccount(account);
  }
}
