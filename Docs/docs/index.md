#  Introduction

This is the documentation for the Smile Scheduler Application, wich was created in the context of the XU-EL-30 module. 
The go point of the application was this [pdf](files/EL30%20-%20Project.pdf).

## Dream Team

* [Dmitri Moscoglo](https://github.com/DimiM99)
* [Veit Brunnhuber](https://github.com/AV3NII)

## Functional Requirements

Following Fiunctional Requirements were indentified from the dialog in said pdf:

* Manual appointment scheduling by receptionist
    * Appointment types: quick checkup, extensive care, operation
    * Appointment durations: 30 minutes, 30 minutes, 1 hour, 2 hours (respectively)
* Patient appointment management via email
    * Appointment cancellation
* Doctor schedule management
    * Vacation/leave handling
* User roles:
    * Receptionist
	* Doctor
	* Patient (interacts via email, no login required)

## Application Stack

The application is built using the following technologies:

* Frontend:
    * React --> Library for building the UI
    * Vite --> Build tool
    * Tailwind CSS --> CSS Framework
* Backend:
    * Java Spring Boot --> Framework for building the backend
    * PostgreSQL --> Database
    * Resend --> Email service provider for sending emails to patients (free tier)

## Project Structure

The project is structured as follows:

* `Docs/` --> Contains the documentation for the project (you are here)
* `Happy Face/` --> Contains the frontend code
* `Smile Base/` --> Contains the backend code
