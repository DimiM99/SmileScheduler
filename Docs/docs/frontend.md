# Overview

The Frontend is built using **React.js** with **Tailwind CSS**, ensuring a modular and maintainable structure that communicates with the backend via RESTful APIs. The project incorporates **Axios** for API requests, with JWT-based authentication using interceptors. The architecture promotes role-based access control and employs **React Context** for state management. This document details the project's technology stack, environment management, API structure, state management, and best practices.

## Technology Stack

- **React**: v18.3.1 – Main library for building the user interface.
- **React Router**: v6.26.1 – Handles client-side navigation and route protection.
- **Axios**: v1.7.7 – Manages HTTP requests to the backend.
- **Tailwind CSS**: v3.4.10 – Utility-first CSS framework for styling.
- **Vite**: v5.4.1 – Frontend tooling for fast builds and hot-reloading.
- **Jest**: v29.7.0 – Testing framework for unit and integration tests.
- **React Testing Library**: v16.0.1 – Used for testing component interactions.

## Environment and Configuration Management

The application uses **environment variables** for configuration, ensuring flexibility across development, staging, and production environments. Variables like API URLs, JWT keys, and secrets are managed via `.env` files.

**Key environment variables**:
- `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB`: Database configuration.
- `RES_API_KEY`, `PS_KEY`, `PS_TOKEN`: External service keys and tokens.
- `BE_PORT`: Backend port configuration.
- `JWT_SECRET_KEY`: Secret key for JWT generation and validation.
- `VITE_CRYPTO_SECRET`: Encryption password used for token storage.

The environment variables are injected into the build process using **Vite**, allowing different configurations based on the environment. Additionally, environment-specific configurations ensure secure handling of sensitive data, such as API keys and JWT secrets.

## Docker Integration

The project includes a **Dockerfile** for containerization, which facilitates consistent environments across different machines:
- **Node.js 18** is used as the base image.
- The app is built using **npm**, and served using **serve**.

This enables easy deployment and ensures that the app runs consistently across various environments.

## API and Services Reference

The application interacts with multiple backend endpoints via services. Each service handles specific logic and API requests, including authentication, user management, appointment handling, and patient scheduling.

### Key Services

 **AuthService**: 

  - Manages user authentication and JWT token handling. 
  - Handles tasks like login, saving and clearing tokens, refreshing tokens, and checking if a user is logged in.
  - Utilizes **AES encryption** for securing sensitive data in `sessionStorage` (e.g., tokens, credentials).
  - Example methods: `login`, `getUser`, `saveToken`, `refreshToken`, `isLoggedIn`.


 **AccountManagementService**: 

  - Handles user-related operations like creating, updating, deleting users, and fetching a list of users.
  - Singleton design pattern is applied for instance management.
  - Example methods: `createUser`, `updateUser`, `deleteUser`, `getUsers`.


 **AppointmentService**: 

  - Manages appointment scheduling, fetching available slots, retrieving appointment details, and updating or deleting appointments.
  - Example methods: `getFreeSlots`, `createAppointment`, `getAppointment`, `updateAppointment`, `deleteAppointment`.


 **PatientScheduleService**: 

  - Fetches patient schedules and upcoming appointments based on the patient's ID and date of birth.
  - Uses **HMAC-SHA256** encryption to securely send patient data in API requests.
  - Example method: `fetchAppointments`.

### Example API Interaction

The `AccountManagementService` provides methods like `createUser`, `updateUser`, and `getUsers`, making API calls to the `/account-management` endpoints. 

All API requests are authenticated using **JWT**, which is securely managed using AES encryption for storage in the browser's `sessionStorage`. Tokens are automatically injected into the request headers via Axios interceptors to ensure secure communication with the backend.

### AuthService Example Interaction
- Upon login, credentials are encrypted and stored in `sessionStorage`, and a JWT token is received from the backend, which is then securely stored.
- The token's expiration is tracked, and if expired, the service will automatically attempt to refresh the token before proceeding with API requests.

## Component Reference

The app is composed of reusable components and follows a clear component hierarchy:

### Key Components

- **Layout**: Provides the overall structure, including navbar and sidebar.
- **AppointmentForm**: Handles the form to create or update appointments.
- **UserList**: Displays a list of users for management.
- **WeekCalendar**: Displays a calendar view for scheduling appointments.
- **LoginForm**: Handles user login and authentication.

