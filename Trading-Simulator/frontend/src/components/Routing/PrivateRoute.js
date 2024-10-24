import React from 'react';
import { Navigate } from 'react-router-dom';

const PrivateRoute = ({ element: Component, isLoggedIn, ...rest }) => {
  const token = localStorage.getItem('jwtToken');
  return token ? <Component {...rest} /> : <Navigate to="/login" />;
};

export default PrivateRoute;
