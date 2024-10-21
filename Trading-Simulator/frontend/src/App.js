import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
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

import axios from 'axios';

import './App.css';

axios.interceptors.request.use(config => {
    const token = localStorage.getItem('jwtToken');
    console.log('Token during request:', token);
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    useEffect(() => {
        const token = localStorage.getItem('jwtToken');
        setIsLoggedIn(!!token);
    }, [isLoggedIn]);

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
                        <Link to="/main" className="nav-link">Main Page</Link>
                        <button onClick={handleLogout} className="nav-button">Logout</button>
                    </nav>
                )}
                <Routes>
                    <Route path="/" element={isLoggedIn ? <Navigate to="/main" /> : <HomePage />} />
                    <Route path="/verify" element={<VerificationPage />} />
                    <Route path="/login" element={<PublicRoute element={LoginPage} isLoggedIn={isLoggedIn} setIsLoggedIn={setIsLoggedIn} />} />
                    <Route path="/register" element={<PublicRoute element={RegisterPage} isLoggedIn={isLoggedIn} />} />
                    <Route path="/main" element={<PrivateRoute element={MainPage} isLoggedIn={isLoggedIn} />} />
                    <Route path="/settings/*" element={<PrivateRoute element={UserSettingsPage} isLoggedIn={isLoggedIn} />} />
                    <Route path="/wallet" element={<PrivateRoute element={WalletPage} isLoggedIn={isLoggedIn} />} />
                    <Route path="/currencies" element={<PrivateRoute element={CurrenciesPage} isLoggedIn={isLoggedIn} />} />
                    <Route path="/portfolios" element={<PrivateRoute element={PortfoliosPage} isLoggedIn={isLoggedIn} />} />
                    <Route path="/portfolios/:id" element={<PortfolioDetails />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
