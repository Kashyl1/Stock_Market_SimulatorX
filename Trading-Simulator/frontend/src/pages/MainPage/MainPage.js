import React from 'react';
import { Link } from 'react-router-dom';
import './MainPage.css';
import logo from '../../assets/stock_logov2.png';

const MainPage = () => {
  return (
    <div className="main">
      <div className="logo-container">
        <img src={logo} alt="Logo" className="logo" />
      </div>
      <div className="links">
        <Link to="/settings">User Settings</Link>
        <Link to="/wallet">Wallet</Link>
        <Link to="/currencies">Crypto</Link>
        <Link to="/portfolios">Go to Portfolios</Link>
      </div>
    </div>
  );
};

export default MainPage;
