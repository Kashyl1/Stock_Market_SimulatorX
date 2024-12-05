import React, { useState, useEffect } from 'react';
import { NavLink } from 'react-router-dom';
import '../../pages/Sidebar/Sidebar.css';
import logo from '../../assets/stock_logov2.png';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHome, faUsers, faWallet, faBriefcase, faCog, faFolderOpen, faMoneyCheckAlt, faBars, faSignOutAlt } from '@fortawesome/free-solid-svg-icons';

const AdminSidebar = () => {
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
        <NavLink to="/adminpageusers" className={({ isActive }) => (isActive ? 'active' : '')}>
          <FontAwesomeIcon icon={faUsers} /> Users Page
        </NavLink>
        <NavLink to="/admintransactions" className={({ isActive }) => (isActive ? 'active' : '')}>
          <FontAwesomeIcon icon={faMoneyCheckAlt} /> Users Transactions
        </NavLink>
        <NavLink to="/adminportfolios" className={({ isActive }) => (isActive ? 'active' : '')}>
          <FontAwesomeIcon icon={faFolderOpen} /> Users Portfolios
        </NavLink>
        <NavLink to="/main" className={({ isActive }) => (isActive ? 'active' : '')}>
          <FontAwesomeIcon icon={faHome} /> Main Page
        </NavLink>
        {isMobileView && (
         <button className="nav-button" onClick={handleLogout}>
           <FontAwesomeIcon icon={faSignOutAlt} /> Logout
        </button>
        )}
      </div>
    </div>
  );
};

export default AdminSidebar;
