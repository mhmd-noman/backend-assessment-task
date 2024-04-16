package com.midas.app.workflows.impl;

import com.midas.app.activities.AccountActivity;
import com.midas.app.models.Account;
import com.midas.app.workflows.CreateAccountWorkflow;
import com.stripe.exception.StripeException;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import java.time.Duration;

@WorkflowImpl(taskQueues = "create-account-workflow")
public class CreateAccountWorkflowImpl implements CreateAccountWorkflow {

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
  public Account createAccount(Account details) throws StripeException {
    Account account = accountActivity.createPaymentAccount(details);
    return accountActivity.saveAccount(account);
  }
}
