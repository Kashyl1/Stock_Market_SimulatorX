import React from 'react';
import { Navigate } from 'react-router-dom';

const PublicRoute = ({ element: Component, isLoggedIn, ...rest }) => {
  return !isLoggedIn ? <Component {...rest} /> : <Navigate to="/main" />;
};

export default PublicRoute;
