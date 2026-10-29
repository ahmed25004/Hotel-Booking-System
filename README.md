# Hotel Booking Management System

A Java desktop application designed to manage hotel rooms and reservations through an interactive graphical user interface built with **Java Swing**.

The system supports room management, reservation scheduling, availability checking, booking searches, statistics, and local data persistence.

## Features

### Room Management
- Add new hotel rooms with room numbers, types, prices, and statuses.
- Edit existing room information.
- Delete rooms while protecting active future reservations.
- Support three room categories: Single, Double, and Suite.

### Booking Management
- Create reservations with guest information, room number, check-in and check-out dates, and optional notes.
- Edit existing active reservations.
- Cancel reservations while preserving booking history.
- Prevent overlapping active reservations for the same room.
- Search bookings by guest name, room number, and check-in date range.

### Statistics
- Calculate available rooms by room type for a selected date.
- Calculate total active booked nights within the current month.

### Data Persistence
- Save hotel rooms and reservations to a local file.
- Load previously saved data.
- Use Java object serialization for persistent storage.

## Technologies

- **Java 17+**
- **Java Swing** — Desktop graphical user interface
- **Maven** — Build and dependency management
- **JUnit 4** — Unit testing
- **Java Collections Framework** — In-memory room and booking management
- **Java Serialization** — File-based data persistence

## Project Structure

```text
src/
├── main/java/com/hotelbooking/
│   ├── model/
│   │   ├── Booking.java
│   │   ├── BookingStatus.java
│   │   ├── Room.java
│   │   ├── RoomStatus.java
│   │   └── RoomType.java
│   ├── service/
│   │   └── Hotel.java
│   ├── ui/
│   │   ├── BookingTableModel.java
│   │   ├── HotelBookingFrame.java
│   │   └── RoomTableModel.java
│   └── HotelApp.java
└── test/java/com/hotelbooking/
    └── HotelTest.java
```

The application is organized into three main layers:

- **Model:** Represents rooms, bookings, and their statuses.
- **Service:** Implements reservation management, business rules, statistics, and persistence.
- **UI:** Implements the Swing interface and table models.

`HotelApp.java` is the application entry point.

## Getting Started

### Prerequisites

- JDK 17 or later
- Apache Maven, or an IDE with Maven support
- IntelliJ IDEA (recommended)

### Run the Application

1. Clone the repository:

   ```bash
   git clone https://github.com/YOUR_USERNAME/Hotel-Booking-System.git
   ```

2. Open the project folder in IntelliJ IDEA.
3. Import the project as a Maven project if prompted.
4. Allow Maven to download the required dependencies.
5. Open `src/main/java/com/hotelbooking/HotelApp.java`.
6. Run the `main()` method.

The Hotel Booking System desktop interface will open.

### Run the Tests

Execute the following command from the project root:

```bash
mvn clean test
```

Alternatively, use IntelliJ IDEA's **Maven → Lifecycle → test** action.

## Testing

The project includes six JUnit 4 unit tests covering:

1. Successful booking creation.
2. Rejection of overlapping bookings.
3. Booking cancellation.
4. Booking searches by guest name and date.
5. Available room calculations.
6. Monthly booked-night statistics.

## Documentation

The original project documentation includes UML diagrams, sequence diagrams, application screenshots, and testing explanations.

[View Project Documentation](docs/documentation.pdf)

## Future Improvements

Potential improvements include stronger input validation, improved exception handling, more comprehensive automated testing, and further separation of persistence responsibilities.

## Project Background

Originally developed as a university Programming 3 project and subsequently reorganized into a modular Maven-based Java application.

## License

No license has been selected yet. All rights are reserved unless a license is added.
