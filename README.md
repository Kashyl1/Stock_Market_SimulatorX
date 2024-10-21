# Stock Market Simulator

This project is a **Stock Market Simulator** that allows users to simulate cryptocurrency trading and manage their portfolios. The application provides various features for managing user accounts, wallets, and assets, giving a full simulation experience.

## Features

### Authentication
- **User Registration:** Allows new users to create an account.
- **User Login:** Allows existing users to log in.
- **Email Verification:** Sends a verification email to the user upon registration to confirm their email address.

### Cryptocurrency Trading
- **Buy Cryptocurrencies:** Users can now purchase cryptocurrencies within the simulator using virtual funds.
- **Create Wallets:** Allows users to create and manage multiple cryptocurrency wallets.
- **Add Funds:** Users can add virtual funds to their accounts for trading purposes.
- **View Current Prices:** Displays real-time prices of available cryptocurrencies, allowing users to monitor the market.

### Portfolio Management
- **Manage Portfolio:** Users can view and manage their cryptocurrency investments, tracking their portfolio performance.
- **Transaction History:** Keeps a log of all transactions, including purchases and wallet funding.

### User Settings
- **Update User Profile:** Users can update their personal details and configure settings in their accounts.

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
   git clone https://github.com/Kashyl1/Stock_Market_SimulatorX
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





   
   
