# Stock Market Simulator

This project is a Stock Market Simulator currently under development. The project aims to provide users with the ability to simulate stock market trading.

## Features

### Authentication
- **User Registration:** Allows new users to create an account.
- **User Login:** Allows existing users to log in.
- **Email Verification:** Sends a verification email to the user upon registration to confirm their email address.

### Future Features
- **Market Simulation:** Simulate stock market trading with real-time data.
- **Portfolio Management:** Manage a portfolio of simulated investments.
- **Transaction History:** Track all transactions and portfolio changes.
- **Alerts and Notifications:** Set up alerts for specific market conditions and receive notifications.
- **Project of Database:** [Database.pdf](https://github.com/Kashyl1/Stock_Market_SimulatorX/files/15365477/Database.pdf)



## Technologies Used

- **Backend:**
  - Java Spring Boot
  - Spring Security
  - JWT for authentication
  - JPA/Hibernate for ORM
  - Lombok for boilerplate code reduction
  - Mockito and JUnit for testing

- **Frontend:**
  - React.js
  - Axios for HTTP requests
  - React Router for routing
  - FontAwesome for icons

- **Database:**
  - PostgreSQL

- **Email:**
  - MailDev for email testing

## Getting Started

### Prerequisites

- Java 11 or higher
- Node.js and npm
- PostgreSQL
- MailDev

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/stock-market-simulator.git
   cd stock-market-simulator
   ```
2. **Backend Setup:**
   -Navigate to the backend directory:
   ```
     cd backend
   ```
   -Install dependencies and run the application:
   ```
   ./mvnw spring-boot:run
   ```
3. **Frontend Setup:**
   -Navigate to the frontend directory.
   ```
   cd frontend
   ```
   -Install dependencies and start the development server:
   ```
   npm install
   npm start
   ```
### Configuration
   -*Database Configuration:*
   Update the application.properties file in the backend with your PostgreSQL database credentials.
   -*Email Configuration:*
   Update the email service configuration in the backend to ensure email functionality works.

### Contributing
   Contributions are welcome! Please fork the repository and create a pull request with your changes.

### License
   This project is licensed under the MIT License. See the LICENSE file for details.





   
   
