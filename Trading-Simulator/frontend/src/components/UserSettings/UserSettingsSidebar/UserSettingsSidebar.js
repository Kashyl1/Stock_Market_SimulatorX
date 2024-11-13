import React from 'react';
import { Link } from 'react-router-dom';
import './UserSettingsSidebar.css';

const UserSettingsSidebar = () => {
  return (
    <div className="sidebar">
      <h2>User Settings</h2>
      <ul className="settings-links">
        <li><Link to="/settings/change-password">Change Password</Link></li>
        <li><Link to="/settings/delete-account">Delete Account</Link></li>
        <li><Link to="/settings/change-email">Change Email</Link></li>
      </ul>
    </div>

  );
};

export default UserSettingsSidebar;
