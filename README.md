# Altaira Workspace

Altaira Workspace is a university project by Adnanne Bourhayal. It helps a small consultancy take an enquiry from its website, agree what work to do and give the customer a private space to follow delivery. The backend is built with Java 21, Spring Boot and PostgreSQL. The web interface uses Next.js and TypeScript.

## Follow a customer through the workspace

Imagine a bicycle workshop asks for an online booking system. A consultant reviews the enquiry and approves a delivery plan. Once payment is confirmed, the workspace links a client record to the original enquiry and assigns the agreed booking service. The owner can then receive an invitation to their private workspace. The owner can now review the work for their business. A second person can have read-only access.

| Step | What happens | Where to see it |
| --- | --- | --- |
| Enquiry | The visitor submits the form. Staff see the enquiry in their sales inbox. | [Public form and lead lifecycle](docs/core-client-service-flow.md) |
| Client and service | The consultant approves the plan. Payment confirmation activates the client workspace and the selected services. | [Lead intake and conversion](docs/admin-lead-intake-conversion.md) |
| Private workspace | The client can review their own work. A viewer cannot change it. | [Membership checks](backend/src/main/java/com/altaira/backend/security/ClientAccessService.java) · [Permission tests](backend/src/test/java/com/altaira/backend/security/ClientAccessServiceTests.java) |

The [public website](https://altairalabs.vercel.app/) shows the visitor experience. The staff and client workspaces require a sign-in. You can explore the [guided workspace walkthrough](docs/client-portal-admin-crm-demo.md) and run the acceptance tests below without a live account.

## Run the workflow locally

From the root of the repository, use a JDK 21 installation and the included Maven wrapper. On Windows, use `mvnw.cmd` in place of `./mvnw`.

```sh
cd backend
./mvnw -Dtest=CommercialFlowEndToEndAcceptanceTests,ClientAccessServiceTests test
```

The commercial test takes sample intake data through payment confirmation, workspace activation and a provisioning preview. It uses a disposable database and local test doubles. The permission tests cover a client who can write, a viewer with read-only access and a session that must explicitly select a workspace.

## Find your way through the code

| Area | What it owns |
| --- | --- |
| [Next.js application](app) | Public pages, consultant screens, client pages and the server-side routes that call the API |
| [HTTP controllers](backend/src/main/java/com/altaira/backend/controller) | Input validation and the HTTP contract used by the web interface |
| [Services](backend/src/main/java/com/altaira/backend/service) and [security](backend/src/main/java/com/altaira/backend/security) | Conversion, service assignment, onboarding, sessions and access to each client's data |
| [Entities](backend/src/main/java/com/altaira/backend/entity) and [repositories](backend/src/main/java/com/altaira/backend/repository) | Persistent records and their PostgreSQL queries |
| [Planning rules](backend/src/main/java/com/altaira/backend/provisioning) | How the services agreed with a client become proposed tools, human tasks and resource requests |

For local setup, follow the [installation guide](docs/setup.md). For a map of the runtime boundaries, start with the [class reference](docs/technical-class-reference.md). The [provider capability matrix](docs/architecture/provider-automation-status.md) explains which integrations produce previews and which support a guarded external action.

## Tests and JavaDoc

From `backend/`, run the full test suite and build the implementation and test references.

```sh
./mvnw test
./mvnw javadoc:javadoc javadoc:test-javadoc
```

Open `backend/target/reports/apidocs/index.html` for the implementation and `backend/target/reports/testapidocs/index.html` for the tests. From the repository root, `java scripts/CheckJavaDocs.java` checks that every declared type, method, field and record component has an explanation. The JavaDoc includes private helpers and nested types so a student can trace a request from a controller through its rules into storage. See [testing and validation](docs/testing.md) for the frontend checks and local smoke tests.
