# Ride Booking System – Backend & Demo UI

## Overview:
### This project is a simplified multi-tenant ride-hailing platform inspired by Current Ride Booing Apps (uber/ola). It supports:

* Real-time driver availability
* Driver–rider matching
* Trip lifecycle management
* Idempotent payments
* Concurrency-safe operations

## HLD:
### 1. Core Components
* **Ride Service:** Handles ride creation and matching
* **Driver Service:** Manages driver state and ride acceptance
* **Trip Service:**  Manages trip lifecycle (start/end)
* **Payment Service:** Handles idempotent payments
* **PostgreSQL:** Transactional datastore
* **React UI:** Lifecycle demo frontend

### 2. Architecture Overview
* I followed below pattern for Reliability and Readability of Code

                       React UI
                         ↓
                 Spring Boot REST APIs
                          ↓
              Service Layer (Transactional)
                          ↓
        Repository (PostgreSQL for ,Row Locking + Optimistic Locking)
* Matching and state transitions are handled synchronously inside transactions to guarantee correctness

### 3. Key Design Decision
* **Pessimistic Locking:** Used during Driver assignment, Ride acceptance, Trip start and Payment processing. This prevents race from conditions.
* **Optimistic Locking:** This prevents from lost updates. All major entities use this like ride, driver and trip.

      @version
*  **Idempotency:** This ensures no duplicate ride or payment creation. Used in header.
* **Clean State Machine(enums):** Ride states, Driver states, Trip states and Payment states.

## LLD:
### 1. Entities
* In Driver handles the following areas, Maintains availability, Tracks current ride, Embedded vehicle, location and Optimistic locking enabled.
* In Ride handles the following areas, Stores ride lifecycle, Tracks assigned driver, Stores estimated, final fare and Idempotency key unique.
* In Trip handles the following areas, Created only after driver accepts, Stores duration and fare.
* In Payment handles the following areas, Linked to ride Idempotent, Prevents double payment.

### 2. Matching Algo
* Matching should be atomic (transactional safe), so when a ride is created the lock available to the drivers then select first available (ordered by creation time) after that driver status should be RESERVED and ride status should be OFFERED.

### 3. Concurrency Handling
* It handles via @Transactional, @Lock(LockModeType.PESSIMISTIC_WRITE) and @Version annotations. It prevents from multiple trips start simultaneously by same driver.


## API-FLOW (UI+Manually(localhost)):
* First start the server.
* First create the driver for taking the rides.

      POST /v1/drivers  
* Now driver status should be AVAILABLE.
* Then update the driver location (driver-id generate in above response).

      POST /v1/drivers/{driver-id}/location
* Then go-to browser then click the button "create ride" then driver status will be "RESERVED" and ride status will be "OFFERED" or "REQUESTED" (in case when driver is not available).
* Then driver will accept the ride and then trip is now "ONGOING".
* Then after drop driver will end the ride, now trip status is "COMPLETED".
* Final step payment collection.


### Start the backend server

    mvn spring-boot:run
### Start the frontend server

    npm install
    npm run dev


