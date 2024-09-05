# Entity Models

## Overview

based on the requirements, the following entities are identified:

* Patient
* Appointment
* AppointmentType
* Doctor
* User

## Entity Details

### Patient

* Attributes:
    * id: string
    * name: string
    * email: string
    * phone: string
    * dateOfBirth: date

### AppointmentType

* Attributes:
    * id: string
    * name: string
    * duration: number

### Appointment

* Attributes:
    * id: string
    * patientId: string
    * doctorId: string
    * appointmentTypeId: string
    * date: date
    * time: time

### Doctor

* Attributes:
    * id: string
    * name: string
    * email: string
    * phone: string

### User

* Attributes:
    * id: string
    * username: string
    * password: string
    * role: string