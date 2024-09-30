# Entity Models

## Overview

based on the requirements, the following entities are identified:

* Patient
* Appointment
* User
* Enum Role
* Enum AppointmentType

## Entity Details

### Patient

* Attributes:
    * id: string
    * name: string
    * email: string
    * phone: string
    * dateOfBirth: date
    * allergies: string
    * medicalHistory: string


### Appointment

* Attributes:
    * id: string
    * title: string
    * patientId: string
    * doctorId: string
    * appointmentType: AppointmentType
    * start: datetime
    * end: datetime
    * reasonForAppointment: string
    * notes: string


### User

* Attributes:
    * id: string
    * username: string
    * email: string
    * password: string
    * role: Role
    * active: bool

### Enum Role

* Attributes:
    RECEPTIONIST, DOCTOR, ADMIN, PATIENT

    
### Enum AppointmentType

* Attributes:
   QUICKCHECK -> 30
   EXTENSIVE -> 60
   SURGERY -> 120