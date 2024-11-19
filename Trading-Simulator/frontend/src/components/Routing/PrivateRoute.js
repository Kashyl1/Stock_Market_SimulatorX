import React from 'react';
import { Navigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

const PrivateRoute = ({ element: Component, isLoggedIn, requiredRole, ...rest }) => {
  const token = localStorage.getItem('jwtToken');

  if (!token) {
    return <Navigate to="/login" />;
  }

  const decoded = jwtDecode(token);
  const userRole = decoded.role;

  if (requiredRole && userRole !== requiredRole) {
    return <Navigate to="/" />;
  }

  return <Component {...rest} />;
};

export default PrivateRoute;
