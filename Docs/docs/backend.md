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

The generation of the test coverage reports will be done using the IntelliJ IDEA IDE.

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


#### Register User (open) (**Removed**) 

* <del>Method: POST<del>
* <del>URL: `/auth/register`<del>
* <del>Request Body: UserDTO object<del>

> **The user registration functionality has been moved to accounnt management endpoint (adding new users stricly as admin)**


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

### Account Management

**Overview and current code coverage** **`(class/method/line)`**

* Controller: `AccountManagementController`  **`(100%/100%/100%)`**
* Services: 
    * `AMService` via Interface `IAccountManagement` **`(100%/100%/100%)`**
        * `UserRepository` extends `JpaRepository`  **`(100%/100%/100%)`**
        * `PasswordEncoder` (BCryptPasswordEncoder managed by Spring as a bean)
        * `UserService` via Interface `IUserService` **`(100%/100%/100%)`**

#### Create User (protected)

* Method: POST
* URL: `/account-management/user`
* Request Body: UserDTO object

Request Body Schema (UserDTO)
```json
{
    "username": "string",
    "password": "string",
    "name": "string",
    "email": "string",
    "role": "enum (UserRole)", // RECEPTIONIST, DOCTOR
    "active": "boolean"
}
```

* Responses:
    * **201 Created**:
        * Body: User object
    * **403 Fobidden**:
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

#### Edit a User (protected)

* Method: PUT
* URL: `/account-management/user`
* Request Body: UserDTO object

Request Body Schema (UserDTO)
```json
{
    "id": "long",
    "username": "string",
    "password": "string",
    "name": "string",
    "email": "string",
    "role": "enum (UserRole)", // RECEPTIONIST, DOCTOR
    "active": "boolean"
}
```

* Responses:
    * **200 OK**:
        * Body: User object
    * **403 Fobidden**:
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

#### Delete a User (protected)

* Method: DELETE
* URL: `/account-management/user`
* Request Body: UserDTO object

Request Body Schema (UserDTO)
```json
{
    "id": "long",
    "username": "string",
    "password": "string",
    "name": "string",
    "email": "string",
    "role": "enum (UserRole)", // RECEPTIONIST, DOCTOR
    "active": "boolean"
}
```

* Responses:
    * **200 OK**:
        * Body: "User deleted"
    * **403 Fobidden**:
        * Body: `Error message`

#### Get All Users (protected)

* Method: GET
* URL: `/account-management/users`

* Responses:
    * **200 OK**:
        * Body: List<User> object
    * **403 Fobidden**:
        * Body: `Error message`


### Scheduling API

**Overview and current code coverage** **`(class/method/line)`**
	
* Controller: AppointmentController  **`(100%/100%/100%)`**
    * Services:
	    * AppointmentService via Interface IAppointmentService **`(100%/100%/100%)`**
            * AppointmentRepository **`(100%/100%/100%)`**
            * UserRepository **`(100%/100%/100%)`**
            * PatientRepository **`(100%/100%/100%)`**

#### Get Free Appointment Slots

* Method: GET
* URL: `/api/appointments/free-slots`
* Query Parameters:
	* doctorId (long): The ID of the doctor
	* date (string): The date in ISO format (YYYY-MM-DD)
	* weekView (boolean, optional): Whether to return slots for the whole week (default: false)
	* appointmentType (string): The type of appointment
* Responses:
    * **200 OK**:
        * Body: List of LocalDateTime objects
    * **400 Bad Request**:
        * Body: Error message

Response Body Schema

```
[
    "2024-09-23T10:00:00",
    "2024-09-23T11:00:00",
    // ... more datetime strings
]
```

#### Get Appointment Details

* Method: GET
* URL: `/api/appointments/booked`
* Query Parameters:
	* doctorId (long): The ID of the doctor
	* date (string): The date in ISO format (YYYY-MM-DD)
	* weekView (boolean): Whether to return slots for the whole week (default: false)
* Responses:
    * **200 OK**:
        * Body: List of Appointment objects
    * **400 Bad Request**:
        * Body: Error message

