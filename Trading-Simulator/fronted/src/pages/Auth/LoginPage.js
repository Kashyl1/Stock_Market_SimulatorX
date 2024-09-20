import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import LoginForm from '../../components/Auth/LoginForm';
import { initParticles } from '../../assets/customParticles';

const LoginPage = ({ setIsLoggedIn }) => {
  useEffect(() => {
    initParticles();
  }, []);

  return (
    <div className="auth-page">
      <div className="static-background"></div>
      <div className="home-link">
        <Link to="/">Home</Link>
      </div>
      <LoginForm setIsLoggedIn={setIsLoggedIn} />
      <canvas className="background"></canvas>
    </div>
  );
};

export default LoginPage;
