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
 /*
 Uncaught runtime errors:
 ×
 ERROR
 Cannot read properties of null (reading 'offsetParent')
 TypeError: Cannot read properties of null (reading 'offsetParent')
     at new i (http://localhost:3000/static/js/bundle.js:50227:13)
     at n._initializeStorage (http://localhost:3000/static/js/bundle.js:50138:63)
     at n._refresh (http://localhost:3000/static/js/bundle.js:50164:10)
     at http://localhost:3000/static/js/bundle.js:50168:31
*/