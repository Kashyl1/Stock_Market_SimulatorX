import React from 'react';
import { NavLink } from 'react-router-dom';
import '../../pages/Sidebar/Sidebar.css';
import logo from '../../assets/stock_logov2.png';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHome, faUsers, faWallet, faBriefcase, faCog, faBell, faHistory } from '@fortawesome/free-solid-svg-icons';

const AdminSidebar = () => {
    return (
            <div className="sidebar_main">
              <div className="logo-container">
                <NavLink to="/main">
                  <img src={logo} alt="Logo" className="logo" />
                </NavLink>
              </div>
              <div className="menu-links">
                <NavLink to="/adminpageusers" className={({ isActive }) => (isActive ? 'active' : '')}>
                  <FontAwesomeIcon icon={faUsers} /> Users Page
                </NavLink>
                <NavLink to="/main" className={({ isActive }) => (isActive ? 'active' : '')}>
                  <FontAwesomeIcon icon={faHome} /> Main Page
                </NavLink>
              </div>
            </div>
    );
};

export default AdminSidebar;