### Props

Each component accepts specific props, ensuring flexibility and reusability across the application. For example, `UserList` accepts `users` and `onUserSelect` props, enabling parent components to pass user data and event handlers.

## State Management Details

The project employs a hybrid approach to state management, combining **React Context** for global state with **local state** managed within components using the `useState` hook.

### Global State with React Context

Global state is managed using custom hooks like `useAuth`, which abstracts access to **AuthContext**. This context manages authentication-related data such as user information and login status. Key details of `AuthContext` include:

- **user**: Stores the currently logged-in user's data.
- **login**: A method to authenticate the user and update the state.
- **logout**: A method to clear session data and log out the user.

The `login` and `logout` methods ensure authentication is handled globally across the application, allowing for seamless navigation and secure access to protected routes.

### Local State in Components

For specific page or component-level state, such as managing users in an admin dashboard, local state is handled using the `useState` hook. The `AdminDashboard` component demonstrates how this works in practice. Here's a breakdown of the local state:

- **dashboardState**: This local state manages users, the currently selected user, loading status, and potential errors.
- **handleUserSelect**: Updates the `selectedUser` in the state when a user is selected from the list.
- **fetchUsers**: Asynchronously fetches the list of users and updates the state with the fetched data.
- **handleUserUpdated**: Refreshes the user list after a user is updated, ensuring the UI reflects the latest changes.

### Example: AdminDashboard Component

In the `AdminDashboard`, we manage users with a local state object that stores:

- **users**: The list of users fetched from the `AccountManagementService`.
- **selectedUser**: The user currently selected for editing.
- **loading**: A boolean indicating whether the component is waiting for data.
- **error**: Stores any error message if fetching or updating users fails.

```tsx
const [dashboardState, setDashboardState] = useState({
    users: [] as User[],
    selectedUser: null as User | null,
    loading: true,
    error: null as string | null,
});
```
Data is fetched and state is updated through asynchronous methods, ensuring reactivity within the component while keeping business logic encapsulated in services like `AccountManagementService`.

### Key Points

- **Local State**: Managed in components like `AdminDashboard` using `useState`, enabling fine-grained control over component-specific logic (e.g., user management, loading indicators).

- **Global State**: Managed with **React Context** for authentication, ensuring global accessibility to user login status across all parts of the application.

This hybrid state management approach ensures both flexibility and scalability, allowing you to efficiently manage global and local states in a decoupled and maintainable way.


## Best Practices and Conventions

### Code Style Guidelines

- **Linting**: The project uses **ESLint** with **Prettier** for consistent code formatting, ensuring clean and standardized code.
- **Folder Structure**: 
  - **Components**: Organized in the `components` folder, focusing on UI elements. Subfolders (like `ui` and `Form`) ensure clear separation of individual components.
  - **Services**: Located in the `services` folder, divided into `requests` and `responses` in the `models/services` section for clean API service management.
  - **Utilities and Helpers**: Utility functions and reusable logic reside in `helpers`, categorized by purpose (e.g., `navbar`).
- **Naming Conventions**: 
  - **Variables**: Follow camelCase (e.g., `userName`, `getUserData`).
  - **Components and Classes**: Use PascalCase (e.g., `NavbarComponent`, `AuthService`).

### Component Organization

Components are organized into `ui` for reusable elements (buttons, inputs) and feature-specific folders for more complex components like forms and lists. This structure ensures scalability as the project grows.

## Security Considerations

### JWT Handling and Security

JWT tokens are stored in **session storage** and encrypted using **CryptoJS** to mitigate security risks such as token exposure. The tokens are refreshed automatically upon expiration, ensuring that the user remains authenticated without re-entering credentials.

### Input Validation and Sanitization

All form inputs are validated using **React Hook Form**. Additionally, **Java Percistance API** prevent injection attacks.


### Logging Out After Inactivity

The app includes a mechanism to log out users after a session timeout, enhancing security for role-based systems. Tokens are refreshed upon expiration, but users are logged out if the refresh fails.


## Project Setup and Build Process

The project is built using **Vite** for efficient development workflows, and **TypeScript** for type safety. The build process is optimized with:
- **Development Server**: `vite dev` for local development.
- **Production Build**: `vite build` for optimized production builds.
- **Testing**: Jest is used for running unit tests and generating coverage reports.

The project can be built and served using Docker, making deployment to production environments straightforward and scalable.

