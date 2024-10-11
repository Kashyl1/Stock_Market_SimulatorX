import React from 'react';
import { Link } from 'react-router-dom';
import './MainPage.css';

const MainPage = () => {
  return (
    <div className="main">
      <Link to="/settings">User Settings</Link>
      <Link to="/wallet">Wallet</Link>
      <Link to="/currencies">Crypto</Link>
      <Link to="/portfolios">Go to Portfolios</Link>
    </div>
  );
};

export default MainPage;
