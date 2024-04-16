package com.midas.app.providers.external.stripe;

import com.midas.app.enums.ProviderType;
import com.midas.app.models.Account;
import com.midas.app.providers.payment.CreateAccount;
import com.midas.app.providers.payment.PaymentProvider;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerUpdateParams;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class StripePaymentProvider implements PaymentProvider {
  private final Logger logger = LoggerFactory.getLogger(StripePaymentProvider.class);

  private final StripeConfiguration configuration;

  /** providerName is the name of the payment provider */
  @Override
  public String providerName() {
    return "stripe";
  }

  /**
   * createAccount creates a new account in the payment provider.
   *
   * @param details is the details of the account to be created.
   * @return Account
   */
  @Override
  public Account createAccount(CreateAccount details) throws StripeException {
    Stripe.apiKey = configuration.getApiKey();
    Map<String, Object> params = new HashMap<>();
    params.put("name", (details.getFirstName() + " " + details.getLastName()));
    params.put("email", details.getEmail());
    Customer customer = Customer.create(params);
    return Account.builder()
        .firstName(
            Objects.nonNull(customer.getName().split(" ")[0])
                ? customer.getName().split(" ")[0]
                : null)
        .lastName(
            (customer.getName() != null && customer.getName().split(" ").length > 1)
                ? customer.getName().split(" ")[1]
                : null)
        .email(customer.getEmail())
        .providerId(customer.getId())
        .providerType(ProviderType.STRIPE)
        .createdAt(OffsetDateTime.now())
        .build();
  }

  /**
   * updateAccount updates an existing account in the payment provider.
   *
   * @param details is the details of the account to be updated.
   * @return Account
   */
  @Override
  public Account updateAccount(CreateAccount details, String assignedId) throws StripeException {
    Stripe.apiKey = configuration.getApiKey();
    Customer existingCustomer = Customer.retrieve(assignedId);
    if (Objects.isNull(existingCustomer)) return createAccount(details);
    CustomerUpdateParams params =
        CustomerUpdateParams.builder()
            .setName(details.getFirstName() + " " + details.getLastName())
            .setEmail(details.getEmail())
            .build();
    existingCustomer.update(params);
    return Account.builder()
        .firstName(details.getFirstName())
        .lastName(details.getLastName())
        .email(details.getEmail())
        .providerId(details.getUserId())
        .providerType(ProviderType.STRIPE)
        .updatedAt(OffsetDateTime.now())
        .build();
  }
}
