# Build a User Registration System with SMS Phone Verification

A Spring Boot application demonstrating user registration, authentication, and SMS phone verification using Twilio Verify.

## Commands

```bash
# Install dependencies
mvn clean install

# Run
mvn spring-boot:run

# Test
mvn test
```

## Environment Variables

Copy `.env.example` to `.env`. Never commit `.env`.

```bash
cp .env.example .env
```

| Variable | Where to find | Format |
| -------- | ------------- | ------ |
| `TWILIO_ACCOUNT_SID` | [Console](https://console.twilio.com) homepage | Starts with `AC` |
| `TWILIO_AUTH_TOKEN` | Console homepage → click to reveal | 32-char string. Treat as a password. |
| `TWILIO_VERIFY_SERVICE_SID` | Console → Verify → Services | Starts with `VA` |

## Project Structure

- `src/main/java/com/twilio/verify/` — Main application code
  - `controller/` — Spring MVC controllers
  - `model/` — JPA entities
  - `repository/` — Spring Data repositories
  - `service/` — Business logic and Twilio integration
  - `config/` — Spring Security configuration
- `src/main/resources/templates/` — Thymeleaf HTML templates
- `src/main/resources/static/css/` — Twilio Paste styling
- `pom.xml` — Maven dependencies

## Agent Boundaries

**Always:**
- Confirm `.env` is configured before running any command
- Use the Environment Variables section to guide the user to each credential — don't ask them to find values without direction
- Confirm the app is running before asking the user to test it

**Never:**
- Run the app with missing or placeholder credentials
- Hardcode credentials or phone numbers in source files
- Skip the `cp .env.example .env` step

## Verify It's Working

1. Open http://localhost:8080 and register a new account with your phone number
2. Submit the registration form — you should receive an SMS with a verification code
3. Enter the code on the verify page — you should see the verified dashboard

## Twilio Resources

- [Twilio Console](https://console.twilio.com) — credentials, phone numbers, webhook configuration
- [Twilio Verify Documentation](https://www.twilio.com/docs/verify)
- [Twilio Java SDK](https://www.twilio.com/docs/libraries/reference/twilio-java)
