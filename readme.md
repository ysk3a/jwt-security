#  Secure your REST API with JWT: (Auth + Roles + Refresh) in Spring Security
- `youtube.com/watch?v=clMI_jhQcR4`
- Prerequ:
  - note the application.properties having the properties for postgres. we also need to have the database called jwt-security
    - if jwt-security database does not exist, open psql or whatever sql client to execute `create database jwt-security;` then you can run this spring boot app
      - you can then view the generated tables(entities) in pgAdmin gui Servers>PostgresSQL 15>Databases>jwt-security>schemas>public>tables to view the tables
        - or run `SELECT * FROM public.application_user ORDER BY id ASC LIMIT 100`, `SELECT * FROM public.products ORDER BY id ASC LIMIT 100`
      - Or if using Beekeeper Studio Community, connect to postgres under jwt-security > public for the tables
    - might want to add some dummy data to see rows for testing sql snippets or few endpoint response 
      - but if a row is added manually it should be removed since it will conflict with `@Id @GeneratedValue(strategy = GenerationType.SEQUENCE)` in the models.
- one of many videos that shows visually the flow: `youtu.be/clMI_jhQcR4?t=2988`
- secret key to be used in application.properties, lets generate one using the git bash terminal with `openssl rand -base64 32`. 256-bits or 32bytes.
- note that we told spring boot in SecurityConfig to allow everyone to access certain endpoint. normally you could add quick deterrents like 
  - CAPTCHA so only humans get through, rate-limit (e.g., 5 hits per minute per IP) to slow bots, and a tiny secret header/token that only my frontend sends, with CORS locked to my domain.

# limitations in this project:
- No logic to handle when user logs out, or we need to revoke a token as this project has no way to know if a token should be valid
- 

# JWT Security Project `github.com/learnwithiftekhar/Spring-Security-JWT`

This project demonstrates a secure REST API built with Spring Boot, utilizing JSON Web Tokens (JWT) for authentication and authorization. It features user registration, login, token refresh, and a basic product management system.

## Features

* **User Registration:** Allows new users to register with a username, password, and role.
* **User Login:** Authenticates users and returns an access token and a refresh token.
* **Token Refresh:** Provides a mechanism to refresh access tokens using refresh tokens.
* **JWT-Based Authentication:** Secures API endpoints using JWTs.
* **Role-Based Authorization:**  Supports different user roles (though not explicitly demonstrated in the provided code).
* **Product Management:** Basic CRUD operations for products (create, read, update, delete).
* **PostgreSQL Database:** Stores user and product data in a PostgreSQL database.
* **Validation:** Implements input validation for request payloads.

## Technologies

* **Spring Boot:** The core framework for building the application.
* **Spring Security:** Handles authentication and authorization.
* **Spring Data JPA:** Simplifies database interactions.
* **PostgreSQL:** The relational database management system.
* **JSON Web Tokens (JWT):** Used for secure authentication.
* **io.jsonwebtoken (JJWT):** A library for creating and parsing JWTs.
* **Lombok:** Reduces boilerplate code.
* **Maven:** Build tool for dependency management and project building.
* Java 17 or higher

## Setup and Installation

1. **Prerequisites:**
   * Java Development Kit (JDK) 17 or higher
   * Maven
   * PostgreSQL database installed and running.

2. **Database Configuration:**
   * Open `src/main/resources/application.properties`.
   * Update the PostgreSQL connection details (`spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`) to match your database setup.
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/<DATABASE-NAME>
     spring.datasource.username=<USERNAME>
     spring.datasource.password=<PASSWORD>
     ```
   * The application is set to create and drop tables on start up, using: `spring.jpa.hibernate.ddl-auto=create-drop`. Change to `spring.jpa.hibernate.ddl-auto=update` if you wish the data to be persisted.

3. **JWT Secret:**
    *   The `app.jwt.secret` property in `application.properties` contains a long, randomly generated string. **Keep this secret safe and secure in production.**
     ```properties
     app.jwt.secret=very-secure-and-complex-key-that-is-at-least-256-bits-long-for-production
     ```

4. **Build and Run:**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

   The application will start on `http://localhost:8080` (default Spring Boot port).

## API Endpoints

* **POST /api/auth/register:**
    * Registers a new user.
    * new row should be inserted in postgres with hashed password

* **POST /api/auth/login:**
    * Request body is json that matches LoginRequest dto
    * Logs in a user and returns JWT tokens.
    * In bruno rest client folder, contains test script lines to use variables in other endpoint request

* **POST /api/auth/refresh-token:**
    * Refreshes an access token using a refresh token by sending a request with body matching RefreshTokenRequest
    * make sure in rest client, auth is not inherited so it doesn't send expired access token to here

* **Product related endpoints**:
    * Delete product by id.
    * Requires a valid access token.

## Further Development

* More detailed error handling:** Improve error handling and provide more informative error messages.
* **Comprehensive testing:** Add unit and integration tests.
* **Enhanced security:** Consider additional security measures, such as input sanitization and rate limiting.
* **Role based authorization**
* **Validation for all entities**
* **Product update**


***
Question:
- where to store tokens?
  - db?
  - stateless meaning here?
- tokens need to be hidden?
- how to prevent user from spam log in?

***
- Note regarding security, in these development/demo examples csrf is disabled. normally you don't want this and handle it properly!
- csrf in SecurityConfig
  -  technically rest api don't need csrf protection while jwt since each request will need a valid jwt token, jwt token already protect us(?) from these(?) attacks already

***
- Role names I could look into to expand levels of privileges/authority for fun:
```txt
ROLE_AGENT
ROLE_ALCHEMIST
ROLE_ANALYST
ROLE_ARCHITECT
ROLE_ATTACK
ROLE_CATALYST
ROLE_CONDUCTOR
ROLE_CONTRIBUTOR
ROLE_DEVELOPER
ROLE_DOMAIN_ADMIN
ROLE_EDITOR
ROLE_EMERGENCY
ROLE_EXECUTIVE
ROLE_GUARDIAN
ROLE_GUEST
ROLE_LEAD
ROLE_LOCAL_ADMIN
ROLE_MAESTRO
ROLE_MANAGER
ROLE_MEMBER
ROLE_MODERATOR
ROLE_NAVIGATOR
ROLE_ORACLE
ROLE_OWNER
ROLE_PATHFINDER
ROLE_PIONEER
ROLE_POWER_USER
ROLE_SCRIBE
ROLE_SECURITY
ROLE_SENTINEL
ROLE_SERVICE
ROLE_STANDARD_USER
ROLE_SUBSCRIBER
ROLE_TEMPORARY
ROLE_THREAT
ROLE_TRAILBLAZER
ROLE_USER
ROLE_VIEWER
ROLE_VIRTUOSO
ROLE_WARDEN
```