Response Body Schema (List of Appointment ojects)
``` json
{
    [
        {
            "id": "long",
            "title": "string",
            "start": "string (ISO datetime)",
            "appointmentType": "string (QUICKCHECK, EXTENSIVE, SURGERY)",
            "end": "string (ISO datetime)",
            "doctor": {
                "id": "long",
                "username": "string",
                "name": "string",
                "email": "string",
                "role": "string (RECEPTIONIST, DOCTOR, ADMIN, PATIENT)",
                "active": "boolean"
            },
            "patient": {
                "id": "long",
                "name": "string",
                "birthdate": "string (ISO date)",
                "insuranceNumber": "string",
                "insuranceProvider": "string",
                "email": "string",
                "phoneNumer": "string"
            }
        },
        // ... more appointment objects
    ]
}
```

#### Schedule Appointment

* Method: POST
* URL: `/api/appointments`
* Request Body: NewAppointmentDTO object

Request Body Schema (NewAppointmentDTO)
``` json
{
    "title": "string",
    "doctorId": "long",
    "start": "string (ISO datetime)",
    "appointmentType": "string (QUICKCHECK, EXTENSIVE, SURGERY)",
    "patient": {
        "id": "long (optional)", // If the patient is already in the system if not set, the patient will be created
        "name": "string",
        "birthdate": "string (ISO date)",
        "insuranceNumber": "string",
        "insuranceProvider": "string",
        "email": "string",
        "phoneNumer": "string"
    }
}
```

* Responses:
    * **200 OK**:
        * Body: Appointment object
    * **403 Forbidden**:
        * Body: Error message

Response Body Schema (Appointment)
``` json
{
    "id": "long",
    "title": "string",
    "start": "string (ISO datetime)",
    "appointmentType": "string (QUICKCHECK, EXTENSIVE, SURGERY)",
    "end": "string (ISO datetime)",
    "doctor": {
        "id": "long",
        "username": "string",
        "name": "string",
        "email": "string",
        "role": "string (RECEPTIONIST, DOCTOR, ADMIN, PATIENT)",
        "active": "boolean"
    },
    "patient": {
        "id": "long",
        "name": "string",
        "birthdate": "string (ISO date)",
        "insuranceNumber": "string",
        "insuranceProvider": "string",
        "email": "string",
        "phoneNumer": "string"
    }
}
```

#### Get Appointment Details

* Method: GET
* URL: `/api/appointments`
* Query Parameters:
    * appointmentId (long): The ID of the appointment
* Responses:
    * **200 OK**:
        * Body: Appointment object
    * **404 Not Found**:
        * Body: "The appointment with the provided id does not exist"

Response Body Schema (Appointment)
``` json
{
    "id": "long",
    "title": "string",
    "start": "string (ISO datetime)",
    "appointmentType": "string (QUICKCHECK, EXTENSIVE, SURGERY)",
    "end": "string (ISO datetime)",
    "doctor": {
        "id": "long",
        "username": "string",
        "name": "string",
        "email": "string",
        "role": "string (RECEPTIONIST, DOCTOR, ADMIN, PATIENT)",
        "active": "boolean"
    },
    "patient": {
        "id": "long",
        "name": "string",
        "birthdate": "string (ISO date)",
        "insuranceNumber": "string",
        "insuranceProvider": "string",
        "email": "string",
        "phoneNumer": "string"
    }
}
```

#### Change Appointment

* Method: PUT
* URL: `/api/appointments`
* Request Body: AppointmentDTO object

Request Body Schema (AppointmentDTO) // for optional fields - if set to null, the values will not be changed if set to something, the values will be updated
``` json
{
    "id": "long", // required
    "title": "string", // optional
    "patientId": "long", // makes no difference
    "doctorId": "long", // optional
    "start": "string (ISO datetime)", // optional
    "appointmentType": "string (QUICKCHECK, EXTENSIVE, SURGERY)" // optional
}

```

* Responses:
    * **200 OK**:
        * Body: Appointment object
    * **404 Not Found**:
        * Body: "The appointment with the provided id does not exist"
    * **403 Forbidden**:
        * Body: Error message

