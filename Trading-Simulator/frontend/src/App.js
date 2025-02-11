import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import LoginPage from './pages/Auth/LoginPage';
import VerificationPage from './pages/Auth/VerificationPage';
import RegisterPage from './pages/Auth/RegisterPage';
import HomePage from './pages/StartPage/StartPage';
import MainPage from './pages/MainPage/MainPage';
import UserSettingsPage from './pages/UserSettings/UserSettingsPage';
import PrivateRoute from './components/Routing/PrivateRoute';
import PublicRoute from './components/Routing/PublicRoute';
import WalletPage from './pages/Wallet/WalletPage';
import CurrenciesPage from './pages/Currencies/CurrenciesPage';
import PortfoliosPage from './pages/PortfoliosPage/PortfoliosPage';
import PortfolioDetails from './components/Portfolios/PortfolioDetails/PortfolioDetails';
import ResetPasswordForm from './components/Auth/ResetPasswordForm/ResetPasswordForm';
import Contact from './components/Footer/Contact';
import PrivacyPolicy from './components/Footer/PrivacyPolicy';
import AlertsPage from './pages/AlertsPage/AlertsPage';
import HistoryPage from './pages/HistoryPage/HistoryPage';
import AdminPageUsers from './pages/AdminPageUsers/AdminPageUsers';
import AdminTransactionsPage from './pages/AdminTransactionsPage/AdminTransactionsPage';
import AdminPortfoliosPage from './pages/AdminPortfoliosPage/AdminPortfoliosPage';
import AdminEventsPage from './pages/AdminEventsPage/AdminEventsPage';
import AdminAlertsPage from './pages/AdminAlertsPage/AdminAlertsPage';
import Footer from './components/Footer/Footer'

import axios from 'axios';

import './App.css';

axios.interceptors.request.use(config => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userRole, setUserRole] = useState(false);
useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        const decoded = jwtDecode(token);
        setUserRole(decoded.role);
        setIsLoggedIn(true);
    } else {
        setIsLoggedIn(false);
        setUserRole(null);
    }
}, [isLoggedIn], [userRole] );


    const handleLogout = () => {
        localStorage.removeItem('jwtToken');
        setIsLoggedIn(false);
        window.location.href = '/login';
    };

    return (
        <Router>
            <div className="App">
                {isLoggedIn && (
                    <nav className="navbar">
                        <button onClick={handleLogout} className="nav-button">Logout</button>
                    </nav>
                )}
                <Routes>
                    <Route path="/" element={isLoggedIn ? <Navigate to="/main" /> : <HomePage />} />
                    <Route path="/verify" element={<VerificationPage />} />
                    <Route path="/login" element={<PublicRoute element={LoginPage} isLoggedIn={isLoggedIn} setIsLoggedIn={setIsLoggedIn} />} />
                    <Route path="/register" element={<PublicRoute element={RegisterPage} isLoggedIn={isLoggedIn} />} />
                    <Route path="/reset-password" element={<ResetPasswordForm />} />
                    <Route path="/main" element={<PrivateRoute element={MainPage} isLoggedIn={isLoggedIn} />} />
                    <Route path="/settings/*" element={<PrivateRoute element={() => <UserSettingsPage setIsLoggedIn={setIsLoggedIn} />} isLoggedIn={isLoggedIn} />} />
                    <Route path="/wallet" element={<PrivateRoute element={WalletPage} isLoggedIn={isLoggedIn} />} />
                    <Route path="/currencies" element={<PrivateRoute element={CurrenciesPage} isLoggedIn={isLoggedIn} />} />
                    <Route path="/portfolios" element={<PrivateRoute element={PortfoliosPage} isLoggedIn={isLoggedIn} />} />
                    <Route path="/portfolios/:id" element={<PortfolioDetails />} />
                    <Route path="/alerts" element={<PrivateRoute element = {AlertsPage} isLoggedIn={isLoggedIn} />} />
                    <Route path="/history" element={<PrivateRoute element = {HistoryPage} isLoggedIn={isLoggedIn} />} />
                    <Route path="/contact" element={<Contact />} />
                    <Route path="/privacypolicy" element={<PrivacyPolicy />} />
                    <Route path="/adminpageusers" element={<PrivateRoute element={AdminPageUsers} isLoggedIn={isLoggedIn} requiredRole="ROLE_ROLE_ADMIN"  />}  />
                    <Route path="/admintransactions" element={<PrivateRoute element={AdminTransactionsPage} isLoggedIn={isLoggedIn} requiredRole="ROLE_ROLE_ADMIN"  />}  />
                    <Route path="/adminportfolios" element={<PrivateRoute element={AdminPortfoliosPage} isLoggedIn={isLoggedIn} requiredRole="ROLE_ROLE_ADMIN"  />}  />
                    <Route path="/adminevents" element={<PrivateRoute element={AdminEventsPage} isLoggedIn={isLoggedIn} requiredRole="ROLE_ROLE_ADMIN"  />}  />
                    <Route path="/adminalerts" element={<PrivateRoute element={AdminAlertsPage} isLoggedIn={isLoggedIn} requiredRole="ROLE_ROLE_ADMIN"  />}  />
                </Routes>
                <Footer/>
            </div>
        </Router>
    );
}

export default App;