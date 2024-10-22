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
### Installation

1. **Pull the latest container from Docker Hub:**

   To download the latest image of your application from Docker Hub, run the following command:

   ```bash
   docker pull kashylt/trading-simulator:latest

2. **Create the application.properties file:**
    The example is here: Trading-Simulator/backend/src/main/resources/application.example.properties

3. **Set up PostgreSQL database:**
Ensure that PostgreSQL is running and accessible for the application. If you want to run the database in a Docker container, use the following steps:
Run the PostgreSQL container:
```
   docker run -d \
  --name postgres \
  -e POSTGRES_DB=trading_simulator_local \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=123 \
  -p 5432:5432 \
  postgres:14
```
4. **Run the application container with volume:**
To run the application container while mounting the application.properties file, use the following command:

```
docker run -d \
  --name trading-simulator \
  --link postgres:postgres \
  -p 8080:8080 \
  -v /path/to/your/application.properties:/app/config/application.properties \
  kashylt/trading-simulator:latest
```
5. Access the application:
  ```
   The application should now be available at http://localhost:8080
```

### Configuration
   The application will automatically serve both the backend and frontend from the same container.
Adjust the settings in the application.properties file as needed to suit your requirements.
  
 -*Email Configuration:*
   Update the email service configuration in the backend to ensure email functionality works.

### Contributing
   Contributions are welcome! Please fork the repository and create a pull request with your changes.

### License
   This project is licensed under the MIT License. See the LICENSE file for details.





   
   