Response Body Schema (Appointment)
``` json
{
    "id": "long",
    "title": "string",
    "start": "string (ISO datetime)",
    "appointmentType": "string (QUICKCHECK, EXTENSIVE, SURGERY)",
    "end": "string (ISO datetime)",
    "doctor": {
        "id": "long",
        "username": "string",
        "name": "string",
        "email": "string",
        "role": "string (RECEPTIONIST, DOCTOR, ADMIN, PATIENT)",
        "active": "boolean"
    },
    "patient": {
        "id": "long",
        "name": "string",
        "birthdate": "string (ISO date)",
        "insuranceNumber": "string",
        "insuranceProvider": "string",
        "email": "string",
        "phoneNumer": "string"
    }
}
```

#### Cancel Appointment

* Method: DELETE
* URL: `/api/appointments/{appointmentId}`
* Path Variables:
    * appointmentId (long): The ID of the appointment to cancel
* Responses:
    * 200 OK:
        * Body: None


### Patient Management API

**Overview and current code coverage** **`(class/method/line)`**

* Controller: PatientController  **`(100%/100%/100%)`**
    * Services:
	    * PatientService via Interface IPatientService **`(100%/100%/100%)`**
            * PatientRepository **`(100%/100%/100%)`**


#### Search Patient by Insurance

* Method: GET
* URL: `/api/patients/search`
* Query Parameters:
	* insuranceNumber (string): The insurance number of the patient
* Responses:
	* **200 OK**:
	    * Body: Patient object
	* **404 Not Found**:
	    * Body: None

Response Body Schema (Patient)
``` json
{
    "id": "long",
    "name": "string",
    "birthdate": "string (ISO date)",
    "insuranceNumber": "string",
    "insuranceProvider": "string",
    "email": "string",
    "phoneNumer": "string"
}
```


#### Update a Patient

* Method: GET
* URL: `/api/patients`
* Request Body: patientDTO object
* Responses:
	* **200 OK**:
	    * Body: Patient object
	* **404 Not Found**:
	    * Body: Error message

Request Body Schema (PatientDTO)
``` json
{
    "id": "long",
    "name": "string",
    "birthdate": "string (ISO date)",
    "insuranceNumber": "string",
    "insuranceProvider": "string",
    "email": "string",
    "phoneNumer": "string"
}
```

Response Body Schema (Patient)
``` json
{
    "id": "long",
    "name": "string",
    "birthdate": "string (ISO date)",
    "insuranceNumber": "string",
    "insuranceProvider": "string",
    "email": "string",
    "phoneNumer": "string"
}
```

### Patient Schedule API

**Overview and current code coverage** **`(class/method/line)`**

* Controller: PatientScheduleController  **`(100%/100%/100%)`**
    * Services:
        * PatientScheduleService via Interface IPatientScheduleService **`(100%/100%/100%)`**
            * AppointmentRepository **`(100%/100%/100%)`**
            * PatientRepository **`(100%/100%/100%)`**

#### Get Patient Schedule

* Method: GET
* URL: `/api/patient-schedule`
* Requset Body: PatientScheduleRequestDTO object
* Responses:
    * **200 OK**:
        * Body: List of Appointment objects
    * **400 Bad Request**:
        * Body: Error message

Request Body Schema (PatientScheduleRequestDTO)
``` json
{
    "receivedHash": "String",
    "patientId": "long",
    "patientDateOfBirth": "string (ISO date)"
}
```

Response Body Schema (List of Appointment objects)
``` json
{
    [
        {
            "id": "long",
            "title": "string",
            "start": "string (ISO datetime)",
            "appointmentType": "string (QUICKCHECK, EXTENSIVE, SURGERY)",
            "end": "string (ISO datetime)",
            "doctor": {
                "id": "long",
                "username": "string",
                "name": "string",
                "email": "string",
                "role": "string (RECEPTIONIST, DOCTOR, ADMIN, PATIENT)",
                "active": "boolean"
            },
            "patient": {
                "id": "long",
                "name": "string",
                "birthdate": "string (ISO date)",
                "insuranceNumber": "string",
                "insuranceProvider": "string",
                "email": "string",
                "phoneNumer": "string"
            }
        },
        // ... more appointment objects
    ]
}
```