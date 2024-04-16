# Getting Started

**IMPORTANT: Do not send pull requests to this repository. This is a template repository and is not used for grading. Any pull requests will be closed and ignored.**

## Introduction

If you are reading this, you are probably have received this project as a coding challenge. Please read the instructions
carefully and follow the steps below to get started.

## Setup

### Pre-requisities

To run the application you would require:

- [Java](https://www.azul.com/downloads/#zulu)
- [Temporal](https://docs.temporal.io/cli#install)
- [Docker](https://docs.docker.com/get-docker/)
- [Stripe API Keys](https://stripe.com/docs/keys)

### On macOS:

First, you need to install Java 21 or later. You can download it from [Azul](https://www.azul.com/downloads/#zulu) or
use [SDKMAN](https://sdkman.io/).

```sh
brew install --cask zulu21
```

You can install Temporal using Homebrew

```sh
brew install temporal
```

or visit [Temporal Installation](https://docs.temporal.io/cli#install) for more information.

You can install Docker using Homebrew

```sh
brew install docker
```

or visit [Docker Installation](https://docs.docker.com/get-docker/) for more information.

### Other platforms

Please check the official documentation for the installation of Java, Temporal, and Docker for your platform.

### Stripe API Keys

Sign up for a Stripe account and get your API keys from the [Stripe Dashboard](https://dashboard.stripe.com/apikeys).
Then in `application.properties` file add the following line with your secret key.

```properties
stripe.api-key=sk_test_51J3j
```

## Run

You are required to first start the temporal server using the following command

```sh
temporal server start-dev
```

and then run the application using the following command or using your IDE.

```sh
./gradlew bootRun
```

### Other commands

#### Lint
To run lint checks, use the following command

```sh
./gradlew sonarlintMain
```

#### Code Formatting
To format the code, use the following command

```sh
./gradlew spotlessApply
```

## Guides

The following guides illustrate how to use some features concretely:

- [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
- [Temporal Quick Start](https://docs.temporal.io/docs/quick-start)
- [Temporal Java SDK Quick Guide](https://docs.temporal.io/dev-guide/java)
- [Stripe Quick Start](https://stripe.com/docs/quickstart)
- [Stripe Java SDK](https://stripe.com/docs/api/java)

### Docker Compose support

This project contains a Docker Compose file named `compose.yaml`.
In this file, the following services have been defined:

- postgres: [`postgres:latest`](https://hub.docker.com/_/postgres)

Please review the tags of the used images and set them to the same as you're running in production.



### Prerequisites (mentioned in detail ReadMe provided with project.):
Following are required as part of environment.
    • Gradle
    • Java 21
    • Docker
    • Temporal workflow

### Test Cases Execution:
	Executing following would result in execution of test cases:
    • com.midas.app.test.StripeIntegrationTest

### Brief explanation of your implementation:
### Utilized Stripe Integration for Customer Creation:
		Integrated Stripe functionality into our system to facilitate the creation of customer accounts and seamless updates to their information.
		Implemented robust retry mechanisms within the CreateAccountWorkflow and UpdateAccountWorkflow Temporal workflows for resilient handling of customer data operations in Stripe.

### Tracking Workflow Status
		Workflow statuses through the Temporal UI portal, accessible at localhost:8080, offering visibility into the progression of customer data operations.

### Enhanced API Support:
		Introduced a PATCH endpoint, /accounts/{accountId}, enabling partial updates to customer account information within both our internal system and external provider systems. This update includes the integration of providerId and providerType fields into the Account entity and relevant OpenAPI response DTOs for streamlined data management.

###S treamlined Deployment with Docker:
		Streamlined deployment processes with the addition of Dockerfile profiles tailored for staging and production environments. Furthermore, updated postgres image tags ensure compatibility and efficiency across deployment setups.

### Ensuring Integrity through Integration Testing
		Implemented integration tests to verify the seamless functioning of the Stripe Integration workflow, ensuring that customer data operations remain intact throughout system updates.