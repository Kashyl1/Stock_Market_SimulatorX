import React from 'react';
import { Link } from 'react-router-dom';
import './UserSettingsSidebar.css';
import { NavLink } from 'react-router-dom';

const UserSettingsSidebar = () => {
  return (
    <div className="settings-sidebar">
      <h2>User Settings</h2>
      <ul className="settings-links">
        <li>
          <NavLink
            to="/settings/change-password"
            className={({ isActive }) => (isActive ? 'active' : '')}
          >
            Change Password
          </NavLink>
        </li>
        <li>
          <NavLink
            to="/settings/delete-account"
            className={({ isActive }) => (isActive ? 'active' : '')}
          >
            Delete Account
          </NavLink>
        </li>
        <li>
          <NavLink
            to="/settings/change-email"
            className={({ isActive }) => (isActive ? 'active' : '')}
          >
            Change Email
          </NavLink>
        </li>
      </ul>
    </div>


  );
};

export default UserSettingsSidebar;
