import React from 'react';
import { NavLink } from 'react-router-dom';
import './Sidebar.css';
import logo from '../../assets/stock_logov2.png';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHome, faCoins, faWallet, faBriefcase, faCog, faBell, faHistory } from '@fortawesome/free-solid-svg-icons';

const Sidebar = () => {
    return (
            <div className="sidebar_main">
              <div className="logo-container">
                <NavLink to="/main">
                  <img src={logo} alt="Logo" className="logo" />
                </NavLink>
              </div>
              <div className="menu-links">
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
              </div>
            </div>

    );
};

export default Sidebar;
