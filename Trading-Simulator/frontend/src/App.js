import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import VerificationPage from './pages/VerificationPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/HomePage';
import MainPage from './pages/MainPage';
import UserSettingsPage from './pages/UserSettingsPage';
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
    useEffect(() => {
        const token = localStorage.getItem('jwtToken');
        setIsLoggedIn(!!token);
    }, []);

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
                    <Route path="/login" element={<LoginPage setIsLoggedIn={setIsLoggedIn} />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/main" element={<MainPage />} />
                    <Route path="/settings" element={<UserSettingsPage />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
