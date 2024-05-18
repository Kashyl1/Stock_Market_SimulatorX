import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import RegisterForm from '../components/Auth/RegisterForm';
import { initParticles } from '../assets/customParticles';

const RegisterPage = () => {
  useEffect(() => {
    initParticles();
  }, []);

  return (
    <div className="auth-page">
      <div className="static-background"></div>
      <div className="home-link">
        <Link to="/">Home</Link>
      </div>
      <RegisterForm />
      <canvas className="background"></canvas>
    </div>
  );
};

export default RegisterPage;
