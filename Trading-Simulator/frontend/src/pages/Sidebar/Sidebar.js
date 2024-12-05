import React, { useState, useEffect } from 'react';
import { NavLink } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import './Sidebar.css';
import logo from '../../assets/stock_logov2.png';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHome, faCoins, faWallet, faBriefcase, faCog, faBell, faHistory, faShield, faBars, faSignOutAlt } from '@fortawesome/free-solid-svg-icons';

const Sidebar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(true);
  const [isMobileView, setIsMobileView] = useState(window.innerWidth <= 768);

  const toggleMenu = () => setIsMenuOpen(!isMenuOpen);

  const handleLogout = () => {
    setIsLoggedIn(false);
    localStorage.removeItem('jwtToken');
    window.location.href = '/login';
  };

  const token = localStorage.getItem('jwtToken');
  let isAdmin = false;

  if (token) {
    try {
      const decoded = jwtDecode(token);
      isAdmin = decoded.role === 'ROLE_ROLE_ADMIN';
    } catch (error) {
      console.error('Error decoding JWT:', error);
    }
  }


  useEffect(() => {
    const handleResize = () => setIsMobileView(window.innerWidth <= 768);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return (
    <div className={`sidebar_main ${isMenuOpen ? 'menu-open' : ''}`}>
      <div className="logo-container">
        <NavLink to="/main">
          <img src={logo} alt="Logo" className="logo" />
        </NavLink>
      </div>

      <div className="hamburger" onClick={toggleMenu}>
        <FontAwesomeIcon icon={faBars} />
      </div>

      <div className={`menu-links ${isMenuOpen ? 'show' : ''}`}>
        <NavLink to="/main" className={({ isActive }) => (isActive ? 'active' : '')}>
          <FontAwesomeIcon icon={faHome} /> Main Page
        </NavLink>
        <NavLink to="/currencies" className={({ isActive }) => (isActive ? 'active' : '')}>
          <FontAwesomeIcon icon={faCoins} /> Crypto
        </NavLink>
        <NavLink to="/wallet" className={({ isActive }) => (isActive ? 'active' : '')}>
          <FontAwesomeIcon icon={faWallet} /> Wallet
        </NavLink>
        <NavLink to="/portfolios" className={({ isActive }) => (isActive ? 'active' : '')}>
          <FontAwesomeIcon icon={faBriefcase} /> Go to Portfolios
        </NavLink>
        <NavLink to="/alerts" className={({ isActive }) => (isActive ? 'active' : '')}>
          <FontAwesomeIcon icon={faBell} /> Notifications
        </NavLink>
        <NavLink to="/history" className={({ isActive }) => (isActive ? 'active' : '')}>
          <FontAwesomeIcon icon={faHistory} /> History
        </NavLink>
        <NavLink to="/settings" className={({ isActive }) => (isActive ? 'active' : '')}>
          <FontAwesomeIcon icon={faCog} /> User Settings
        </NavLink>
        {isAdmin && (
          <NavLink to="/adminpageusers" className={({ isActive }) => (isActive ? 'active' : '')}>
            <FontAwesomeIcon icon={faShield} /> Admin Panel
          </NavLink>
        )}
        {isMobileView && (
          <button className="nav-button" onClick={handleLogout}>
            <FontAwesomeIcon icon={faSignOutAlt} /> Logout
          </button>
        )}
      </div>
    </div>
  );
};

export default Sidebar;
