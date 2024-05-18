import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import DemoPage from './pages/DemoPage';
import VerificationPage from './pages/VerificationPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/HomePage';
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
                <nav>
                    {isLoggedIn && <button onClick={handleLogout}>Logout</button>}
                    {isLoggedIn && <Link to="/demo">Demo</Link>}
                </nav>
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/verify" element={<VerificationPage />} />
                    <Route path="/login" element={<LoginPage setIsLoggedIn={setIsLoggedIn} />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/demo" element={<DemoPage />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
