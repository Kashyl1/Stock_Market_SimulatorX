import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import RegisterForm from '../../components/Auth/RegisterForm';
import CustomParticlesBackground from '../../assets/CustomParticlesBackground';

const RegisterPage = () => {

  return (
    <div className="auth-page">
    <CustomParticlesBackground />
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