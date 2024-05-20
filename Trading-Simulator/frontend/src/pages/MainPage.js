import React from 'react';
import { Link } from 'react-router-dom';

const MainPage = () => {
  return (
    <div>
      <h1>Main Page</h1>
      <Link to="/settings">Go to User Settings</Link>
    </div>
  );
};

export default MainPage;
