# PawnPrime Backend — Quick Guide for AI Agents

This guide gives AI assistants enough context to understand and work with the backend quickly and safely.

## Stack
- Language: Java 17
- Framework: Spring Boot 3.5.4 (Web, Security, Data JPA, Validation)
- Database: PostgreSQL (JPA/Hibernate)
- AuthN/AuthZ: JWT (jjwt 0.11.5), role-based access (ADMIN, AGENT, …)
- Integrations: Razorpay (online repayments), Cloudinary (media), JavaMail (OTP via email), Twilio (optional)

Key dependencies: see `pom.xml` (jjwt, razorpay-java, cloudinary-http44, jasperreports, itext7).

## App Entry & Structure
- Main class: `com.project.pawnprime.PawnPrimeApplication`
- Packages: `controller/`, `service/`, `repo/`, `model/`, `security/`, `config/`

## Security
- Config: `config/SecurityConfig.java`
  - CORS: allows `http://localhost:3000`
  - Public endpoints:
    - `/api/auth/**` (login + OTP verify)
    - `/api/customers/otp/send`, `/api/customers/otp/verify` (customer OTP, if used)
  - Everything else requires JWT bearer token
  - Filter: `security.JwtAuthenticationFilter` validates JWT and sets SecurityContext

- JWT utility: `security.JwtUtil`
  - Token contains subject (username/email) and role claim

## Authentication Flows
- Admin login: `POST /api/auth/admin/login`
  - Body: `{ username, password }`
  - Response: `{ token, username, role }` on success

- Agent login + email OTP: `POST /api/auth/agent/login`
  - Body: `{ username, password }` (username = email)
  - Sends a 4-digit OTP to agent email (JavaMailSender)
  - Response: `{ success: true, message: 'OTP sent' }`

- Agent verify OTP: `POST /api/auth/agent/verify-otp`
  - Body: `{ email, otp }`
  - Response: `{ token, email, role, agentId }`

Note: OTPs are stored in-memory (ConcurrentHashMap) — ephemeral, for dev/demo. Use Redis/DB for prod.

## Domain Model (selected)
- `Loan` (id, loanVal, loanStatus, customer, agent, …)
- `RepaymentTransaction` (loan, totalAmt, …) — offline repayments
- `OnlineRepaymentTransaction` (loan, amount, status, orderId, paymentId)
- `Customer`, `Agent`, `Admin`

## Persistence
- Repositories in `repo/` (Spring Data JPA)
- PostgreSQL configured via `application.properties` (not included here; expect standard `spring.datasource.*` and `spring.jpa.*` settings)

## Business Services (selected)
- `LoanService` — create/list/close loans
- `RepaymentTransactionService` — offline repayments
- `OnlineRepaymentService` — Razorpay order flow and confirmations; helper `getAllRepayments()` for reporting

## Key Controllers (selected)
- `AuthController` — admin/agent auth + OTP
- `LoanController`, `LoanTransactionController`, `CloseLoanController` — loan lifecycle
- `RepaymentTransactionController`, `OnlineRepaymentController` — repayments
- `AdminLoanChartController`
  - `GET /api/admin/loan-charts` (ADMIN only)
  - Returns:
    - `byAgentCount`: `[ { name: agentName, value: count } ]`
    - `byAgentApproved`: `[ { name: agentName, value: count } ]` where status in {approved, active, t_done}
    - `cumulativeSeries`: `[ { label: loanIdString, invested: cumulative, recovered: cumulative } ]`
  - Implementation aggregates repayments from both offline and online sources.

## Conventions & Notes
- Roles are enforced via `@PreAuthorize` and/or HTTP path rules.
- DTOs live under `dto/` (e.g., `dto.adminDTO.*`, `dto.agentDTO.*`).
- Error handling is basic (ResponseEntity with message); consider `@ControllerAdvice` for richer APIs.
- CORS is limited to localhost by default; adjust in `SecurityConfig` for deployments.
- Twilio is present but optional; WhatsApp/SMS OTP can be wired in `CustomerController`/`CustomerService` if needed.

## Typical Client Usage
- Include `Authorization: Bearer <jwt>` header for all protected endpoints.
- Admin-only charts: GET `/api/admin/loan-charts`
- Loan operations, repayments: see controller classes in `controller/` for paths and verbs.

## Quick Build/Run
- Build: `./mvnw clean package`
- Run: `./mvnw spring-boot:run`
- Java 17 required.

## Safe assumptions for AI agents
- Prefer existing services/controllers; maintain public APIs.
- Avoid removing imports; add only what’s needed.
- If adding endpoints, align with existing path patterns under `/api/...` and consistent DTOs.
- For reporting, prefer service-level aggregation (don’t query repositories directly in controllers).
