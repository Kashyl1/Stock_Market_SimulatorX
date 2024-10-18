import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import LoginForm from '../../components/Auth/LoginForm';
import CustomParticlesBackground from '../../assets/CustomParticlesBackground';
import logo from '../../assets/stock_logov2.png';

const LoginPage = ({ setIsLoggedIn }) => {
  return (
    <div className="auth-page">
    <CustomParticlesBackground />
      <div className="static-background"></div>
      <div className="home-button">
        <Link to="/">Home</Link>
      </div>
      <LoginForm setIsLoggedIn={setIsLoggedIn} />
      <canvas className="background"></canvas>
    </div>
  );
};

export default LoginPage;
