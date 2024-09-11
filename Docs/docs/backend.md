# Overwiew

The Backend was build using Java with Spring Boot Framework, following the RESTful API principles.

Architecture Layers: `Controller -> Service -> Repository`

The security layer was implemented using JWT (JSON Web Token) for authentication and added to the security filter chain.

## Documentation

The following will be split according to the application's features and endpoints. Testing, wich will be covered later, will also be documented for each feature/endpoint sets

## Testing Approach

The application code will be covered by unit and integration tests. For mocking purposes, the Mockito framework will be used.

JUnit 5 will be used for unit tests and Spring Boot Test will be used for integration tests.

Addiditionally, the testcontainers will be used to cover integration tests with the database identical to the one used in production. Such tests will be written for controllers only to test all levels of the application at once.

## APIs and Endpoints

### Authentication

**Overview and current code coverage** **`(class/method/line)`**

* Controller: `AuthController`  **`(100%/100%/100%)`**
* Services: 
    * `AuthService` via Interface `IAuthService` **`(100%/100%/100%)`**
        * `UserRepository` extends `JpaRepository` 
        * `PasswordEncoder` (BCryptPasswordEncoder managed by Spring as a bean)
        * `UserService` via Interface `IUserService`
        * `AuthenticationManager` (Spring Security)
    * `UserService` vie Interface `IUserService` **`(100%/100%/100%)`**
        * `UserRepository` extends `JpaRepository` **`(100%/100%/100%)`**
        * `PasswordEncoder` (BCryptPasswordEncoder managed by Spring as a bean)
    * `JWTService` **`(100%/100%/100%)`**


#### Register User (open)
**This endpoint will be removed in the future and user management will be done by an admin user via a separate dashboard/endpoint**

* Method: POST
* URL: `/auth/register`
* Request Body: UserDTO object

Request Body Schema (UserDTO)
```json
{
    "username": "string",
    "password": "string",
    "name": "string",
    "email": "string",
    "role": "enum (UserRole)" // RECEPTIONIST, DOCTOR
}
```

* Responses:
    * **201 Created** 
        * Body: User registered successfully
    * **409 Confilct** 
        * Body: Username already taken. Please try again
    * **500 Internal Server Error** 
        * Body: `Error message`

#### Login User (open)

* Method: POST
* URL: `/auth/login`
* Request Body: LoginDTO object

Request Body Schema (LoginDTO)
```json
{
    "username": "string",
    "password": "string"
}
```

* Responses: (Code : Response)
    * **200 OK**:
        * Response Body: LoginResponseDTO object
    * **404 Not Found**:
        * Body: "User not found"
    * **401 Unauthorized**
        * Body: `Error message`

Response Body Schema (LoginResponseDTO)
```json
{
    "token": "string",
    "expiresIn": "long"
}
```

#### Get Authenticated User (protected)

* Method: GET
* URL: `/user`
* Request Headers: Authorization: Bearer (JWT Token)

* Responses:
    * **200 OK**:
        * Body: User object
    * **401 Unauthorized**:
        * Body: `Error message`

Response Body Schema (User)
```json
{
    "id": "long",
    "username": "string",
    "name": "string",
    "email": "string",
    "role": "enum (UserRole)", // RECEPTIONIST, DOCTOR
    "active": "boolean"
}
```