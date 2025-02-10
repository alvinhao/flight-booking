# **Seat Reservation System**

## **Overview**
This project implements a **file-based seat reservation system** using Java. The system simulates a **database-like behavior** by storing seat reservations in a binary file (`seats.dat`). It supports:

- **Automatically initialize seats.dat if not exists**
- **Booking consecutive seats**
- **Canceling reservations**
- **Concurrency control**
- **Efficient bitwise operations** for memory optimization
- **Input validation**

## **Project Structure**
```
FlightBooking/
│── src/
│   ├── main/
│   │   │── java/
│   │   │   │── hao/
│   │   │   │   │── alvin/
│   │   │   │   │   │── chase/
│   │   │   │   │   │   ├── BookingSystem.java  # Main CLI program
│   │   │   │   │   │   ├── SeatManager.java  # Manages seat reservations & ensures data integrity
│   │   │   │   │   │   ├── DataAccess.java  # Handles reading/writing seat data to file
│   ├── tests/
│   │   │── java/
│   │   │   │── hao/
│   │   │   │   │── alvin/
│   │   │   │   │   │── chase/
│   │   │   │   │   │   ├── BookingSystemTest.java  # Unit tests for Main CLI program
│   │   │   │   │   │   ├── DataAccessTest.java  # Unit tests for file operations
│   │   │   │   │   │   ├── SeatManagerTest.java  # Unit tests for seat management
│── build.gradle  # Gradle build file
│── README.md  # Project documentation
```

---
## **How It Works**
### **1 Seat Storage Format**
Each row is stored as a **single byte (8 bits)** where each **bit represents a seat**:
```
Bit position:   7  6  5  4  3  2  1  0
Seats:         S8 S7 S6 S5 S4 S3 S2 S1
```
- `0` → Available seat
- `1` → Booked seat

### **2 File Handling**
- `DataAccess.java` manages **binary file operations**.
- The **file persists seat reservations** across multiple runs.

### **3 Concurrency Handling**
- Though not specifically required, in a real collaborative project SeatManager class maybe used by other classes. 
- To prevent race condition, it's created following singleton pattern.  
- And added **Row-level locks**.

---
## **Build & Run Instructions**
### **🔹 Build the Project (Gradle)**
Run the following command to build the project. 
It creates a flight_booking.sh shell script for users to execute the program. 
```sh
./gradlew build
```
### **🔹 Running the Program**
Use the provided **shell script** to execute the reservation system:
```sh
./flight_booking.sh BOOK A1 3
./flight_booking.sh CANCEL A1 3
```
---
## **Testing**
### **🔹 Run Unit Tests**
Execute all tests with:
```sh
./gradlew test
```

### **🔹 Test Coverage**
| **Test Suite**        | **Purpose** |
|----------------------|------------|
| `SeatManagerTest`    | Tests seat booking & cancellations |
| `DataAccessTest`     | Tests file read/write operations |
| `BookingSystemTest`  | Tests CLI argument handling |
---
